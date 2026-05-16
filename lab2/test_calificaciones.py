import pytest
from calificaciones import evaluar_rendimiento

def test_clases_validas():
    print("Ejecutando test_clases_validas")
    # Fronteras 0 - 10 (Insuficiente)
    assert evaluar_rendimiento(0) == "Insuficiente"
    assert evaluar_rendimiento(10) == "Insuficiente"
    
    # Fronteras 11 - 15 (Regular)
    assert evaluar_rendimiento(11) == "Regular"
    assert evaluar_rendimiento(15) == "Regular"
    
    # Fronteras 16 - 20 (Excelente)
    assert evaluar_rendimiento(16) == "Excelente"
    assert evaluar_rendimiento(20) == "Excelente"

def test_clases_invalidas():
    print("Ejecutando test_clases_invalidas")
    # Verificación de excepciones para valores fuera de rango
    with pytest.raises(ValueError):
        evaluar_rendimiento(-1)
    with pytest.raises(ValueError):
        evaluar_rendimiento(21)