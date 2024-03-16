package org.tims.tirechange.configuration;

import lombok.Builder;
import lombok.Data;

//@Builder
@Data
public class TireShopConfig {
    private String name;
    private String address;
    private String[] vehicleTypes;
    private ApiConfig api;
}
