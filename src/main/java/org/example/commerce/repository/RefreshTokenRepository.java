package org.example.commerce.repository;

import org.example.commerce.entity.RefreshToken;
import org.example.commerce.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @EntityGraph(attributePaths = {"user"})
    Optional<RefreshToken> findWithUserByToken(String token);

    Optional<RefreshToken> findByUserAndRevokedFalse(User user);

    void deleteByUser(User user);
}
