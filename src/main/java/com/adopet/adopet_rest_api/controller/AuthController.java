package com.adopet.adopet_rest_api.controller;

import com.adopet.adopet_rest_api.entity.User;
import com.adopet.adopet_rest_api.model.LoginUserRequest;
import com.adopet.adopet_rest_api.model.LoginUserResponse;
import com.adopet.adopet_rest_api.model.RegisterUserRequest;
import com.adopet.adopet_rest_api.repository.UserRepository;
import com.adopet.adopet_rest_api.security.JwtUtil;
import com.adopet.adopet_rest_api.service.CustomUserDetailService;
import com.adopet.adopet_rest_api.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);

    @Autowired
    JwtUtil jwtUtils;

    @Autowired
    ValidationService validationService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest request) {
        validationService.validate(request);
        if(userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        User newUser = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .build();

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("User Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + request.getUsername()));

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username not found");
        }

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Password tidak cocok!");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateToken(userDetails.getUsername());
        LoginUserResponse response = new LoginUserResponse("Login successful", jwtToken);
        return ResponseEntity.ok(response);
    }

}
