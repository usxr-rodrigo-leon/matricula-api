
# language: es

Característica: Cotización, matrícula y pago de cursos
  Como alumno de CIBERTEC
  Quiero cotizar, matricularme y pagar mis cursos
  Para verificar las reglas de negocio de la API

  Antecedentes:
    Dado que Ana consulta la API de matricula
    Y Ana inicia sesión con sus credenciales

  Escenario: Cotizar el curso PI5620
    Cuando Ana cotiza el curso "PI5620" para el alumno "i202610001"
    Entonces el codigo de respuesta es 200
    Y la cotizacion tiene 6 creditos
    Y el subtotal es 720
    Y el descuento es 20
    Y el total es 576

  Escenario: Registrar y pagar una matricula con TARJETA
    Cuando Ana registra la matricula del curso "PI5620" para el alumno "i202610001"
    Entonces el codigo de respuesta es 201
    Y el estado de la matricula es "REGISTRADA"
    Y el monto de la matricula es 576
    Y la respuesta cumple el esquema "matricula.json"
    Cuando Ana paga la matricula registrada con "TARJETA"
    Entonces el codigo de respuesta es 200
    Y el estado de la matricula es "PAGADA"
    Y la matricula tiene fecha de pago
    Y la respuesta cumple el esquema "matricula.json"
    Cuando Ana consulta sus pagos
    Entonces el codigo de respuesta es 200
    Y existe un pago asociado a la matricula con metodo "TARJETA"
    Y el numero de operacion tiene el formato requerido


  Escenario: Rechazar cotizacion por limite de creditos
    Cuando Ana cotiza simultaneamente los cursos:
      | PS5511 |
      | QA5517 |
      | DM5512 |
      | TS5514 |
      | SA5516 |
      | BD5410 |
    Entonces el codigo de respuesta es 422
    Y el codigo de error es "LIMITE_CREDITOS"
    Y la respuesta cumple el esquema "error.json"
