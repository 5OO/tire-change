package org.tims.tirechange.configuration;

import lombok.*;

//@Builder
@Data
//@AllArgsConstructor
@Getter
@Setter
public class ApiConfig {
    private String endpoint;
    private String type;
}
