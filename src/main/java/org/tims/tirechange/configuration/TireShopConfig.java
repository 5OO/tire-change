package org.tims.tirechange.configuration;

import lombok.Data;

@Data
public class TireShopConfig {
    private String name;
    private String address;
    private String[] vehicleTypes;
    private ApiConfig api;
}
