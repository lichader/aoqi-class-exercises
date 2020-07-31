package com.diangezan.api.communication;
import com.diangezan.api.communication.web.model.CreateCommunicationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class CommunicationsController {
    private final static String MESSAGE_TEMPLATE = "Sent %s to %s with content: %s";

    @PostMapping("/communications")
    public ResponseEntity<?> createCommunication(@RequestBody CreateCommunicationRequest request) {
        var data = request.getData();

        if (StringUtils.isEmpty(data.getDestination())
            || StringUtils.isEmpty(data.getType())
            || StringUtils.isEmpty(data.getContent())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is invalid");
        }

        log.info(String.format(MESSAGE_TEMPLATE, data.getType(), data.getDestination(), data.getContent()));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
