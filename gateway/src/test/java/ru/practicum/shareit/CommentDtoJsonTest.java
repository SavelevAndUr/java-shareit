package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialize() throws Exception {
        CommentDto dto = new CommentDto(1L, "Отличная дрель!", "Иван", LocalDateTime.now());

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная дрель!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Иван");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
    }

    @Test
    void testDeserialize() throws Exception {
        String content = """
        {
          "id": 1,
          "text": "Отличная дрель!",
          "authorName": "Иван",
          "created": "2024-01-01T10:00:00.000000000"
        }
        """;

        CommentDto dto = objectMapper.readValue(content, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличная дрель!");
        assertThat(dto.getAuthorName()).isEqualTo("Иван");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
    }

    @Test
    void whenTextIsBlank_thenValidationFails() {
        CommentDto dto = new CommentDto(1L, "", "Иван", LocalDateTime.now());

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
    }
}
