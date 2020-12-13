package com.mt.identityaccess.infrastructure.persistence;

import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.domain.model.user.UserId;
import com.mt.identityaccess.domain.model.user.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HibernateUserRepository extends JpaRepository<User, Long>, UserRepository {
    default UserId nextIdentity() {
        return new UserId();
    }

    default Optional<User> userOfId(UserId userId) {
        return findById(userId.persistentId());
    }

    default void add(User user) {
        save(user);
    }
}
