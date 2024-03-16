package org.tims.tirechange.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Builder
@Component
public class TireShopConfigLoader {
    private ObjectMapper objectMapper = new ObjectMapper();

    public List<TireShopConfig> loadConfig(String filePath)  throws IOException {
        TireShopConfig[] configs = objectMapper.readValue(new File(filePath), TireShopConfig[].class);
        return List.of(configs);
    }
}
