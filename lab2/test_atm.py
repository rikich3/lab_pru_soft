import pytest
from atm import ATM

def test_retiro_fondos_insuficientes():
    print("Ejecutando test_retiro_fondos_insuficientes")
    # Escenario BDD: "Dado que mi saldo es 100"
    cajero = ATM(100)
    
    # "Cuando intento retirar 150"
    resultado = cajero.retirar(150)
    
    # "Entonces debo ver un mensaje de Fondos Insuficientes"
    assert resultado == "Fondos Insuficientes"