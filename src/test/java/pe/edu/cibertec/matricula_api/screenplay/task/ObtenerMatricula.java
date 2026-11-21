package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.rest.SerenityRest;

import java.util.List;
import java.util.Map;

public class ObtenerMatricula implements Task {

    private final String codigoCurso;
    private final String estado;

    public ObtenerMatricula(String codigoCurso, String estado) {
        this.codigoCurso = codigoCurso;
        this.estado = estado;
    }

    public static ObtenerMatricula delCurso(
            String codigoCurso,
            String estado
    ) {
        return new ObtenerMatricula(codigoCurso, estado);
    }

    @Override
    @Step("{0} obtiene la matrícula del curso #codigoCurso con estado #estado")
    public <T extends Actor> void performAs(T actor) {

        String token = actor.recall("accessToken");

        if (token == null || token.isBlank()) {
            throw new AssertionError("El alumno no tiene una sesión activa");
        }

        actor.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(request -> request
                                .header("Authorization", "Bearer " + token)
                                .queryParam("alumno", actor.getName())
                        )
        );

        if (SerenityRest.lastResponse().statusCode() != 200) {
            throw new AssertionError(
                    "No se pudieron consultar las matrículas. HTTP: "
                            + SerenityRest.lastResponse().statusCode()
            );
        }

        List<Map<String, Object>> matriculas =
                SerenityRest.lastResponse().jsonPath().getList("$");

        if (matriculas == null) {
            throw new AssertionError("La API no devolvió una lista de matrículas");
        }

        Map<String, Object> matricula = matriculas.stream()
                .filter(m -> codigoCurso.equals(m.get("codigoCurso")))
                .filter(m -> estado.equals(m.get("estado")))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No se encontró matrícula del curso "
                                + codigoCurso + " con estado " + estado
                ));

        Number id = (Number) matricula.get("id");

        if (id == null) {
            throw new AssertionError("La matrícula encontrada no tiene identificador");
        }

        actor.remember("idMatricula", id.intValue());
    }
}