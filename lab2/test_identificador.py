import pytest
from identificador import es_identificador_valido

def test_limites_longitud_myers():
    print("Ejecutando test_limites_longitud_myers")
    assert es_identificador_valido("") == False       # Longitud 0
    assert es_identificador_valido("A") == True       # Longitud 1
    assert es_identificador_valido("Var123") == True  # Longitud 6
    assert es_identificador_valido("Var1234") == False# Longitud 7

def test_reglas_formato():
    print("Ejecutando test_reglas_formato")
    assert es_identificador_valido("1var") == False   # Falla regla primer char
    assert es_identificador_valido("var_1") == False  # Falla char especial
    assert es_identificador_valido("v@r") == False    # Falla char especial