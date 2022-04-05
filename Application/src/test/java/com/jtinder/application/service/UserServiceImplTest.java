package com.jtinder.application.service;

import com.jtinder.application.domain.Sex;
import com.jtinder.application.domain.User;
import com.jtinder.application.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    public User returnUser = User.builder()
            .name("Oleg")
            .userId(99L)
            .password("123")
            .sex(Sex.FEMALE)
            .description("Вся такая растакая")
            .build();

    @Test
    void returnedUserByIdIsCorrect() {
        when(userRepository.findById(99L)).thenReturn(Optional.of(returnUser));

        User user = (User) userService.loadUserByUsername("99");
        assertNotNull(user);
        assertEquals(user.getName(), "Oleg");
        assertEquals(user.getUserId(), 99L);
        assertEquals(user.getPassword(), "123");
        assertEquals(user.getSex(), Sex.FEMALE);
    }


}
