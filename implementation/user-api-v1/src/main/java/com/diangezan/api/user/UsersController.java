package com.diangezan.api.user;

import com.diangezan.api.user.db.DbUser;
import com.diangezan.api.user.db.UserRepository;
import com.diangezan.api.user.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;


@RestController
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
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

        var commData = new CommunicationData();
        commData.setDestination(user.getEmail());
        commData.setType("email");
        commData.setContent("Welcome");

        var commsRequest = new CreateCommunicationRequest(commData);

        var commsResponse = restTemplate.postForEntity(
                "http://communication-api-v1:8081/api/communication/v1/communications",
                    commsRequest,
                    Void.class);

        response.addHeader("Location", "/api/user/v1/users/" + savedUser.getId());

        var clone = new DbUser();
        clone.setId(savedUser.getId());
        clone.setUsername(savedUser.getUsername());
        clone.setType(savedUser.getType());
        clone.setFirstName(savedUser.getFirstName());
        clone.setLastName(savedUser.getLastName());
        clone.setMobile(savedUser.getMobile());
        clone.setEmail(savedUser.getEmail());

        return new CreateUserResponseWebModel(clone);
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
