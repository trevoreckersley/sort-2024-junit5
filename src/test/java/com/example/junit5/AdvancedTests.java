/*
 * (c) 2024 by Intellectual Reserve, Inc. All rights reserved.
 */

package com.example.junit5;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.example.junit5.extension.MockableWeb;
import com.example.junit5.extension.Port;
import com.example.junit5.extension.Uri;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(2)
@MockableWeb
public class AdvancedTests {

  @BeforeEach
  void beforeEach(MockWebServer server, @Port int port, @Uri String uri) {
    System.out.printf("%s started on port %d with URI %s%n", server, port, uri);
  }

  @Test
  void works(MockWebServer server, @Uri URI uri) throws IOException, InterruptedException {
    String data = "Hello, world!";
    server.enqueue(new MockResponse().setBody(data));

    String body = HttpClient.newHttpClient()
      .send(
        HttpRequest.newBuilder().GET().uri(uri).build(),
        HttpResponse.BodyHandlers.ofString())
      .body();

    assertEquals(data, body);
  }

  @Nested
  class FieldTests {
    MockWebServer server;
    @Uri URI uri;
    @Port int port;

    @Test
    void test(MockWebServer server, @Uri String uri, @Port Integer port) {
      assertSame(this.server, server);
      assertEquals(this.uri.toString(), uri);
      assertEquals(this.port, port);
    }
  }

}
