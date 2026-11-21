
package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Patch;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.junit.jupiter.api.Assertions.*;

public class MatriculaStepDefinitions {

    private String alumno;
    private Integer idMatricula;

    private Actor actor() {
        return theActorInTheSpotlight();
    }

    private String token() {
        String token = actor().recall("token");

        assertNotNull(token, "Primero debe iniciarse sesión");
        assertFalse(token.isBlank(), "El token está vacío");

        return token;
    }

    // ==========================================
    // ESCENARIO 1: COTIZACIÓN
    // ==========================================

    @When("Ana cotiza el curso {string} para el alumno {string}")
    public void cotizarCurso(String curso, String codigoAlumno) {
        alumno = codigoAlumno;

        actor().attemptsTo(
                Post.to("/api/matriculas/cotizacion")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token())
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "codigoAlumno", codigoAlumno,
                                        "cursos", List.of(curso)
                                ))
                        )
        );
    }

    @And("la cotizacion tiene {int} creditos")
    public void verificarCreditos(int creditos) {
        assertEquals(
                creditos,
                SerenityRest.lastResponse()
                        .jsonPath().getInt("creditos")
        );
    }

    @And("el subtotal es {int}")
    public void verificarSubtotal(int subtotal) {
        assertEquals(
                subtotal,
                SerenityRest.lastResponse()
                        .jsonPath().getDouble("subtotal"),
                0.001
        );
    }

    @And("el descuento es {int}")
    public void verificarDescuento(int descuento) {
        assertEquals(
                descuento,
                SerenityRest.lastResponse()
                        .jsonPath().getDouble("descuento"),
                0.001
        );
    }

    @And("el total es {int}")
    public void verificarTotal(int total) {
        assertEquals(
                total,
                SerenityRest.lastResponse()
                        .jsonPath().getDouble("total"),
                0.001
        );
    }

    // ==========================================
    // ESCENARIO 2: REGISTRO DE MATRÍCULA
    // ==========================================

    @When("Ana registra la matricula del curso {string} para el alumno {string}")
    public void registrarMatricula(String curso, String codigoAlumno) {
        alumno = codigoAlumno;

        actor().attemptsTo(
                Post.to("/api/matriculas")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token())
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "codigoAlumno", codigoAlumno,
                                        "codigoCurso", curso
                                ))
                        )
        );

        if (SerenityRest.lastResponse().statusCode() == 201) {
            idMatricula = SerenityRest.lastResponse()
                    .jsonPath().getInt("id");

            actor().remember("idMatricula", idMatricula);
        }
    }

    @And("el estado de la matricula es {string}")
    public void verificarEstado(String estado) {
        assertEquals(
                estado,
                SerenityRest.lastResponse()
                        .jsonPath().getString("estado")
        );
    }

    @And("el monto de la matricula es {int}")
    public void verificarMonto(int monto) {
        assertEquals(
                monto,
                SerenityRest.lastResponse()
                        .jsonPath().getDouble("monto"),
                0.001
        );
    }

    // ==========================================
    // ESCENARIO 2: PAGO DE MATRÍCULA
    // ==========================================

    @When("Ana paga la matricula registrada con {string}")
    public void pagarMatricula(String metodo) {
        idMatricula = actor().recall("idMatricula");

        assertNotNull(
                idMatricula,
                "No existe ID de matrícula"
        );

        actor().attemptsTo(
                Patch.to("/api/matriculas/" + idMatricula + "/pagar")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token())
                                .contentType(ContentType.JSON)
                                .body(Map.of("metodo", metodo))
                        )
        );
    }

    @And("la matricula tiene fecha de pago")
    public void verificarFechaPago() {
        String fecha = SerenityRest.lastResponse()
                .jsonPath().getString("fechaPago");

        assertNotNull(
                fecha,
                "La matrícula no tiene fecha de pago"
        );

        assertFalse(
                fecha.isBlank(),
                "La fecha de pago está vacía"
        );
    }

    // ==========================================
    // ESCENARIO 2: CONSULTAR PAGOS
    // ==========================================

    @When("Ana consulta sus pagos")
    public void consultarPagos() {
        assertNotNull(alumno, "No se ha definido el alumno");

        actor().attemptsTo(
                Get.resource("/api/pagos")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token())
                                .queryParam("alumno", alumno)
                        )
        );
    }

    @And("existe un pago asociado a la matricula con metodo {string}")
    public void verificarPago(String metodo) {
        assertNotNull(idMatricula, "No existe ID de matrícula");

        List<Map<String, Object>> pagos =
                SerenityRest.lastResponse()
                        .jsonPath().getList("$");

        assertNotNull(pagos, "La lista de pagos es nula");

        boolean encontrado = pagos.stream().anyMatch(pago -> {
            Object idPago = pago.get("idMatricula");

            return idPago instanceof Number
                    && idMatricula.equals(((Number) idPago).intValue())
                    && metodo.equals(pago.get("metodo"))
                    && alumno.equals(pago.get("codigoAlumno"));
        });

        assertTrue(
                encontrado,
                "No se encontró el pago asociado a la matrícula"
        );
    }

    @And("el numero de operacion tiene el formato requerido")
    public void verificarNumeroOperacion() {
        assertNotNull(idMatricula, "No existe ID de matrícula");

        List<Map<String, Object>> pagos =
                SerenityRest.lastResponse()
                        .jsonPath().getList("$");

        assertNotNull(pagos, "La lista de pagos es nula");

        boolean valido = pagos.stream().anyMatch(pago -> {
            Object idPago = pago.get("idMatricula");
            Object numeroOperacion = pago.get("numeroOperacion");

            return idPago instanceof Number
                    && idMatricula.equals(((Number) idPago).intValue())
                    && numeroOperacion instanceof String
                    && ((String) numeroOperacion).matches("OP-\\d{6}");
        });

        assertTrue(
                valido,
                "El número de operación no tiene el formato OP-000000"
        );
    }

    // ==========================================
    // ESCENARIO 3: LÍMITE DE CRÉDITOS
    // ==========================================

    @When("Ana cotiza simultaneamente los cursos:")
    public void cotizarVariosCursos(DataTable tabla) {
        List<String> cursos = tabla.asList();

        assertFalse(
                cursos.isEmpty(),
                "La lista de cursos no puede estar vacía"
        );

        actor().attemptsTo(
                Post.to("/api/matriculas/cotizacion")
                        .with(req -> req
                                .header("Authorization", "Bearer " + token())
                                .contentType(ContentType.JSON)
                                .body(Map.of(
                                        "codigoAlumno", "i202610001",
                                        "cursos", cursos
                                ))
                        )
        );
    }
}
