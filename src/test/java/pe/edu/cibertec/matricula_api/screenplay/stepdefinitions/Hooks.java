
package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    @Before
    public void beforeScenario() {

        OnStage.setTheStage(new OnlineCast());

        System.out.println("======================================");
        System.out.println("ESTUDIANTE: Rodrigo León");
        System.out.println("CODIGO: COMPLETAR_CODIGO_ESTUDIANTE");
        System.out.println("FECHA Y HORA: " +
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                ));
        System.out.println("======================================");
    }
}
