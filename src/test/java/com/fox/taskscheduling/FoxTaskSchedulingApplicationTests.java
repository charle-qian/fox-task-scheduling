package com.fox.taskscheduling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
class FoxTaskSchedulingApplicationTests {

    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
    }

    @Test
    void virtualThreadsAreEnabled() {
        Boolean enabled = environment.getProperty("spring.threads.virtual.enabled", Boolean.class);

        org.assertj.core.api.Assertions.assertThat(enabled).isTrue();
    }
}
