package com.diangezan.api.communication.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunicationData {

    private String destination;
    private String type;
    private String content;
}
