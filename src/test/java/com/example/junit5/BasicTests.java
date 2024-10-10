package com.example.junit5;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.junit.jupiter.api.DynamicContainer.*;
import static org.junit.jupiter.api.DynamicTest.*;

//@Test
//@ParameterizedTest
//@RepeatedTest
//@TestFactory
//@TestTemplate--
//@TestClassOrder
//@TestMethodOrder
//@TestInstace
//@DisplayName
//@DisplayNameGeneration
//@BeforeEach/@AfterEach
//@BeforeAll/@AfterAll
//@Nested
//@Tag
//@Disabled
//@AutoClose
//@Timeout
//@TempDir
//@ExtendWith/@RegisterExtension
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.Random.class)
@Order(1)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("My Awesome Test Suite 🙈")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BasicTests {
  private static final AtomicInteger COUNTER = new AtomicInteger();

  @BeforeAll
  static void beforeAll() {
    System.out.println("Before all tests");
  }

  @BeforeEach
  void beforeEach() {
    System.out.println("Before each test: " + COUNTER.incrementAndGet());
  }

  @AfterEach
  void afterEach() {
    System.out.println("After each test: " + COUNTER.incrementAndGet());
  }

  @AfterAll
  static void afterAll() {
    System.out.println("After all tests");
  }

  @Tag("Essential")
  @Test
  void my_special_test() {
    assertTrue("Hello world!".length() > 0);
  }

  @Test
  @Disabled("For some reason, this test won't pass...")
  void butWhy() {
    fail();
  }

  @ParameterizedTest
  @EnumSource(value = Desktop.Action.class)
  void actionTest(Desktop.Action action) {
    assertNotNull(action);
  }

  @RepeatedTest(5)
  void repeatedTest() {
    assertTrue(COUNTER.getAndIncrement() % 2 == 0);
  }

  @TestFactory
  List<DynamicNode> testFactory() {
    return List.of(
      dynamicTest("1st dynamic test", () -> assertTrue(true)),
      dynamicTest("2nd dynamic test", () -> assertEquals(4, 2 * 2)),
      dynamicContainer("Dynamic container", List.of(
        dynamicTest("3rd dynamic test", () -> assertTrue(true)),
        dynamicTest("4th dynamic test", () -> assertEquals(4, 2 * 2))
      ))
    );
  }

  @Test
  @Timeout(2)
  void longRunningTest() throws InterruptedException {
    Thread.sleep(5000);
    assertTrue(true);
  }

  @Test
  @EnabledForJreRange(min = JRE.JAVA_11)
  void onlyOnJava11Plus() {
    assertTrue(true);
  }

  @Test
  @EnabledOnOs(OS.SOLARIS)
  void onlySolaris() {
    assertTrue(true);
  }

  @Test
  void my_false_assumptions() {
    assumeTrue(ThreadLocalRandom.current().nextLong() == 0);
    fail("You lucky duck! It almost never is 0!");
  }

  @Test
  void my_true_assumptions() {
    assertTrue(LocalDate.now().isAfter(LocalDate.EPOCH));
    assertTrue(true);
    assumingThat(true, () -> assertTrue(true));
    assumingThat(false, () -> fail("This should not happen"));
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_METHOD)
  class NestedTests {
    @TempDir
    private Path tempDir;

    @AutoClose
    private BufferedWriter writer;

    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void beforeEach(@TempDir(cleanup = CleanupMode.NEVER) Path tempDir) throws IOException {
      System.out.println("In the nested test");
      Path path = tempDir.resolve(uuid.toString());
      System.out.println("Writing to " + path);
      writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE_NEW);
    }

    @Test
    void nestedTest(TestInfo testInfo) throws IOException {
      writer.write(testInfo.getDisplayName());
      assertTrue(true);
      writer.write(" - success");
    }
  }
}
