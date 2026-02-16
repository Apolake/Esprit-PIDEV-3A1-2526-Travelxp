package com.travelxp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.travelxp.util.StageManager;

@SpringBootTest
class TravelXPApplicationTests {

    @MockBean
    private StageManager stageManager;

    @Test
    void contextLoads() {
    }
}
