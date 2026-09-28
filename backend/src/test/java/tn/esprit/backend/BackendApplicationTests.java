package tn.esprit.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Nécessite MySQL — désactivé pour la CI (tests unitaires Mockito dans service/)")
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
