
package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CerrarSesion implements Task {

    public static CerrarSesion actual() {
        return instrumented(CerrarSesion.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        String accessToken = actor.recall("accessToken");
        String refreshToken = actor.recall("refreshToken");

        assertNotNull(accessToken, "No existe accessToken en memoria");
        assertNotNull(refreshToken, "No existe refreshToken en memoria");

        actor.attemptsTo(
                Post.to("/api/auth/logout")
                        .with(req -> req
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", refreshToken))
                        )
        );
    }
}
