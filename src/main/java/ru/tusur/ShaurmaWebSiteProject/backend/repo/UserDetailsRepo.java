package ru.tusur.ShaurmaWebSiteProject.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;

import java.util.Optional;

public interface UserDetailsRepo extends JpaRepository<UserDetails, Long> {
    public Optional<UserDetails> findByUsername(String username);

    public Optional<UserDetails> findByUsernameAndPassword(String username, String password);
}
