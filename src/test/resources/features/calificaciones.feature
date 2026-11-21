# language: es

Característica: Calificación de matrículas
  Como docente de CIBERTEC
  Quiero registrar las notas de mis alumnos
  Para actualizar su historial académico y promedio ponderado

  Esquema del escenario: Docente registra nota e historial se actualiza
    Dado que el alumno "i202610005" inicia sesión correctamente
    Y obtiene el identificador de su matrícula pagada del curso "PI5620"
    Cuando el docente "D0001" inicia sesión correctamente
    Y registra la nota <nota> en la matrícula obtenida
    Entonces el código de respuesta es 200
    Y la condición de la matrícula es "<condicion>"
    Dado que el alumno "i202610005" inicia sesión correctamente
    Y consulta su historial académico
    Entonces el código de respuesta es 200
    Y los créditos aprobados del historial son <creditos>
    Y el promedio ponderado del historial es <promedio>
    Y la respuesta cumple el esquema "historial.json"

    Ejemplos:
        | nota | condicion   | creditos | promedio |
        | 14   | APROBADO    | 13       | 14,77    |
        | 12,5 | DESAPROBADO | 7        | 14,08    |

  Escenario: Docente que no dicta el curso intenta registrar una nota
    Dado que el alumno "i202610005" inicia sesión correctamente
    Y obtiene el identificador de su matrícula pagada del curso "PI5620"
    Cuando el docente "D0002" inicia sesión correctamente
    Y registra la nota 16 en la matrícula obtenida
    Entonces el código de respuesta es 403
    Y el código de error es "ACCESO_DENEGADO"

  Escenario: No se puede calificar una matrícula no pagada
    Dado que el alumno "i202610005" inicia sesión correctamente
    Y obtiene el identificador de su matrícula no pagada del curso "PS5511"
    Cuando el docente "D0001" inicia sesión correctamente
    Y registra la nota 16 en la matrícula obtenida
    Entonces el código de respuesta es 409
    Y el código de error es "MATRICULA_NO_PAGADA"