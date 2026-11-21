
package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.junit.jupiter.api.Assertions.*;

public class RenovarToken implements Task {

    private final String nombreToken;

    public RenovarToken(String nombreToken) {
        this.nombreToken = nombreToken;
    }

    public static RenovarToken usando(String nombreToken) {
        return instrumented(RenovarToken.class, nombreToken);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        String refreshToken = actor.recall(nombreToken);

        assertNotNull(refreshToken, "No existe refreshToken en la memoria");

        actor.attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", refreshToken))
                        )
        );

        if (SerenityRest.lastResponse().statusCode() == 200) {

            String nuevoAccessToken = SerenityRest.lastResponse()
                    .jsonPath().getString("accessToken");

            String nuevoRefreshToken = SerenityRest.lastResponse()
                    .jsonPath().getString("refreshToken");

            assertNotNull(nuevoAccessToken);
            assertNotNull(nuevoRefreshToken);

            actor.remember("nuevoAccessToken", nuevoAccessToken);
            actor.remember("nuevoRefreshToken", nuevoRefreshToken);
        }
    }
}
