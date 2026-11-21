
# language: es

Característica: Seguridad de la sesión
  Como usuario de la API de matrícula
  Quiero verificar los mecanismos de seguridad
  Para comprobar la protección de sesiones y permisos

  Antecedentes:
    Dado que Ana consulta la API de matricula

  Escenario: Rotación del refresh token
    Cuando Ana inicia sesión de seguridad con el usuario "i202610001" y clave "Cibertec2026"
    Entonces el codigo de respuesta es 200
    Y Ana guarda los tokens originales
    Cuando Ana renueva su refresh token original
    Entonces el codigo de respuesta es 200
    Y Ana recibe tokens nuevos
    Cuando Ana reutiliza el refresh token original
    Entonces el codigo de respuesta es 401
    Y la respuesta cumple el esquema "error.json"
    Y el codigo de error es "REFRESH_REUTILIZADO"
    Cuando Ana consulta su perfil con el nuevo access token
    Entonces el codigo de respuesta es 401
    Y la respuesta cumple el esquema "error.json"
    Y el codigo de error es "TOKEN_INVALIDO"

  Escenario: Cierre de sesión
    Cuando Ana inicia sesión de seguridad con el usuario "i202610001" y clave "Cibertec2026"
    Entonces el codigo de respuesta es 200
    Cuando Ana cierra su sesión
    Entonces el codigo de respuesta es 204
    Cuando Ana consulta su perfil con el access token cerrado
    Entonces el codigo de respuesta es 401
    Y la respuesta cumple el esquema "error.json"
    Y el codigo de error es "TOKEN_INVALIDO"

  Escenario: Bloqueo de cuenta por intentos fallidos
    Cuando Ana intenta iniciar sesión cinco veces con credenciales incorrectas:
      | usuario    | clave             |
      | i202610004 | ClaveIncorrecta01 |
      | i202610004 | ClaveIncorrecta02 |
      | i202610004 | ClaveIncorrecta03 |
      | i202610004 | ClaveIncorrecta04 |
      | i202610004 | ClaveIncorrecta05 |
    Entonces se verifican los codigos HTTP y de error de los cinco intentos
    Cuando Ana intenta ingresar con la clave correcta del usuario "i202610004"
    Entonces el codigo de respuesta es 423
    Y la respuesta cumple el esquema "error.json"
    Y el codigo de error es "CUENTA_BLOQUEADA"

  Escenario: Alumno sin permiso de administrador
    Cuando Ana inicia sesión de seguridad con el usuario "i202610001" y clave "Cibertec2026"
    Entonces el codigo de respuesta es 200
    Cuando Ana intenta listar todos los alumnos
    Entonces el codigo de respuesta es 403
    Y la respuesta cumple el esquema "error.json"
    Y el codigo de error es "ACCESO_DENEGADO"
