# P1 — Evidencias de pruebas BDD de matrícula

**Herramientas:** Cucumber, Serenity BDD, Java 21 y REST Assured.

**Resultado de ejecución:** 3 escenarios aprobados, 0 fallos y 0 errores.

## Escenario 1: Cotizar curso

Se verifica la cotización del curso `PI5620` para el alumno `i202610001`, incluyendo créditos, subtotal, porcentaje de descuento y total.

**Petición:**

```json
{
  "codigoAlumno": "i202610001",
  "cursos": ["PI5620"]
}
```

**Respuesta HTTP 200:**

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

**Validación del cálculo:**

- Subtotal: 6 × S/ 120 = S/ 720.
- Descuento: 20 % de S/ 720 = S/ 144.
- Total: S/ 720 − S/ 144 = **S/ 576**.

**Resultado:** PASSED.

## Escenario 2: Registrar y pagar matrícula

Se verifica el registro de la matrícula del curso `PI5620`, su pago mediante `TARJETA` y la consulta del pago registrado.

**Petición de registro:**

```json
{
  "codigoAlumno": "i202610001",
  "codigoCurso": "PI5620"
}
```

**Respuesta HTTP 201:**

Se verifica que la matrícula tenga:

```json
{
  "estado": "REGISTRADA",
  "monto": 576
}
```

La respuesta completa debe cumplir el esquema `schemas/matricula.json`.

**Petición de pago:**

```json
{
  "metodo": "TARJETA"
}
```

**Respuesta HTTP 200:**

Se verifica que la matrícula tenga:

```json
{
  "estado": "PAGADA",
  "monto": 576
}
```

También se verifica que `fechaPago` tenga un valor válido y que la respuesta cumpla el esquema `schemas/matricula.json`.

**Consulta de pagos:**

`GET /api/pagos?alumno=i202610001`

Se comprueba que exista un pago asociado al identificador de la matrícula creada, con método `TARJETA` y número de operación que cumpla el formato `OP-000000` (seis dígitos).

**Resultado:** PASSED.

## Escenario 3: Rechazar cotización por límite de créditos

Se verifica que la API rechace una cotización que supera el máximo permitido de 24 créditos.

**Petición:**

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

**Respuesta HTTP 422:**

```json
{
  "status": 422,
  "codigo": "LIMITE_CREDITOS",
  "title": "Unprocessable Content",
  "detail": "El alumno sumaría 28 créditos; el máximo es 24",
  "instance": "/api/matriculas/cotizacion"
}
```

**Explicación de la regla de negocio:**

El alumno tiene 6 créditos matriculados en `PI5620`. Los seis cursos adicionales suman 22 créditos.

6 + 22 = **28 créditos**, superando el máximo permitido de 24.

La API rechaza la cotización con HTTP 422 y código `LIMITE_CREDITOS`. Las validaciones anteriores no impiden la solicitud porque el incumplimiento corresponde al límite acumulado de créditos.

Se verifica el esquema `schemas/error.json`.

**Resultado:** PASSED.

---

## Resultado de ejecución conjunta

| Escenario | Resultado |
|---|---|
| 1. Cotizar PI5620 | PASSED |
| 2. Registrar y pagar con TARJETA | PASSED |
| 3. Rechazar por límite de créditos | PASSED |

**Ejecución registrada:** 21/11/2026, 06:36.

**Resultado Maven:** `BUILD SUCCESS`.

**Nota:** Los JSON parciales del escenario 2 muestran únicamente los campos validados; el identificador de matrícula y las fechas se generan dinámicamente durante la ejecución.
