import pytest
from reembolsos import calcular_reembolso

def test_limites_reembolso():
    print("Ejecutando test_limites_reembolso")
    # Pruebas de fronteras de tiempo para clientes normales (No VIP)
    assert calcular_reembolso(100, 72, False) == 50.0  # Límite exacto 72h (50%)
    assert calcular_reembolso(100, 24, False) == 50.0  # Límite exacto 24h (50%)
    assert calcular_reembolso(100, 23, False) == 0.0   # Menos de 24h (0%)

def test_escenario_vip_bdd():
    print("Ejecutando test_escenario_vip_bdd")
    # Regla VIP: Aunque cancele con 2 horas (debería ser 0%), recibe 50%
    assert calcular_reembolso(200, 2, True) == 100.0   

def test_proteccion_errores():
    print("Ejecutando test_proteccion_errores")
    # Validación de robustez ante montos negativos
    with pytest.raises(ValueError):
        calcular_reembolso(-100, 48, False)