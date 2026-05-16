# Casos de Prueba (Test Cases) - Laboratorio 3

A continuación se detallan los casos de prueba (TC) diseñados para cumplir con los requerimientos del informe. Se han contemplado contextos de uso reales, casos de éxito, límites (edge cases) y flujos de error.

---

## Ejercicio 2: Validador de Contraseñas

La función `validar_contrasena(password: str) -> dict` retorna `{"valida": bool, "errores": list}` en función de las reglas: longitud $\ge 8$, $1$ mayúscula, $1$ minúscula, $1$ número y $1$ carácter especial (`!@#$%^&*`).

| Id.   | Descripción                                      | Entrada (`password`) | Resultado Esperado (`valida`, `errores`) |
|-------|--------------------------------------------------|----------------------|------------------------------------------|
| TC-01 | Contraseña válida mínima estricta (8 chars)      | `"Abcd@123"`         | `True`, `[]`                             |
| TC-02 | Contraseña larga válida                          | `"SoyUnaPasswordReLarga1234!!!"` | `True`, `[]`                             |
| TC-03 | Falla: Longitud menor a 8 (7 caracteres)         | `"Abc@123"`          | `False`, `["Debe tener al menos 8 caracteres"]` |
| TC-04 | Falla: Sin letra mayúscula                       | `"abcdef@123"`       | `False`, `["Debe contener al menos una letra mayúscula"]` |
| TC-05 | Falla: Sin letra minúscula                       | `"ABCDEF@123"`       | `False`, `["Debe contener al menos una letra minúscula"]` |
| TC-06 | Falla: Sin número                                | `"Abcdefg@#"`        | `False`, `["Debe contener al menos un número"]` |
| TC-07 | Falla: Sin carácter especial permitido           | `"Abcdefg123"`       | `False`, `["Debe contener al menos un carácter especial (!@#$%^&*)"]` |
| TC-08 | Falla: Carácter especial no contemplado (ej. `~`) | `"Abcdefg1~"`       | `False`, `["Debe contener al menos un carácter especial (!@#$%^&*)"]` |
| TC-09 | Falla Múltiple: Faltan mayúsculas y números      | `"abcdefg@#"`        | `False`, `["Debe... mayúscula", "Debe... número"]` |
| TC-10 | Falla Múltiple: Cadena vacía                     | `""`                 | `False`, `["Debe tener al menos 8 caracteres", "Debe... mayúscula", "Debe... minúscula", "Debe... número", "Debe... carácter especial"]` |
| TC-11 | Falla: Contraseña contiene espacios solamente    | `"        "`         | `False`, `["Debe... mayúscula", "Debe... minúscula", "Debe... número", "Debe... carácter especial"]` |
| TC-12 | Entrada con tipo de dato diferente (ej. lista)   | `["A","b","1","!"]`  | Debe lanzar una excepción (ej. `TypeError`) o manejarse devolviendo tipo inválido |


---

## Ejercicio 3: Simulador de Cajero Automático (ATM)

Refactorizando la clase `Atm` (o `Cajero`) del LAB_01. Operaciones a testear: crear cuenta, depósitos, retiros y saldo.
*Se utilizarán fixtures para proveer un estado base y @pytest.mark.parametrize donde aplique.*

| Id.   | Descripción                                          | Precondición / Setup        | Acción / Entrada                       | Resultado Esperado / Saldo Restante          |
|-------|------------------------------------------------------|-----------------------------|----------------------------------------|----------------------------------------------|
| TC-01 | Inicialización con saldo por defecto                 | Nueva instancia de `Atm`    | `Atm()`                                | Saldo debe ser el inicial (`1000.0` o `0`)   |
| TC-02 | Inicialización con saldo específico positivo         | Nueva instancia de `Atm`    | `Atm(5000.0)`                          | Saldo = `5000.0`                             |
| TC-03 | Inicialización con saldo negativo (Casos Límite)     | Nueva instancia de `Atm`    | `Atm(-200.0)`                          | Debe manejar error (excepción) o poner 0     |
| TC-04 | Consultar saldo estándar                             | Cuenta con Saldo `1000.0`   | `consultar_saldo()`                    | Obtiene `1000.0`                             |
| TC-05 | Depósito válido (caso normal)                        | Cuenta con Saldo `1000.0`   | `depositar(500.0)`                     | Retorna Éxito. Saldo final = `1500.0`        |
| TC-06 | Depósito de monto cero (caso frontera)               | Cuenta con Saldo `1000.0`   | `depositar(0.0)`                       | Falla/Error ("Monto debe ser mayor a 0")     |
| TC-07 | Depósito de monto negativo (caso frontera)           | Cuenta con Saldo `1000.0`   | `depositar(-200.0)`                    | Falla/Error ("Monto debe ser mayor a 0")     |
| TC-08 | Depósito con monto muy alto (Stress)                 | Cuenta con Saldo `1000.0`   | `depositar(99999999.0)`                | Retorna Éxito. Saldo final esperado incrementado |
| TC-09 | Retiro válido (menor al saldo)                       | Cuenta con Saldo `1000.0`   | `retirar(300.0)`                       | Retorna Éxito. Saldo final = `700.0`         |
| TC-10 | Retiro límite: Monto exactamente igual al saldo total| Cuenta con Saldo `1000.0`   | `retirar(1000.0)`                      | Retorna Éxito. Saldo final = `0.0`           |
| TC-11 | Retiro inválido: Monto supera saldo (fondos insufic.)| Cuenta con Saldo `1000.0`   | `retirar(1500.0)`                      | Falla ("Saldo insuficiente"). Saldo = `1000.0`|
| TC-12 | Retiro inválido: Monto igual a cero                  | Cuenta con Saldo `1000.0`   | `retirar(0.0)`                         | Falla ("Monto debe ser mayor a 0")           |
| TC-13 | Retiro inválido: Monto negativo                      | Cuenta con Saldo `1000.0`   | `retirar(-50.0)`                       | Falla ("Monto debe ser mayor a 0")           |
| TC-14 | Operaciones secuenciales exitosas                    | Cuenta con Saldo `1000.0`   | `depositar(200.0)` luego `retirar(150.0)`| Retornos Exitosos. Saldo final = `1050.0`  |
| TC-15 | Operaciones secuenciales con intentos fallidos interp| Cuenta con Saldo `1000.0`   | `retirar(2000)` (Falla), `retirar(500)`(Exito)| Saldo final = `500.0`                  |
| TC-16 | Tipos de datos inválidos (cadenas de texto en montos)| Cuenta con Saldo `1000.0`   | `depositar("Doscientos")`              | Excepción `TypeError` / `ValueError`         |
