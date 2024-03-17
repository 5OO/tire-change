package org.tims.tirechange.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ApiConfig {
    private String endpoint;
    private String type;
}
