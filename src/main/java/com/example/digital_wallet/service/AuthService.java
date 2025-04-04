package com.example.digital_wallet.service;

import com.example.digital_wallet.dto.AuthResponseDTO;
import com.example.digital_wallet.dto.LoginRequestDTO;
import com.example.digital_wallet.dto.RegisterRequestDTO;
import com.example.digital_wallet.model.RoleEnum;
import com.example.digital_wallet.model.User;
import com.example.digital_wallet.model.Wallet;
import com.example.digital_wallet.repository.UserRepository;
import com.example.digital_wallet.repository.WalletRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final WalletRepository walletRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       UserDetailsService userDetailsService, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.walletRepository = walletRepository;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(RoleEnum.valueOf(request.getRole().toUpperCase())); // Convert string to enum

        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        walletRepository.save(wallet);

        savedUser.setWallet(wallet);
        userRepository.save(savedUser);

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getUsername()));
        return new AuthResponseDTO("Registered Successfully");
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(token);
    }
}
