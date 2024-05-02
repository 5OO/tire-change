package org.tims.tirechange;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TireChangeApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoadsTest() {
        assertThat(applicationContext).isNotNull();
    }
  
}