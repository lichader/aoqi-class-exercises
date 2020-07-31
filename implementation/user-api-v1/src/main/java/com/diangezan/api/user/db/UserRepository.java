package com.diangezan.api.user.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<DbUser, Long> {

    Optional<DbUser> findByUsername(String username);
}
