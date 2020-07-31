package com.diangezan.api.user;

import com.diangezan.api.user.db.UserRepository;
import com.diangezan.api.user.web.model.CreateUserRequestWebModel;
import com.diangezan.api.user.web.model.CreateUserResponseWebModel;
import com.diangezan.api.user.web.model.GetUserByIdResponseWebModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;


@RestController
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateUserResponseWebModel createUser(@RequestBody CreateUserRequestWebModel request, HttpServletResponse response){
        var user = request.getData();

        if (StringUtils.isEmpty(user.getUsername())
                || StringUtils.isEmpty(user.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or password must be provided!");
        }

        var existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username has already been used!");
        }

        var savedUser = userRepository.save(user);
        savedUser.setPassword(null);

        response.addHeader("Location", "/api/user/v1/users/" + savedUser.getId());

        return new CreateUserResponseWebModel(savedUser);
    }

    @GetMapping(value = "/users/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GetUserByIdResponseWebModel getUserById(@PathVariable Long id){
        var result = userRepository.findById(id);

        if (result.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist!");
        }

        var user = result.get();
        user.setPassword(null);

        var response = new GetUserByIdResponseWebModel(user);

        return response;
    }
}
