import pytest
from validador import validar_contrasena

@pytest.mark.parametrize("password, esperado_valida, errores_esperados", [
    # TC-01: Contraseña válida mínima estricta (8 chars)
    ("Abcd@123", True, []),
    
    # TC-02: Contraseña larga válida
    ("SoyUnaPasswordReLarga1234!!!", True, []),
    
    # TC-03: Falla: Longitud menor a 8 (7 caracteres)
    ("Abc@123", False, ["Debe tener al menos 8 caracteres"]),
    
    # TC-04: Falla: Sin letra mayúscula
    ("abcdef@123", False, ["Debe contener al menos una letra mayúscula"]),
    
    # TC-05: Falla: Sin letra minúscula
    ("ABCDEF@123", False, ["Debe contener al menos una letra minúscula"]),
    
    # TC-06: Falla: Sin número
    ("Abcdefg@#", False, ["Debe contener al menos un número"]),
    
    # TC-07: Falla: Sin carácter especial permitido
    ("Abcdefg123", False, ["Debe contener al menos un carácter especial (!@#$%^&*)"]),
    
    # TC-08: Falla: Carácter especial no contemplado (ej. ~)
    ("Abcdefg1~", False, ["Debe contener al menos un carácter especial (!@#$%^&*)"]),
    
    # TC-09: Falla Múltiple: Faltan mayúsculas y números
    ("abcdefg@#", False, ["Debe contener al menos una letra mayúscula", "Debe contener al menos un número"]),
    
    # TC-10: Falla Múltiple: Cadena vacía
    ("", False, [
        "Debe tener al menos 8 caracteres", 
        "Debe contener al menos una letra mayúscula", 
        "Debe contener al menos una letra minúscula", 
        "Debe contener al menos un número", 
        "Debe contener al menos un carácter especial (!@#$%^&*)"
    ]),
    
    # TC-11: Falla: Contraseña contiene espacios solamente
    ("        ", False, [
        "Debe contener al menos una letra mayúscula", 
        "Debe contener al menos una letra minúscula", 
        "Debe contener al menos un número", 
        "Debe contener al menos un carácter especial (!@#$%^&*)"
    ])
])
def test_validar_contrasena_parametrizado(password, esperado_valida, errores_esperados):
    # Act
    resultado = validar_contrasena(password)
    
    # Assert
    assert resultado["valida"] == esperado_valida
    assert resultado["errores"] == errores_esperados

# TC-12: Entrada con tipo de dato diferente (ej. lista)
def test_validar_contrasena_tipo_invalido():
    # Arrange
    pass_invalido = ["A", "b", "1", "!"]
    
    # Act & Assert
    with pytest.raises(TypeError) as exc:
        validar_contrasena(pass_invalido)
    assert "La contraseña debe ser una cadena de texto" in str(exc.value)
