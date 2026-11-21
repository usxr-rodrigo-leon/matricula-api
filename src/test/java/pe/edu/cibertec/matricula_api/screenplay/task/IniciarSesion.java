
package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static org.junit.jupiter.api.Assertions.*;

public class IniciarSesion implements Task {

    private final String usuario;
    private final String clave;

    public IniciarSesion(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public static IniciarSesion conCredenciales(String usuario, String clave) {
        return instrumented(IniciarSesion.class, usuario, clave);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {

        actor.attemptsTo(
                Post.to("/api/auth/login")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "username", usuario,
                                        "password", clave
                                ))
                        )
        );

        if (SerenityRest.lastResponse().statusCode() == 200) {

            String accessToken = SerenityRest.lastResponse()
                    .jsonPath().getString("accessToken");

            String refreshToken = SerenityRest.lastResponse()
                    .jsonPath().getString("refreshToken");

            assertNotNull(accessToken, "Falta accessToken");
            assertNotNull(refreshToken, "Falta refreshToken");
            assertFalse(accessToken.isBlank(), "accessToken vacío");
            assertFalse(refreshToken.isBlank(), "refreshToken vacío");

            actor.remember("accessToken", accessToken);
            actor.remember("refreshToken", refreshToken);
        }
    }
}
