package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:shareit;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto baseUser;

    @BeforeEach
    void setup() {
        baseUser = new UserDto(null, "User", "user@example.com");
    }

    @Test
    void createUser_shouldPersistAndReturnDto() {
        UserDto created = userService.createUser(baseUser);

        assertNotNull(created.getId());
        assertEquals(baseUser.getName(), created.getName());
        assertEquals(baseUser.getEmail(), created.getEmail());

        // Проверим, что реально записался
        User entity = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("User", entity.getName());
        assertEquals("user@example.com", entity.getEmail());
    }

    @Test
    void createUser_whenEmailDuplicate_shouldThrowConflict() {
        userService.createUser(baseUser);

        UserDto dup = new UserDto(null, "Another", "user@example.com");

        assertThrows(ConflictException.class, () -> userService.createUser(dup));
    }

    @Test
    void updateUser_fullUpdate_shouldChangeBothFields() {
        UserDto created = userService.createUser(baseUser);

        UserDto patch = new UserDto(null, "Updated", "updated@example.com");
        UserDto updated = userService.updateUser(created.getId(), patch);

        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());

        User entity = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("Updated", entity.getName());
        assertEquals("updated@example.com", entity.getEmail());
    }

    @Test
    void updateUser_partialUpdate_nameOnly_shouldKeepEmail() {
        UserDto created = userService.createUser(baseUser);

        UserDto patch = new UserDto(null, "New Name", null);
        UserDto updated = userService.updateUser(created.getId(), patch);

        assertEquals("New Name", updated.getName());
        assertEquals("user@example.com", updated.getEmail());
    }

    @Test
    void updateUser_partialUpdate_emailOnly_shouldKeepName() {
        UserDto created = userService.createUser(baseUser);

        UserDto patch = new UserDto(null, null, "new@example.com");
        UserDto updated = userService.updateUser(created.getId(), patch);

        assertEquals("User", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void updateUser_whenEmailAlreadyInUse_shouldThrowConflict() {
        UserDto u1 = userService.createUser(new UserDto(null, "U1", "u1@example.com"));
        UserDto u2 = userService.createUser(new UserDto(null, "U2", "u2@example.com"));

        UserDto patch = new UserDto(null, null, "u1@example.com");

        assertThrows(ConflictException.class, () -> userService.updateUser(u2.getId(), patch));
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowNotFound() {
        UserDto patch = new UserDto(null, "Name", "mail@example.com");

        assertThrows(NotFoundException.class, () -> userService.updateUser(999L, patch));
    }

    @Test
    void getUserById_shouldReturnDto() {
        UserDto created = userService.createUser(baseUser);

        UserDto found = userService.getUserById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals(created.getName(), found.getName());
        assertEquals(created.getEmail(), found.getEmail());
    }

    @Test
    void getUserById_whenNotFound_shouldThrow() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getAllUsers_shouldReturnList() {
        userService.createUser(new UserDto(null, "A", "a@ex.com"));
        userService.createUser(new UserDto(null, "B", "b@ex.com"));

        List<UserDto> all = userService.getAllUsers();

        assertEquals(2, all.size());
    }

    @Test
    void deleteUser_shouldRemoveEntity() {
        UserDto created = userService.createUser(baseUser);

        userService.deleteUser(created.getId());

        assertFalse(userRepository.existsById(created.getId()));
    }

    @Test
    void deleteUser_whenNotFound_shouldThrow() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
    }
}
