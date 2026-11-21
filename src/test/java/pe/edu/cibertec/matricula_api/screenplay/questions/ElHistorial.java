package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ElHistorial {

    public static Question<Integer> creditosAprobados() {
        return actor -> SerenityRest.lastResponse()
                .jsonPath()
                .getInt("creditosAprobados");
    }

    public static Question<Double> promedioPonderado() {
        return actor -> SerenityRest.lastResponse()
                .jsonPath()
                .getDouble("promedioPonderado");
    }

    public static Question<String> condicionDeMatricula() {
        return actor -> SerenityRest.lastResponse()
                .jsonPath()
                .getString("condicion");
    }
}