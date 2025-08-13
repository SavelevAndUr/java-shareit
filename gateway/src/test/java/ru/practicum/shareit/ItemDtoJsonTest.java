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
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testSerialize() throws Exception {
        ItemDto dto = new ItemDto(1L, "Дрель", "Аккумуляторная дрель", true, 10L, null, null, List.of(), null);

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Аккумуляторная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(10);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Дрель\",\"description\":\"Аккумуляторная дрель\",\"available\":true,\"ownerId\":10}";

        ItemDto dto = objectMapper.readValue(content, ItemDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getOwnerId()).isEqualTo(10L);
    }

    @Test
    void whenRequiredFieldsInvalid_thenValidationFails() {
        ItemDto dto = new ItemDto();
        dto.setName("");
        dto.setDescription("");
        dto.setAvailable(null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(3);
    }
}
