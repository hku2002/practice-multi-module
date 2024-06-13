package com.user.api.service;

import com.user.entity.User;
import com.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String findUserById(Long id) {
        User user = userRepository.findById(1L).orElseThrow();
        return user.getName();
    }

}
