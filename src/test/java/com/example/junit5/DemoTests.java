package com.example.junit5;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

//@Test*
//@ParameterizedTest*
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DemoTests {

  private final AtomicInteger counter = new AtomicInteger(0);

  @BeforeAll
  static void beforeAll() {
    System.out.println("Before all test methods");
  }

  @BeforeEach
  void beforeEach() {
    System.out.println("Before each test method " + counter.incrementAndGet());
  }

  @Test
  void test() {
    assertTrue(true);
    System.out.println("Hello, JUnit 5!");
  }

  static Stream<Integer> intsForTesting() {
    return Stream.of(1, 2, 3);
  }

  @ParameterizedTest
  @MethodSource("intsForTesting")
  void testWithParameters(int integer) {
    assertTrue(integer < 6);
  }

  @TestFactory
  Stream<DynamicNode> dynamicTestsFromStream() {
    return Stream.of("A", "B", "C")
      .map(letter -> dynamicTest("test" + letter, () -> {
        assertTrue(letter.length() == 1);
      }));
  }

}
