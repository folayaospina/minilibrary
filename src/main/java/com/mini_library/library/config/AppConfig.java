package com.mini_library.library.config;

import com.mini_library.library.interfaces.IRole;
import com.mini_library.library.model.Role;
import com.mini_library.library.model.User;
import com.mini_library.library.repository.RoleRepository;
import com.mini_library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    @Bean
    public UserDetailsService userDetailsService(){
        return email -> {
         final User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));
         return org.springframework.security.core.userdetails.User.builder()
                 .username(user.getEmail())
                 .password(user.getPassword())
                 .authorities(user.getRole().getRole().name())
                 .build();
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            Arrays.stream(IRole.values()).forEach(iRole -> {
                if (roleRepository.findRoleByRole(iRole).isEmpty()) {
                    Role role = new Role();
                    role.setRole(iRole);
                    roleRepository.save(role);
                }
            });
        };
    }

}
