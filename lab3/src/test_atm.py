import pytest
import threading
from atm import Atm

# --- Fixtures ---
@pytest.fixture
def cajero():
    """Fixture: retorna una cuenta de ATM con saldo inicial de 1000.0."""
    return Atm(1000.0)

# --- Pruebas de Inicialización ---
# TC-01: Inicialización con saldo por defecto
def test_atm_inicializacion_defecto():
    cajero_nuevo = Atm()
    assert cajero_nuevo.consultar_saldo() == 1000.0

# TC-02: Inicialización con saldo específico positivo
def test_atm_inicializacion_especifica():
    cajero_nuevo = Atm(5000.0)
    assert cajero_nuevo.consultar_saldo() == 5000.0

# TC-03: Inicialización con saldo negativo (Excepción)
def test_atm_inicializacion_negativa():
    with pytest.raises(ValueError) as exc:
        Atm(-200.0)
    assert "El saldo inicial no puede ser negativo" in str(exc.value)

# --- Pruebas de Consultar Saldo ---
# TC-04: Consultar saldo estándar
def test_consultar_saldo(cajero):
    assert cajero.consultar_saldo() == 1000.0

# --- Pruebas de Depósito ---
@pytest.mark.parametrize("monto, saldo_esperado", [
    (500.0, 1500.0),     # TC-05: Depósito válido normal
    (99999999.0, 100000999.0) # TC-08: Depósito monto muy alto
])
def test_depositar_valido(cajero, monto, saldo_esperado):
    exito, msg = cajero.depositar(monto)
    assert exito is True
    assert msg == "Depósito exitoso"
    assert cajero.consultar_saldo() == saldo_esperado

@pytest.mark.parametrize("monto", [
    0.0,    # TC-06: Monto cero
    -200.0  # TC-07: Monto negativo
])
def test_depositar_invalido(cajero, monto):
    with pytest.raises(ValueError) as exc:
        cajero.depositar(monto)
    assert "El monto a depositar debe ser mayor a 0" in str(exc.value)

# --- Pruebas de Retiro ---
@pytest.mark.parametrize("monto, saldo_esperado", [
    (300.0, 700.0),  # TC-09: Retiro válido
    (1000.0, 0.0)    # TC-10: Retiro límite exacto
])
def test_retirar_valido(cajero, monto, saldo_esperado):
    exito, msg = cajero.retirar(monto)
    assert exito is True
    assert msg == "Retiro exitoso"
    assert cajero.consultar_saldo() == saldo_esperado

# TC-11: Retiro inválido (fondos insuficientes)
def test_retirar_fondos_insuficientes(cajero):
    with pytest.raises(ValueError) as exc:
        cajero.retirar(1500.0)
    assert "Saldo insuficiente" in str(exc.value)
    assert cajero.consultar_saldo() == 1000.0  # Saldo intacto

@pytest.mark.parametrize("monto", [
    0.0,   # TC-12: Monto cero
    -50.0  # TC-13: Monto negativo
])
def test_retirar_monto_invalido(cajero, monto):
    with pytest.raises(ValueError) as exc:
        cajero.retirar(monto)
    assert "El monto a retirar debe ser mayor a 0" in str(exc.value)

# --- Pruebas de Flujos Mixtos y Tipos de Datos ---
# TC-14: Operaciones secuenciales exitosas
def test_operaciones_secuenciales_exitosas(cajero):
    cajero.depositar(200.0)
    cajero.retirar(150.0)
    assert cajero.consultar_saldo() == 1050.0

# TC-15: Operaciones secuenciales con intentos fallidos intercalados
def test_operaciones_secuenciales_con_fallos(cajero):
    with pytest.raises(ValueError):
        cajero.retirar(2000.0) # Falla, y debe mantener saldo intacto en 1000
    
    cajero.retirar(500.0)      # Exito
    assert cajero.consultar_saldo() == 500.0

# TC-16: Tipos de datos inválidos en operaciones
@pytest.mark.parametrize("monto_invalido", [
    "Doscientos",
    [100, 200],
    None
])
def test_operaciones_tipo_invalido(cajero, monto_invalido):
    with pytest.raises(TypeError) as exc:
        cajero.depositar(monto_invalido)
    assert "El monto debe ser numérico" in str(exc.value)
    
    with pytest.raises(TypeError) as exc:
        cajero.retirar(monto_invalido)
    assert "El monto debe ser numérico" in str(exc.value)

# --- Pruebas Extra Solicitadas ---

# TC-12 (Extra): Manejar comandos no definidos en la interfaz
def test_metodo_no_definido(cajero):
    with pytest.raises(AttributeError):
        cajero.trasladar_cuenta("cuenta_foranea_123")

# TC-13 (Extra): Error: input de diferente tipo (string específico)
def test_input_tipo_diferente_string(cajero):
    with pytest.raises(TypeError) as exc:
        cajero.depositar("1000.0")
    assert "El monto debe ser numérico" in str(exc.value)

# TC-14 (Extra): Manejo de Concurrencia de eventos
def test_concurrencia_eventos(cajero):
    # Condición inicial: saldo = 1000.0
    
    t1 = threading.Thread(target=cajero.depositar, args=(100.0,))
    t2 = threading.Thread(target=cajero.retirar, args=(200.0,))
    t3 = threading.Thread(target=cajero.depositar, args=(100.0,))
    t4 = threading.Thread(target=cajero.retirar, args=(50.0,))
    
    hilos = [t1, t2, t3, t4]
    
    for hilo in hilos:
        hilo.start()
        
    for hilo in hilos:
        hilo.join()
        
    # Saldo final esperado: 1000 + 100 - 200 + 100 - 50 = 950
    assert cajero.consultar_saldo() == 950.0
