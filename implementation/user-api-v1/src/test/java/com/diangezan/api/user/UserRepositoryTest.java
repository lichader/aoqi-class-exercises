package com.diangezan.api.user;

import com.diangezan.api.user.db.DbUser;
import com.diangezan.api.user.db.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_empty(){
        var username = "ripeng";

        var result = userRepository.findByUsername(username);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByUsername_foundExisting(){
        var user1 = new DbUser();
        user1.setUsername("ripeng1");
        user1.setPassword("password");
        user1.setType("student");

        var user1Id = userRepository.save(user1).getId();

        var user2 = new DbUser();
        user2.setUsername("ripeng2");
        user2.setPassword("password");
        user2.setType("teacher");
        userRepository.save(user2);

        var result = userRepository.findByUsername("ripeng1");
        assertTrue(result.isPresent());
        assertEquals(user1Id, result.get().getId());
    }
}
