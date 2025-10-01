# JUnit Unit Testing Setup for University Voting System

This document explains the JUnit unit testing setup and provides examples of how to write and run unit tests in the University Voting System project.

## Dependencies Added

The following JUnit and unit testing dependencies have been added to `pom.xml`:

```xml
<!-- Core Spring Boot Test (includes JUnit 5) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5 for unit testing -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito for mocking in unit tests -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

## Unit Testing Approach

This project focuses on **unit testing only**, which means:
- Testing individual components in isolation
- Using mocks for all external dependencies
- No database or Spring context loading (except for the main application test)
- Fast execution and minimal setup

## Unit Test Examples

### UserService Unit Test

The main example demonstrates testing the `UserService` class with mocked dependencies:

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testRegisterSuccess() {
        // Given - Set up mocks
        when(userRepository.findByUniversityId("TEST123")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When - Execute the method under test
        User result = userService.register("TEST123", "Test User", "VOTER", "password123", "1234567890");
        
        // Then - Verify the results
        assertNotNull(result);
        assertEquals("TEST123", result.getUniversityId());
        assertEquals("VOTER", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testRegisterWithAdminRole() {
        // When & Then - Test exception scenarios
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register("TEST123", "Test User", "ADMIN", "password123", "1234567890");
        });
        
        assertEquals("Only VOTER role is allowed for new registrations", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
```

## Running Unit Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=UserServiceTest#testRegisterSuccess
```

### Run Tests with Verbose Output
```bash
mvn test -Dtest=JUnitExamplesTest
```

## JUnit 5 Features for Unit Testing

### 1. Basic Assertions
```java
@Test
void testBasicAssertions() {
    assertEquals(2, 1 + 1, "1 + 1 should equal 2");
    assertTrue(5 > 3, "5 should be greater than 3");
    assertFalse(10 < 5, "10 should not be less than 5");
    assertNotNull("test", "String should not be null");
}
```

### 2. Exception Testing
```java
@Test
void testExceptionThrowing() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        throw new IllegalArgumentException("Invalid argument");
    });
    assertEquals("Invalid argument", exception.getMessage());
}
```

### 3. Parameterized Tests
```java
@ParameterizedTest
@ValueSource(strings = {"racecar", "radar", "able was I ere I saw elba"})
void testPalindromes(String candidate) {
    assertTrue(isPalindrome(candidate));
}
```

### 4. Test Lifecycle
```java
@BeforeAll
static void setUpBeforeClass() {
    // Setup before all tests in the class
}

@BeforeEach
void setUp() {
    // Setup before each test method
}

@AfterEach
void tearDown() {
    // Cleanup after each test method
}

@AfterAll
static void tearDownAfterClass() {
    // Cleanup after all tests in the class
}
```

### 5. Conditional Tests
```java
@Test
void testOnlyOnCiServer() {
    Assumptions.assumingThat("CI".equals(System.getenv("ENV")),
        () -> {
            // execute only on CI server
            assertEquals(2, 1 + 1);
        });
}
```

### 6. Timeout Tests
```java
@Test
@Timeout(5)
void testWithTimeout() {
    assertTimeout(Duration.ofMillis(100), () -> {
        Thread.sleep(50);
        return "result";
    });
}
```

## Mockito Features for Unit Testing

### 1. Creating Mocks
```java
@Mock
private UserRepository userRepository;

@InjectMocks
private UserService userService;
```

### 2. Stubbing Method Calls
```java
when(userRepository.findByUniversityId("TEST123")).thenReturn(Optional.empty());
when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
```

### 3. Verifying Interactions
```java
verify(userRepository, times(1)).save(any(User.class));
verify(userRepository, never()).delete(any(User.class));
verify(passwordEncoder).encode("password123");
```

### 4. Argument Matchers
```java
when(userRepository.save(any(User.class))).thenReturn(savedUser);
when(service.process(eq("specificValue"), anyString())).thenReturn(result);
```

## Test Structure (Unit Tests Only)

```
src/test/java/
├── com/univvoting/
│   ├── service/
│   │   └── UserServiceTest.java
│   ├── examples/
│   │   └── JUnitExamplesTest.java
│   └── UnivVotingApplicationTests.java
```

## Unit Testing Best Practices

1. **Test in Isolation**: Each unit test should test only one component
2. **Use Mocks**: Mock all external dependencies (repositories, services, etc.)
3. **Follow AAA Pattern**: Arrange (setup), Act (execute), Assert (verify)
4. **Test Edge Cases**: Include tests for error conditions and boundary values
5. **Use Descriptive Names**: Test method names should clearly describe the scenario
6. **Keep Tests Fast**: Unit tests should execute quickly
7. **Independent Tests**: Each test should be able to run in isolation
8. **One Assertion Per Test**: Focus on testing one behavior per test method

## Unit Testing Advantages

- **Fast Execution**: No database or Spring context startup
- **Isolated Testing**: Tests focus on individual components
- **Easy Debugging**: Failures are isolated to specific components
- **Simple Setup**: Minimal configuration required
- **Reliable**: No external dependencies to cause flaky tests

## What's NOT Included

This setup intentionally excludes:
- Integration tests (full Spring context)
- Web layer tests (MockMvc, controllers)
- Database tests (JPA repositories)
- End-to-end tests

These can be added later if needed, but the focus here is on fast, reliable unit testing.

## Troubleshooting

### Common Issues

1. **Tests not running**: Check test file naming (should end with `Test` or `Tests`)
2. **Mock injection issues**: Ensure `@InjectMocks` and `@Mock` are used correctly
3. **Verification failures**: Check that the exact method calls are being verified
4. **Stubbing issues**: Ensure mocked methods match the actual method signatures

For more information, refer to:
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=UserServiceTest#testRegisterSuccess
```

### Run Tests with Different Profiles
```bash
mvn test -Dspring.profiles.active=test
```

## JUnit 5 Features Demonstrated

### 1. Basic Assertions
```java
@Test
void testBasicAssertions() {
    assertEquals(2, 1 + 1, "1 + 1 should equal 2");
    assertTrue(5 > 3, "5 should be greater than 3");
    assertFalse(10 < 5, "10 should not be less than 5");
    assertNotNull("test", "String should not be null");
}
```

### 2. Exception Testing
```java
@Test
void testExceptionThrowing() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        throw new IllegalArgumentException("Invalid argument");
    });
    assertEquals("Invalid argument", exception.getMessage());
}
```

### 3. Parameterized Tests
```java
@ParameterizedTest
@ValueSource(strings = {"racecar", "radar", "able was I ere I saw elba"})
void testPalindromes(String candidate) {
    assertTrue(isPalindrome(candidate));
}
```

### 4. Test Lifecycle
```java
@BeforeAll
static void setUpBeforeClass() {
    // Setup before all tests
}

@BeforeEach
void setUp() {
    // Setup before each test
}

@AfterEach
void tearDown() {
    // Cleanup after each test
}

@AfterAll
static void tearDownAfterClass() {
    // Cleanup after all tests
}
```

### 5. Conditional Tests
```java
@Test
void testOnlyOnCiServer() {
    Assumptions.assumingThat("CI".equals(System.getenv("ENV")),
        () -> {
            // execute only on CI server
            assertEquals(2, 1 + 1);
        });
}
```

### 6. Timeout Tests
```java
@Test
@Timeout(5)
void testWithTimeout() {
    assertTimeout(Duration.ofMillis(100), () -> {
        Thread.sleep(50);
        return "result";
    });
}
```

## Test Structure

```
src/test/java/
├── com/univvoting/
│   ├── controller/
│   │   ├── AuthControllerTest.java
│   │   └── TestSecurityConfig.java
│   ├── service/
│   │   └── UserServiceTest.java
│   ├── integration/
│   │   └── RegistrationIntegrationTest.java
│   ├── examples/
│   │   └── JUnitExamplesTest.java
│   └── UnivVotingApplicationTests.java
└── resources/
    └── application-test.properties
```

## Best Practices

1. **Use descriptive test names**: Test method names should clearly describe what is being tested
2. **Follow AAA pattern**: Arrange, Act, Assert
3. **Use @DisplayName**: Add human-readable test descriptions
4. **Mock external dependencies**: Use Mockito to mock repositories and external services
5. **Use test profiles**: Separate test configuration from production
6. **Test edge cases**: Include tests for error conditions and boundary values
7. **Keep tests independent**: Each test should be able to run in isolation

## Additional Features

- **Test Reports**: Maven Surefire generates test reports in `target/surefire-reports/`
- **Code Coverage**: Can be added with JaCoCo plugin
- **Parallel Execution**: JUnit 5 supports parallel test execution
- **Test Discovery**: Tests are automatically discovered by naming convention

## Troubleshooting

### Common Issues

1. **Tests not running**: Check test file naming (should end with `Test` or `Tests`)
2. **Security issues in web tests**: Use `@Import(TestSecurityConfig.class)` or disable security
3. **Database issues**: Ensure H2 dependency is included and test profile is active
4. **Mock issues**: Use `@MockBean` for Spring components, `@Mock` for plain objects

For more information, refer to:
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)