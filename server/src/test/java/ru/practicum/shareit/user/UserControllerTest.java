package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User"))
                .andExpect(jsonPath("$[0].email").value("user@email.com"));

        Mockito.verify(userService, Mockito.times(1)).getAllUsers();
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto(null, "User", "user@email.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"));

        Mockito.verify(userService, Mockito.times(1)).createUser(any(UserDto.class));
    }

    @Test
    void createUser_conflictOnDuplicateEmail_shouldReturn409() throws Exception {
        Mockito.when(userService.createUser(any(UserDto.class)))
                .thenThrow(new ConflictException("Email is already in use"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto(null, "User", "dup@email.com"))))
                .andExpect(status().isConflict());

        Mockito.verify(userService).createUser(any(UserDto.class));
    }

    @Test
    void updateUser_shouldReturnUpdated() throws Exception {
        UserDto updated = new UserDto(1L, "Updated", "updated@email.com");
        Mockito.when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updated);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto(null, "Updated", "updated@email.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));

        Mockito.verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void updateUser_conflictOnDuplicateEmail_shouldReturn409() throws Exception {
        Mockito.when(userService.updateUser(eq(1L), any(UserDto.class)))
                .thenThrow(new ConflictException("Email is already in use"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto(null, null, "exists@email.com"))))
                .andExpect(status().isConflict());

        Mockito.verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void updateUser_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.updateUser(eq(999L), any(UserDto.class)))
                .thenThrow(new NotFoundException("User with id=999 not found"));

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto(null, "Name", "mail@email.com"))))
                .andExpect(status().isNotFound());

        Mockito.verify(userService).updateUser(eq(999L), any(UserDto.class));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"));

        Mockito.verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_notFound_shouldReturn404() throws Exception {
        Mockito.when(userService.getUserById(999L)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        Mockito.verify(userService).getUserById(999L);
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUser(1L);
    }
}
