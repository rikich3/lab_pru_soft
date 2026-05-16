import re

def es_identificador_valido(identificador):
    # Refactorizacion: Validacion de entrada (Robustez)
    if not isinstance(identificador, str):
        return False
        
    # Validacion limites Myers (1 a 6 caracteres)
    if len(identificador) < 1 or len(identificador) > 6:
        return False
        
    # Regla: Primer caracter letra, resto letras o digitos
    patron = r'^[A-Za-z][A-Za-z0-9]*$'
    if not re.match(patron, identificador):
        return False
        
    return True