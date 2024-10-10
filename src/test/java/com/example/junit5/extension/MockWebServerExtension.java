package com.example.junit5.extension;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.Optional;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.ReflectionUtils;

public class MockWebServerExtension  implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
  private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MockWebServerExtension.class);
  private static final String INSTANCE_KEY = "mockWebServer";
  private static final String PORT_KEY = "port";
  private static final String URI_KEY = "uri";

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    MockWebServer mockWebServer = new MockWebServer();
    mockWebServer.start();
    int port = mockWebServer.getPort();
    String uri = mockWebServer.url("").toString();

    ExtensionContext.Store store = context.getStore(NAMESPACE);
    store.put(INSTANCE_KEY, mockWebServer);
    store.put(PORT_KEY, port);
    store.put(URI_KEY, uri);

    for (Object instance : context.getRequiredTestInstances().getAllInstances()) {
      ReflectionSupport.streamFields(instance.getClass(), field -> true, HierarchyTraversalMode.TOP_DOWN)
        .forEach(field -> parameter(store, field, field.getType()).ifPresent(it -> setField(instance, field, it)));
    }
  }

  private static void setField(Object instance, Field field, Object value) {
    try {
      ReflectionUtils.makeAccessible(field).set(instance, value);
    }
    catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    ExtensionContext.Store store = context.getStore(NAMESPACE);
    MockWebServer mockWebServer = store.get(INSTANCE_KEY, MockWebServer.class);
    mockWebServer.shutdown();
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    throws ParameterResolutionException {
    Parameter parameter = parameterContext.getParameter();
    Class<?> type = parameter.getType();
    return isMockWebServer(type) || isPort(parameter, type) || isUri(parameter, type);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
    throws ParameterResolutionException {
    Class<?> type = parameterContext.getParameter().getType();
    return parameter(extensionContext.getStore(NAMESPACE), parameterContext.getParameter(), type)
      .orElseThrow(() -> new ParameterResolutionException("Unsupported parameter type: " + type));
  }

  private static Optional<Object> parameter(ExtensionContext.Store store, AnnotatedElement annotatedElement, Class<?> type) {
    Object result;
    if (isMockWebServer(type)) {
      result = store.get(INSTANCE_KEY, MockWebServer.class);
    }
    else if (isPort(annotatedElement, type)) {
      result = store.get(PORT_KEY, Integer.class);
    }
    else if (annotatedElement.isAnnotationPresent(Uri.class) && type.equals(String.class)) {
      result = store.get(URI_KEY, String.class);
    }
    else if (annotatedElement.isAnnotationPresent(Uri.class) && type.equals(URI.class)) {
      result = URI.create(store.get(URI_KEY, String.class));
    }
    else {
      result = null;
    }
    return Optional.ofNullable(result);
  }

  private static boolean isMockWebServer(Class<?> type) {
    return type.equals(MockWebServer.class);
  }

  private static boolean isUri(AnnotatedElement annotatedElement, Class<?> type) {
    return annotatedElement.isAnnotationPresent(Uri.class) && (type.equals(String.class) || type.equals(URI.class));
  }

  private static boolean isPort(AnnotatedElement annotatedElement, Class<?> type) {
    return annotatedElement.isAnnotationPresent(Port.class) && (type.equals(int.class) || type.equals(Integer.class));
  }
}
