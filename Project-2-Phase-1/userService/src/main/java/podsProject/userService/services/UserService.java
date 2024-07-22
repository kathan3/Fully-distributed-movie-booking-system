package podsProject.userService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import podsProject.userService.DTO.UserDTO;
import podsProject.userService.entity.User;
import podsProject.userService.repository.UserRepository;


import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    public User createUser(UserDTO userDTO) {
        String email = userDTO.getEmail();
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(email);

        return userRepository.save(user);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public boolean deleteUser(Integer userId) {
        User user=userRepository.findById(userId).orElse(null);

        if (user==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

// Delete bookings
        try {
            restTemplate.delete("http://host.docker.internal:8081/bookings/users/"+ userId);
        } catch (HttpClientErrorException.NotFound e) {
            // Ignore the 404 error if the user doesn't have any bookings
        }

// Delete wallet
        try {
            restTemplate.delete("http://host.docker.internal:8082/wallets/"+ userId);
        } catch (HttpClientErrorException.NotFound e) {
            // Ignore the 404 error if the user doesn't have any bookings
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok("User and associated data deleted successfully").hasBody();
    }

    public void deleteAllUsers() {
        // Delete all user records from Booking service
        restTemplate.delete("http://host.docker.internal:8081/bookings");

        // Delete all user records from Wallet service
        restTemplate.delete("http://host.docker.internal:8082/wallets");

        // Delete all user records from User service
        userRepository.deleteAll();
    }

    }

