package dev.rpmhub;

import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
class LoansWSIT extends LoansWSTest {
    // Execute the same tests but in packaged mode.
    // Note: @InjectMock only works with @QuarkusTest, so this IT class
    // is not exercised in the default build (skipITs=true).
}
