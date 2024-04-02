package edu.iu.isaikuma.primeservice.service;

import edu.iu.isaikuma.primeservice.model.Customer;
import edu.iu.isaikuma.primeservice.repository.IAuthenticationRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Service
public class AuthenticationService implements UserDetailsService {

    private final IAuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(IAuthenticationRepository authenticationRepository, PasswordEncoder passwordEncoder) {
        this.authenticationRepository = authenticationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(Customer customer) {
        try {
            String passwordEncoded = passwordEncoder.encode(customer.getPassword());
            customer.setPassword(passwordEncoded);
            authenticationRepository.save(customer);
        } catch (IOException e) {
            // Handle the exception appropriately, such as logging or rethrowing as a runtime exception
            throw new RuntimeException("Error occurred while saving the customer", e);
        }
    }
    
    
    
    public boolean login(String username, String password) {
        try {
            Customer customer = authenticationRepository.findByUsername(username);
            if (customer != null && passwordEncoder.matches(password, customer.getPassword())) {
                return true;
            } else {
                throw new UsernameNotFoundException("User not found or password does not match");
            }
        } catch (IOException e) {
            throw new RuntimeException("Login failed", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Customer customer = authenticationRepository.findByUsername(username);
            if (customer == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return User
                    .withUsername(username)
                    .password(customer.getPassword())
                    .roles("USER")
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("User retrieval failed", e);
        }
    }
}
