package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialize() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель для ремонта")
                .created(LocalDateTime.now())
                .items(java.util.List.of())
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель для ремонта");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"description\":\"Нужна дрель\",\"created\":\"2024-01-01T10:00:00\"}";

        ItemRequestDto dto = objectMapper.readValue(content, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
    }

    @Test
    void whenDescriptionIsNull_thenStillSerializable() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(null);

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
