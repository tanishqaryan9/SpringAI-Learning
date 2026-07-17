package com.SpringAI.Generator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService{

    private final ApplicationRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public String signin(LoginRequestDTO login) {
        UserProfile user=repository.findByEmail(login.getEmail());
        if(user==null || user.getPassword() == null || !passwordEncoder.matches(login.getPassword(), user.getPassword()))
        {
            throw new IllegalArgumentException("Invalid Credentials");
        }
        return jwtUtils.generateTokenFromUsername(user.getEmail());
    }

    @Override
    public String register(LoginRequestDTO register) {
            if (repository.existsByEmail(register.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            UserProfile user = new UserProfile();
            user.setEmail(register.getEmail());
            user.setPassword(passwordEncoder.encode(register.getPassword()));
            user.setRoles(java.util.List.of("ROLE_USER"));
            repository.save(user);

            return jwtUtils.generateTokenFromUsername(register.getEmail());
    }


}
