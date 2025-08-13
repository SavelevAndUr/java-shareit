package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialize() throws Exception {
        UserDto user = new UserDto(1L, "John Doe", "john.doe@example.com");

        JsonContent<UserDto> result = json.write(user);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        UserDto result = objectMapper.readValue(content, UserDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void whenEmailIsInvalid_thenValidationFails() {
        UserDto user = new UserDto(null, "John Doe", "invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
    }

    @Test
    void whenEmailIsNull_thenValidationFails() {
        UserDto user = new UserDto(null, "John Doe", null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        UserDto user = new UserDto(null, "", "john.doe@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
    }
}
