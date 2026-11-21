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


# PREGUNTA 2 – PRUEBAS DE SOFTWARE

## Resultados de los escenarios 1, 2, 3 y 4

Se realizaron pruebas de seguridad de la API de matrícula, verificando la rotación del refresh token, la invalidación de sesiones, el bloqueo de cuentas por intentos fallidos y las restricciones de acceso según el rol del usuario.

**Alumno de prueba:** i202610001 – Ana Torres  
**Alumno utilizado para bloqueo:** i202610004 – Jorge Huamán  
**Fecha de ejecución:** 21/11/2026  
**API:** http://localhost:8080  
**Tecnologías:** Cucumber, Serenity BDD, Screenplay y REST Assured.

---

## Escenario 1: Rotación del refresh token

Se verifica que la API permita renovar los tokens de autenticación, invalide el refresh token original después de utilizarlo y revoque los tokens de la sesión cuando se detecta su reutilización.

### Iniciar sesión

**Endpoint:** POST /api/auth/login

**Código HTTP:** 200 OK

**Petición enviada:**

```json
{
  "username": "i202610001",
  "password": "Cibertec2026"
}
```

**Respuesta obtenida:** HTTP 200, con un access token y un refresh token.

**Validación:** Se comprueba que ambos tokens existen y pueden almacenarse en la memoria del actor de Screenplay.

**Resultado:** CORRECTO – Inicio de sesión exitoso.

### Renovar el refresh token

**Endpoint:** POST /api/auth/refresh

**Código HTTP:** 200 OK

**Petición enviada:** Refresh token original obtenido durante el inicio de sesión.

**Respuesta obtenida:** Nuevos tokens de acceso y renovación.

**Validación:** Se comprueba que el nuevo access token y el nuevo refresh token son diferentes de los originales.

**Resultado:** CORRECTO – Tokens renovados.

### Reutilizar el refresh token original

**Endpoint:** POST /api/auth/refresh

**Código HTTP:** 401 Unauthorized

**Petición enviada:** El mismo refresh token original utilizado anteriormente.

**Campo de error verificado:**

```json
{
  "codigo": "REFRESH_REUTILIZADO"
}
```

**Validación:** La API rechaza la reutilización del refresh token y devuelve una respuesta compatible con el esquema `error.json`.

**Resultado:** CORRECTO – HTTP 401, código REFRESH_REUTILIZADO.

### Consultar el perfil con el nuevo access token

**Endpoint:** GET /api/auth/perfil

**Código HTTP:** 401 Unauthorized

**Petición enviada:** Nuevo access token obtenido durante la renovación.

**Campo de error verificado:**

```json
{
  "codigo": "TOKEN_INVALIDO"
}
```

**Validación:** Se comprueba que el nuevo access token queda invalidado después de detectar la reutilización del refresh token original.

**Resultado:** CORRECTO – HTTP 401, código TOKEN_INVALIDO.

---

## Escenario 2: Cierre de sesión

Se verifica que el usuario pueda cerrar su sesión correctamente y que el access token utilizado anteriormente deje de ser válido.

### Iniciar sesión

**Endpoint:** POST /api/auth/login

**Código HTTP:** 200 OK

**Petición enviada:**

```json
{
  "username": "i202610001",
  "password": "Cibertec2026"
}
```

**Validación:** Se obtiene y almacena el access token de la sesión.

**Resultado:** CORRECTO – Inicio de sesión exitoso.

### Cerrar sesión

**Endpoint:** POST /api/auth/logout

**Código HTTP:** 204 No Content

**Petición enviada:** Access token en la cabecera Authorization y refresh token de la sesión.

**Validación:** La API confirma el cierre de sesión mediante HTTP 204, sin contenido de respuesta.

**Resultado:** CORRECTO – Sesión cerrada.

### Consultar el perfil después del cierre de sesión

**Endpoint:** GET /api/auth/perfil

**Código HTTP:** 401 Unauthorized

**Petición enviada:** Access token utilizado antes del cierre de sesión.

**Campo de error verificado:**

```json
{
  "codigo": "TOKEN_INVALIDO"
}
```

**Validación:** La API impide reutilizar el access token después del logout y valida el esquema `error.json`.

**Resultado:** CORRECTO – HTTP 401, código TOKEN_INVALIDO.

---

## Escenario 3: Bloqueo de cuenta por intentos fallidos

Se verifica que la API bloquee una cuenta después de cinco intentos consecutivos de inicio de sesión con contraseñas incorrectas.

**Usuario de prueba:** i202610004 – Jorge Huamán

**Endpoint:** POST /api/auth/login

### Peticiones enviadas

Se realizaron cinco solicitudes consecutivas utilizando el mismo usuario y contraseñas incorrectas.

**Ejemplo de petición:**

```json
{
  "username": "i202610004",
  "password": "ClaveIncorrecta01"
}
```

Las siguientes solicitudes utilizaron las contraseñas `ClaveIncorrecta02`, `ClaveIncorrecta03`, `ClaveIncorrecta04` y `ClaveIncorrecta05`.

### Resultados obtenidos

| Intento | Contraseña | Código HTTP | Código de error |
|---|---|---|---|
| 1 | ClaveIncorrecta01 | 401 | CREDENCIALES_INVALIDAS |
| 2 | ClaveIncorrecta02 | 401 | CREDENCIALES_INVALIDAS |
| 3 | ClaveIncorrecta03 | 401 | CREDENCIALES_INVALIDAS |
| 4 | ClaveIncorrecta04 | 401 | CREDENCIALES_INVALIDAS |
| 5 | ClaveIncorrecta05 | 423 | CUENTA_BLOQUEADA |

**Campo de error obtenido durante los cuatro primeros intentos:**

```json
{
  "codigo": "CREDENCIALES_INVALIDAS"
}
```

**Campo de error obtenido en el quinto intento:**

```json
{
  "codigo": "CUENTA_BLOQUEADA"
}
```

### Intentar ingresar con la contraseña correcta

Después del quinto intento fallido, se realizó una nueva solicitud utilizando la contraseña correcta del usuario.

**Endpoint:** POST /api/auth/login

**Petición enviada:**

```json
{
  "username": "i202610004",
  "password": "Cibertec2026"
}
```

**Código HTTP:** 423 Locked

**Campo de error verificado:**

```json
{
  "codigo": "CUENTA_BLOQUEADA"
}
```

**Validación:** La API bloquea la cuenta en el quinto intento e impide el acceso incluso cuando se utiliza la contraseña correcta. También se comprueba que la respuesta final cumple el esquema `error.json`.

**Resultado:** CORRECTO – HTTP 423, código CUENTA_BLOQUEADA.

---

## Escenario 4: Alumno sin permiso de administrador

Se verifica que un usuario con rol ALUMNO no pueda acceder a un endpoint reservado para administradores.

### Iniciar sesión como alumno

**Endpoint:** POST /api/auth/login

**Código HTTP:** 200 OK

**Petición enviada:**

```json
{
  "username": "i202610001",
  "password": "Cibertec2026"
}
```

**Validación:** Se obtiene el access token correspondiente al usuario de prueba con rol ALUMNO.

**Resultado:** CORRECTO – Inicio de sesión exitoso.

### Intentar listar todos los alumnos

**Endpoint:** GET /api/alumnos

**Código HTTP:** 403 Forbidden

**Petición enviada:** Solicitud GET con el access token del alumno en la cabecera Authorization.

**Campo de error verificado:**

```json
{
  "codigo": "ACCESO_DENEGADO"
}
```

**Validación:** La API identifica que el usuario autenticado no tiene permisos de administrador y rechaza el acceso. Se comprueba que la respuesta cumple el esquema `error.json`.

**Resultado:** CORRECTO – HTTP 403, código ACCESO_DENEGADO.

---

## Resultados de las pruebas automatizadas BDD

Los cuatro escenarios fueron implementados mediante Cucumber, Serenity BDD, Screenplay y REST Assured.

| Escenario | Resultado |
|---|---|
| Escenario 1: Rotación del refresh token | PASSED |
| Escenario 2: Cierre de sesión | PASSED |
| Escenario 3: Bloqueo de cuenta por intentos fallidos | PASSED |
| Escenario 4: Alumno sin permiso de administrador | PASSED |

**Resultado de la ejecución conjunta:**

```text
Tests run: 12, Failures: 0, Errors: 0, Skipped: 8
BUILD SUCCESS
```

Los ocho casos omitidos corresponden a pruebas fuera del filtro de ejecución de la Pregunta 2.

**Fecha de ejecución conjunta:** 21/11/2026, 07:32 a. m.

### Archivos de automatización

- `src/test/resources/features/seguridad.feature`
- `src/test/java/pe/edu/cibertec/matricula_api/screenplay/stepdefinitions/SeguridadStepDefinitions.java`
- `src/test/java/pe/edu/cibertec/matricula_api/screenplay/task/IniciarSesion.java`
- `src/test/java/pe/edu/cibertec/matricula_api/screenplay/task/RenovarToken.java`
- `src/test/java/pe/edu/cibertec/matricula_api/screenplay/task/CerrarSesion.java`
- `src/test/resources/schemas/error.json`

### Evidencias

Las evidencias de ejecución de la Pregunta 2 se organizarán en el directorio `evidencias/P2/`, junto con los resultados de las pruebas y las capturas del reporte Serenity BDD.

### Conclusión

Los cuatro escenarios de la Pregunta 2 fueron aprobados durante su ejecución automatizada con Cucumber y Serenity BDD.

Se comprobó que la API realiza correctamente la rotación de refresh tokens, invalida las sesiones cerradas, bloquea cuentas después de cinco intentos fallidos y restringe el acceso a recursos administrativos según el rol del usuario.

**Resultado final: CORRECTO – 4 escenarios aprobados, 0 fallos y BUILD SUCCESS.**


# Pregunta 3: Calificaciones e historial académico con Serenity Screenplay

## 1. Objetivo

Validar el registro de calificaciones de los alumnos mediante pruebas automatizadas con Cucumber, Gherkin, Serenity BDD, Screenplay y Rest Assured.

Se comprueba que un docente pueda registrar notas en matrículas pagadas de los cursos que tiene asignados, que el sistema determine la condición del alumno y que el historial académico actualice correctamente los créditos aprobados y el promedio ponderado.

**Alumno de prueba:**

| Campo | Valor |
|---|---|
| Código | `i202610005` |
| Nombre | Rosa Medina |
| Carrera | CI |

**Docentes de prueba:**

| Código | Nombre | Curso PI5620 |
|---|---|---|
| D0001 | Marco Salazar | Sí |
| D0002 | Lucía Paredes | No |

**Curso evaluado:**

| Campo | Valor |
|---|---|
| Código | `PI5620` |
| Nombre | Proyecto Integrador |
| Créditos | 6 |
| Nota mínima aprobatoria | 13 |

---

## 2. Escenario 1: Docente registra nota e historial se actualiza

**Objetivo:** comprobar que el docente autorizado pueda registrar una calificación y que el historial académico refleje los resultados actualizados.

**Endpoint para registrar la nota:**

`PATCH /api/matriculas/{id}/nota`

El identificador de la matrícula se obtiene dinámicamente mediante `GET /api/matriculas`, utilizando el token de autenticación de la alumna.

**Request para la nota 14:**

```json
{
  "nota": 14
}
```

**Resultado:** HTTP `200 OK`.

La API devuelve la matrícula actualizada con la siguiente información:

```json
{
  "id": 2,
  "codigoAlumno": "i202610005",
  "codigoCurso": "PI5620",
  "periodo": "2026-2",
  "creditos": 6,
  "estado": "PAGADA",
  "nota": 14,
  "condicion": "APROBADO"
}
```

*Nota: el identificador `2` corresponde a la ejecución documentada. La prueba automatizada obtiene el ID dinámicamente.*

### Consultar historial académico

**Endpoint:**

`GET /api/alumnos/i202610005/historial`

**Resultado obtenido:**

| Indicador | Resultado |
|---|---:|
| Créditos aprobados | 13 |
| Promedio ponderado | 14.77 |
| Condición de PI5620 | APROBADO |

**Cálculo del promedio ponderado:**

Se consideran las calificaciones de los tres cursos con nota registrada:

- Análisis y Diseño de Sistemas: 16, con 3 créditos.
- Lenguaje de Programación II: 15, con 4 créditos.
- Proyecto Integrador: 14, con 6 créditos.

```text
Promedio = (16 × 3 + 15 × 4 + 14 × 6) / 13

Promedio = (48 + 60 + 84) / 13

Promedio = 192 / 13

Promedio = 14.77
```

Los 13 créditos corresponden a cursos aprobados.

**Resultado: CORRECTO – HTTP 200, condición APROBADO.**

### Segunda ejecución: nota 12,5

Se evalúa una segunda calificación para comprobar que el sistema identifique una nota desaprobatoria.

En el archivo `calificaciones.feature` se utiliza la notación decimal española `12,5`.

**Request enviado a la API:**

```json
{
  "nota": 12.5
}
```

**Resultado:** HTTP `200 OK`, con condición `DESAPROBADO`.

La API devuelve los siguientes campos de la matrícula actualizada:

```json
{
  "id": 2,
  "codigoAlumno": "i202610005",
  "codigoCurso": "PI5620",
  "periodo": "2026-2",
  "creditos": 6,
  "estado": "PAGADA",
  "nota": 12.5,
  "condicion": "DESAPROBADO"
}
```

**Resultado del historial académico:**

| Indicador | Resultado |
|---|---:|
| Créditos aprobados | 7 |
| Promedio ponderado | 14.08 |
| Condición de PI5620 | DESAPROBADO |

**Cálculo del promedio ponderado:**

```text
Promedio = (16 × 3 + 15 × 4 + 12.5 × 6) / 13

Promedio = (48 + 60 + 75) / 13

Promedio = 183 / 13

Promedio = 14.08
```

El promedio ponderado considera los créditos de todos los cursos que tienen calificación registrada, mientras que los créditos aprobados únicamente consideran los cursos con nota aprobatoria.

Por ello, los seis créditos del curso Proyecto Integrador no se consideran aprobados cuando la nota es `12,5`.

**Resultado: CORRECTO – HTTP 200, condición DESAPROBADO.**

---

## 3. Escenario 2: Docente que no dicta el curso intenta registrar una nota

**Objetivo:** comprobar que un docente no pueda calificar una matrícula de un curso que no tiene asignado.

El docente `D0002` intenta registrar una nota en el curso `PI5620`, que pertenece al docente `D0001`.

**Endpoint:**

`PATCH /api/matriculas/2/nota`

**Request:**

```json
{
  "nota": 16
}
```

**Resultado:** HTTP `403 Forbidden`.

**Response:**

```json
{
  "detail": "No tiene permisos sobre este recurso",
  "instance": "/api/matriculas/2/nota",
  "status": 403,
  "title": "Forbidden",
  "codigo": "ACCESO_DENEGADO"
}
```

**Validación:** se comprueba que el código HTTP sea `403` y que el campo `codigo` tenga el valor `ACCESO_DENEGADO`.

**Conclusión:** la API valida que el docente tenga asignado el curso antes de permitir el registro de una calificación.

**Resultado: CORRECTO – HTTP 403.**

---

## 4. Escenario 3: No se puede calificar una matrícula no pagada

**Objetivo:** comprobar que el sistema impida registrar notas en matrículas que todavía no han sido pagadas.

La alumna Rosa Medina tiene una matrícula registrada, pero no pagada, en el curso `PS5511`.

**Endpoint:**

`PATCH /api/matriculas/3/nota`

**Request:**

```json
{
  "nota": 16
}
```

**Resultado:** HTTP `409 Conflict`.

**Response:**

```json
{
  "detail": "Solo se califican matrículas pagadas",
  "instance": "/api/matriculas/3/nota",
  "status": 409,
  "title": "Conflict",
  "codigo": "MATRICULA_NO_PAGADA"
}
```

**Validación:** se comprueba que el código HTTP sea `409` y que el campo `codigo` tenga el valor `MATRICULA_NO_PAGADA`.

**Conclusión:** la API impide registrar calificaciones cuando el estado de la matrícula no es `PAGADA`.

**Resultado: CORRECTO – HTTP 409.**

---

## 5. Implementación con Serenity Screenplay

Para automatizar los escenarios se implementaron los siguientes componentes:

| Archivo | Responsabilidad |
|---|---|
| `calificaciones.feature` | Define los escenarios en Gherkin español. |
| `CalificacionStepDefinitions.java` | Implementa los pasos de Cucumber. |
| `ObtenerMatricula.java` | Consulta las matrículas del alumno y obtiene dinámicamente el identificador. |
| `CalificarMatricula.java` | Registra la nota mediante PATCH. |
| `ConsultarHistorial.java` | Consulta el historial académico del alumno. |
| `ElHistorial.java` | Question para consultar créditos aprobados, promedio y condición. |
| `CalificacionesTestSuite.java` | Ejecuta la suite de pruebas de calificaciones. |

Los tokens de autenticación se almacenan en la memoria de cada Actor mediante `remember()` y se recuperan con `recall()`.

El identificador de la matrícula no se encuentra fijado en el código: se obtiene dinámicamente consultando las matrículas del alumno y filtrando por curso y estado.

**Consulta utilizada para obtener la matrícula pagada:**

`GET /api/matriculas?alumno=i202610005&curso=PI5620&estado=PAGADA`

**Consulta utilizada para obtener la matrícula no pagada:**

`GET /api/matriculas?alumno=i202610005&curso=PS5511&estado=REGISTRADA`

También se valida que la respuesta del historial académico cumpla con el esquema JSON:

`src/test/resources/schemas/historial.json`

---

## 6. Ejecución de pruebas

Para ejecutar los escenarios automatizados:

```powershell
.\mvnw.cmd clean test -Dtest=CalificacionesTestSuite
```

### Resultados de la ejecución

En la ejecución realizada se obtuvieron los siguientes resultados:

| Escenario | Resultado |
|---|---|
| Registrar nota 14 y actualizar historial | Correcto: HTTP 200 |
| Registrar nota 12,5 y actualizar historial | Correcto: HTTP 200 |
| Docente sin asignación intenta calificar | Correcto: HTTP 403 |
| Calificar matrícula no pagada | Correcto: HTTP 409 |

**Resultado de Maven:**

```text
Tests run: 4
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

**Fecha de ejecución:** 21/11/2026.

Los cuatro escenarios fueron ejecutados correctamente mediante Cucumber y Serenity BDD.

### Análisis del caso de la nota 12.5

Durante el desarrollo se identificó la importancia de utilizar correctamente la notación decimal en los escenarios de Cucumber.

El archivo `calificaciones.feature` utiliza la configuración:

```gherkin
# language: es
```

En una ejecución anterior se utilizó el valor `12.5` directamente en Gherkin y se observó una respuesta HTTP `400 Bad Request`, con el código de error `DATOS_INVALIDOS`.

La respuesta de error documentada fue:

```json
{
  "detail": "Datos de entrada inválidos",
  "instance": "/api/matriculas/2/nota",
  "status": 400,
  "title": "Bad Request",
  "codigo": "DATOS_INVALIDOS",
  "errores": {
    "nota": "debe ser menor que o igual a 20.0"
  }
}
```

El problema se relacionó con la interpretación del separador decimal en la configuración regional española de Cucumber.

Por ello, se utilizó la notación `12,5` en la tabla de ejemplos de Gherkin, mientras que la petición JSON mantiene el formato numérico `12.5`.

Después de realizar esta corrección se ejecutaron nuevamente los cuatro escenarios.

**Resultado de la ejecución corregida:**

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

La ejecución final confirmó que la nota decimal se registra correctamente, que la condición académica es `DESAPROBADO` y que el historial actualiza los créditos aprobados y el promedio ponderado.

---

## 7. Reporte Serenity BDD

El reporte de Serenity BDD se puede generar mediante el siguiente comando:

```powershell
.\mvnw.cmd serenity:aggregate
```

El reporte HTML se genera en:

`target/site/serenity/index.html`

Para abrirlo:

```powershell
start .\target\site\serenity\index.html
```

Este reporte permite consultar los escenarios automatizados, los pasos ejecutados y los resultados de las validaciones.

---

**Conclusión:** se implementaron y ejecutaron correctamente las pruebas automatizadas para validar el registro de calificaciones, los permisos de los docentes, la restricción de matrículas no pagadas y la actualización del historial académico.

Se verificó que el sistema calcula correctamente el promedio ponderado, determina la condición de aprobación y actualiza los créditos aprobados.

También se comprobó que la notación decimal española `12,5` permite ejecutar correctamente el escenario correspondiente.

**Resultado final: CORRECTO – 4 escenarios aprobados, 0 fallos, 0 errores y BUILD SUCCESS.**