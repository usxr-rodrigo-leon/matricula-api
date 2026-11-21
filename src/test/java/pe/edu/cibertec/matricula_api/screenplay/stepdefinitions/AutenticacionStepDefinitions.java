
package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.junit.jupiter.api.Assertions.*;

public class AutenticacionStepDefinitions {

    @Y("Ana inicia sesión con sus credenciales")
    public void anaIniciaSesion() {

        theActorInTheSpotlight().attemptsTo(
                Post.to("/api/auth/login")
                        .with(req -> req
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "username", "i202610001",
                                        "password", "Cibertec2026"
                                ))
                        )
        );

        assertEquals(200, SerenityRest.lastResponse().statusCode(),
                "El inicio de sesión debe responder HTTP 200");

        String token = SerenityRest.lastResponse()
                .jsonPath().getString("accessToken");

        assertNotNull(token, "La API no devolvió accessToken");
        assertFalse(token.isBlank(), "El accessToken está vacío");

        theActorInTheSpotlight().remember("token", token);
    }
}
