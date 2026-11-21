
package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import pe.edu.cibertec.matricula_api.screenplay.task.CerrarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.RenovarToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.junit.jupiter.api.Assertions.*;

public class SeguridadStepDefinitions {

    private Actor actor() {
        return theActorInTheSpotlight();
    }

    @Cuando("Ana inicia sesión de seguridad con el usuario {string} y clave {string}")
    public void iniciarSesion(String usuario, String clave) {
        actor().attemptsTo(IniciarSesion.conCredenciales(usuario, clave));
    }

    @Y("Ana guarda los tokens originales")
    public void guardarTokensOriginales() {
        String accessToken = actor().recall("accessToken");
        String refreshToken = actor().recall("refreshToken");

        assertNotNull(accessToken);
        assertNotNull(refreshToken);

        actor().remember("accessTokenOriginal", accessToken);
        actor().remember("refreshTokenOriginal", refreshToken);
    }

    @Cuando("Ana renueva su refresh token original")
    public void renovarTokenOriginal() {
        actor().attemptsTo(
                RenovarToken.usando("refreshTokenOriginal")
        );
    }

    @Y("Ana recibe tokens nuevos")
    public void verificarTokensNuevos() {
        String accessOriginal = actor().recall("accessTokenOriginal");
        String refreshOriginal = actor().recall("refreshTokenOriginal");
        String accessNuevo = actor().recall("nuevoAccessToken");
        String refreshNuevo = actor().recall("nuevoRefreshToken");

        assertNotNull(accessNuevo);
        assertNotNull(refreshNuevo);

        assertNotEquals(accessOriginal, accessNuevo);
        assertNotEquals(refreshOriginal, refreshNuevo);
    }

    @Cuando("Ana reutiliza el refresh token original")
    public void reutilizarRefreshOriginal() {
        actor().attemptsTo(
                RenovarToken.usando("refreshTokenOriginal")
        );
    }

    @Cuando("Ana consulta su perfil con el nuevo access token")
    public void consultarPerfilNuevoToken() {
        String token = actor().recall("nuevoAccessToken");
        assertNotNull(token);

        actor().attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(req -> req.header(
                                "Authorization", "Bearer " + token
                        ))
        );
    }

    @Cuando("Ana cierra su sesión")
    public void cerrarSesion() {
        actor().attemptsTo(CerrarSesion.actual());
    }

    @Cuando("Ana consulta su perfil con el access token cerrado")
    public void consultarPerfilTokenCerrado() {
        String token = actor().recall("accessToken");
        assertNotNull(token);

        actor().attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(req -> req.header(
                                "Authorization", "Bearer " + token
                        ))
        );
    }

    @Cuando("Ana intenta iniciar sesión cinco veces con credenciales incorrectas:")
    public void intentosFallidos(DataTable tabla) {
        List<Map<String, String>> filas = tabla.asMaps(
                String.class, String.class
        );

        assertEquals(5, filas.size());

        List<Integer> codigosHttp = new ArrayList<>();
        List<String> codigosError = new ArrayList<>();

        for (Map<String, String> fila : filas) {
            actor().attemptsTo(
                    IniciarSesion.conCredenciales(
                            fila.get("usuario"),
                            fila.get("clave")
                    )
            );

            codigosHttp.add(SerenityRest.lastResponse().statusCode());
            codigosError.add(
                    SerenityRest.lastResponse()
                            .jsonPath().getString("codigo")
            );
        }

        actor().remember("codigosHttpFallidos", codigosHttp);
        actor().remember("codigosErrorFallidos", codigosError);
    }


    @Entonces("se verifican los codigos HTTP y de error de los cinco intentos")
    public void verificarIntentosFallidos() {

        List<Integer> http = actor().recall("codigosHttpFallidos");
        List<String> errores = actor().recall("codigosErrorFallidos");

        assertNotNull(http);
        assertNotNull(errores);
        assertEquals(5, http.size());
        assertEquals(5, errores.size());

        for (int i = 0; i < 5; i++) {

            int httpEsperado = (i == 4) ? 423 : 401;

            String errorEsperado = (i == 4)
                    ? "CUENTA_BLOQUEADA"
                    : "CREDENCIALES_INVALIDAS";

            assertEquals(
                    httpEsperado,
                    http.get(i),
                    "HTTP incorrecto en intento " + (i + 1)
            );

            assertEquals(
                    errorEsperado,
                    errores.get(i),
                    "Código de error incorrecto en intento " + (i + 1)
            );

            System.out.println(
                    "Intento " + (i + 1)
                            + " - HTTP " + http.get(i)
                            + " - codigo: " + errores.get(i)
            );
        }
    }


    @Cuando("Ana intenta ingresar con la clave correcta del usuario {string}")
    public void ingresarConClaveCorrecta(String usuario) {
        // Credencial de prueba correspondiente al entorno local.
        actor().attemptsTo(
                IniciarSesion.conCredenciales(usuario, "Cibertec2026")
        );
    }

    @Cuando("Ana intenta listar todos los alumnos")
    public void listarAlumnos() {
        String token = actor().recall("accessToken");
        assertNotNull(token);

        actor().attemptsTo(
                Get.resource("/api/alumnos")
                        .with(req -> req.header(
                                "Authorization", "Bearer " + token
                        ))
        );
    }

    @Y("Ana registra el codigo de error de seguridad")
    public void registrarCodigoError() {
        String codigo = SerenityRest.lastResponse()
                .jsonPath().getString("codigo");

        assertNotNull(codigo, "La API no devolvió codigo de error");
        assertFalse(codigo.isBlank());

        System.out.println("Código de error de seguridad: " + codigo);
    }
}
