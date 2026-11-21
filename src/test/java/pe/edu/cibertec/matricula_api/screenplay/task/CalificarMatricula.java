package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;

public class CalificarMatricula implements Task {

    private final double nota;

    public CalificarMatricula(double nota) {
        this.nota = nota;
    }

    public static CalificarMatricula conNota(double nota) {
        return new CalificarMatricula(nota);
    }

    @Override
    @Step("{0} registra la nota #nota en la matrícula")
    public <T extends Actor> void performAs(T actor) {

        Integer idMatricula = actor.recall("idMatricula");
        String token = actor.recall("accessToken");

        if (idMatricula == null) {
            throw new AssertionError(
                    "El docente no tiene el identificador de la matrícula"
            );
        }

        if (token == null || token.isBlank()) {
            throw new AssertionError(
                    "El docente no tiene una sesión activa"
            );
        }

        actor.attemptsTo(
                Patch.to("/api/matriculas/{id}/nota")
                        .with(request -> request
                                .header("Authorization", "Bearer " + token)
                                .pathParam("id", idMatricula)
                                .contentType(ContentType.JSON)
                                .body(Map.of("nota", nota))
                        )
        );
        System.out.println("RESPUESTA DE CALIFICACIÓN: "
                + SerenityRest.lastResponse().asString());
    }
}