package org.umm.cifrasyletras.interfaces.rest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    private String name;
}
