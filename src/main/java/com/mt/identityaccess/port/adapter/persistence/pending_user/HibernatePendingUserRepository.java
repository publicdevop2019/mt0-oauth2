package com.mt.identityaccess.port.adapter.persistence.pending_user;

import com.mt.identityaccess.domain.model.pending_user.PendingUser;
import com.mt.identityaccess.domain.model.pending_user.PendingUserRepository;
import com.mt.identityaccess.domain.model.pending_user.RegistrationEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HibernatePendingUserRepository extends JpaRepository<PendingUser, Long>, PendingUserRepository {
    default Optional<PendingUser> pendingUserOfEmail(RegistrationEmail email) {
        return findByRegistrationEmailEmail(email.getEmail());
    }

    Optional<PendingUser> findByRegistrationEmailEmail(String email);

    default void add(PendingUser pendingUser) {
        save(pendingUser);
    }

}
