package com.example.demo.repo;

import com.example.demo.DTO.CustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

    // TestEntityManager is a lightweight alternative to the standard EntityManager.
    // It allows us to insert test data into the database completely independently
    // from the repository we are trying to test.
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_whenUserExists_returnsUser() {
        // 1. Arrange: Create a user and save it directly to the test database
        // Note: You will need to adjust the instantiation below based on whether
        // CustomUser uses a constructor, builder, or setters for its fields.
        CustomUser user = new CustomUser();
        user.setUsername("test123");
        user.setPassword("hashedpassword123");
        // user.setRole("ROLE_ADMIN"); // Add any other non-null fields your entity requires

        // persistAndFlush forces the data into the in-memory database immediately
        entityManager.persistAndFlush(user);

        // 2. Act: Call your custom repository method
        Optional<CustomUser> foundUser = userRepository.findByUsername("test123");

        // 3. Assert: Verify the user was found and the data matches
        assertTrue(foundUser.isPresent(), "User should be found in the database");
        assertEquals("test123", foundUser.get().getUsername());
    }

    @Test
    void findByUsername_whenUserDoesNotExist_returnsEmptyOptional() {
        // 1. Arrange: The database is empty by default at the start of this test.

        // 2. Act: Attempt to find a user that hasn't been saved
        Optional<CustomUser> foundUser = userRepository.findByUsername("unknownUser");

        // 3. Assert: Verify the Optional is empty
        assertFalse(foundUser.isPresent(), "Optional should be empty when user does not exist");
    }
}