package com.mt.identityaccess.infrastructure.persistence;

import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.pending_user.PendingUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface HibernatePendingUserRepository extends JpaRepository<PendingUser, Long>, PendingUserRepository {
    default Optional<PendingUser> registeredUsing(String email) {
        return findByEmail(email);
    }

    Optional<PendingUser> findByEmail(String email);

}