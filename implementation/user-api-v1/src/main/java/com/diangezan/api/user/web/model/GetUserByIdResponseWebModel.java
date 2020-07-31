package com.diangezan.api.user.web.model;

import com.diangezan.api.user.db.DbUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserByIdResponseWebModel {
    private DbUser data;
}
