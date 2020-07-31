package com.diangezan.api.user.web.model;

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
