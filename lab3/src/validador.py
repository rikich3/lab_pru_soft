import re

def validar_contrasena(password: str) -> dict:
    if not isinstance(password, str):
        raise TypeError("La contraseña debe ser una cadena de texto")
        
    errores = []
    
    if len(password) < 8:
        errores.append("Debe tener al menos 8 caracteres")
    
    if not any(c.isupper() for c in password):
        errores.append("Debe contener al menos una letra mayúscula")
        
    if not any(c.islower() for c in password):
        errores.append("Debe contener al menos una letra minúscula")
        
    if not any(c.isdigit() for c in password):
        errores.append("Debe contener al menos un número")
        
    if not bool(re.search(r"[!@#$%^&*]", password)):
        errores.append("Debe contener al menos un carácter especial (!@#$%^&*)")
        
    return {
        "valida": len(errores) == 0,
        "errores": errores
    }