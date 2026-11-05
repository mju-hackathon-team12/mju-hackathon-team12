package com.mju.dearseoul;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:context-test",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.ai.api-key="
})
class DearseoulApplicationTests {

    @Test
    void contextLoads() {
    }

}
