# PREGUNTA 1 – PRUEBAS DE SOFTWARE

## Resultados de los escenarios 1, 2 y 3

Se realizaron pruebas de cotización, matrícula, pago y validación del límite de créditos mediante la API de matrícula.

**Alumno:** i202610001 – Ana Torres  
**Curso:** PI5620 – Proyecto Integrador  
**Periodo académico:** 2026-2  
**Método de pago:** TARJETA  
**Fecha de ejecución:** 21/11/2026

---

## Escenario 1: Cotizar curso

Se verifica la cotización del curso PI5620, los créditos, el subtotal, el descuento y el total.

**Endpoint:** `POST /api/matriculas/cotizacion`

**Código HTTP:** `200 OK`

### Petición enviada

```json
{
  "codigoAlumno": "i202610001",
  "cursos": ["PI5620"]
}
```

### Respuesta JSON obtenida

```json
{
  "codigoAlumno": "i202610001",
  "cursos": ["PI5620"],
  "creditos": 6,
  "subtotal": 720,
  "descuento": 20,
  "total": 576
}
```

**Validación:** El curso tiene 6 créditos a S/ 120 cada uno. El subtotal es S/ 720, se aplica un descuento del 20 % (S/ 144) y el total es S/ 576.

**Resultado: CORRECTO – HTTP 200.**

---

## Escenario 2: Registrar y pagar matrícula

Se verifica que la matrícula se registre correctamente, que el pago con TARJETA cambie su estado a PAGADA y que exista un pago asociado.

### Registrar matrícula

**Endpoint:** `POST /api/matriculas`

**Código HTTP:** `201 Created`

**Petición enviada:**

```json
{
  "codigoAlumno": "i202610001",
  "codigoCurso": "PI5620"
}
```

**Respuesta JSON obtenida:**

```json
{
  "id": 21,
  "codigoAlumno": "i202610001",
  "codigoCurso": "PI5620",
  "periodo": "2026-2",
  "horario": "VIERNES 08:00-14:00",
  "creditos": 6,
  "monto": 576,
  "estado": "REGISTRADA",
  "nota": null,
  "condicion": null,
  "fechaRegistro": "2026-11-21T05:57:16.0291563",
  "fechaPago": null
}
```

**Resultado: CORRECTO – Matrícula REGISTRADA.**

### Pagar matrícula con TARJETA

**Endpoint:** `PATCH /api/matriculas/21/pagar`

**Código HTTP:** `200 OK`

**Petición enviada:**

```json
{
  "metodo": "TARJETA"
}
```

**Respuesta JSON obtenida:**

```json
{
  "id": 21,
  "codigoAlumno": "i202610001",
  "codigoCurso": "PI5620",
  "periodo": "2026-2",
  "horario": "VIERNES 08:00-14:00",
  "creditos": 6,
  "monto": 576,
  "estado": "PAGADA",
  "nota": null,
  "condicion": null,
  "fechaRegistro": "2026-11-21T05:57:16.0291563",
  "fechaPago": "2026-11-21T05:59:17.5284488"
}
```

**Resultado: CORRECTO – Matrícula PAGADA con fecha de pago.**

### Consultar pago registrado

**Endpoint:** `GET /api/pagos?alumno=i202610001&metodo=TARJETA`

**Código HTTP:** `200 OK`

**Respuesta JSON obtenida:**

```json
[
  {
    "id": 16,
    "numeroOperacion": "OP-000016",
    "idMatricula": 21,
    "codigoAlumno": "i202610001",
    "codigoCurso": "PI5620",
    "periodo": "2026-2",
    "metodo": "TARJETA",
    "monto": 576,
    "fecha": "2026-11-21T05:59:17.528449"
  }
]
```

**Validación:** El pago corresponde a la matrícula 21, utiliza TARJETA y su número de operación cumple el formato `OP-` seguido de seis dígitos.

**Resultado: CORRECTO – Pago registrado y asociado a la matrícula.**

*Nota: Los identificadores y las fechas corresponden a la ejecución manual documentada. Las pruebas automatizadas obtienen el identificador de matrícula dinámicamente.*

---

## Escenario 3: Rechazar cotización por límite de créditos

Se verifica que la API rechace una cotización cuando el alumno supera el máximo permitido de 24 créditos.

**Endpoint:** `POST /api/matriculas/cotizacion`

**Código HTTP:** `422 Unprocessable Content`

### Petición enviada

```json
{
  "codigoAlumno": "i202610001",
  "cursos": [
    "PS5511",
    "QA5517",
    "DM5512",
    "TS5514",
    "SA5516",
    "BD5410"
  ]
}
```

### Respuesta JSON obtenida

```json
{
  "detail": "El alumno sumaría 28 créditos; el máximo es 24",
  "instance": "/api/matriculas/cotizacion",
  "status": 422,
  "title": "Unprocessable Content",
  "codigo": "LIMITE_CREDITOS"
}
```

### Validación del límite de créditos

| Curso | Créditos |
|---|---:|
| PS5511 | 3 |
| QA5517 | 3 |
| DM5512 | 4 |
| TS5514 | 4 |
| SA5516 | 4 |
| BD5410 | 4 |
| **Total solicitado** | **22** |
| Matrícula previa PI5620 | 6 |
| **Total acumulado** | **28** |

El alumno ya tiene 6 créditos matriculados. Los seis cursos adicionales suman 22 créditos.

**6 + 22 = 28 créditos.**

El límite permitido es de 24 créditos, por lo que la solicitud supera el máximo en 4 créditos.

**Resultado: CORRECTO – HTTP 422, código LIMITE_CREDITOS.**

---

## Resultados de las pruebas automatizadas BDD

Los tres escenarios fueron implementados mediante **Cucumber, Serenity BDD, Screenplay y REST Assured**.

| Escenario | Resultado |
|---|---|
| Escenario 1: Cotizar PI5620 | PASSED |
| Escenario 2: Registrar y pagar con TARJETA | PASSED |
| Escenario 3: Límite de créditos | PASSED |

**Resultado de la ejecución conjunta:**

```text
Tests run: 8, Failures: 0, Errors: 0, Skipped: 5
BUILD SUCCESS
```

Los cinco casos omitidos corresponden a pruebas fuera del filtro de ejecución.

**Archivos de automatización:**
- `src/test/resources/features/matricula.feature`
- `src/test/java/pe/edu/cibertec/matricula_api/screenplay/stepdefinitions/MatriculaStepDefinitions.java`
- `src/test/java/pe/edu/cibertec/matricula_api/screenplay/stepdefinitions/AutenticacionStepDefinitions.java`

**Conclusión:** Los tres escenarios de la Pregunta 1 fueron verificados manualmente mediante Swagger UI y aprobados en su ejecución automatizada con Cucumber y Serenity BDD.

