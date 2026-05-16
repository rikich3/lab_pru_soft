def evaluar_rendimiento(nota):
    if not isinstance(nota, int):
        raise ValueError("La nota debe ser entera")
        
    if nota < 0 or nota > 20:
        raise ValueError("Nota fuera de rango")
        
    if 0 <= nota <= 10:
        return "Insuficiente"
    elif 11 <= nota <= 15:
        return "Regular"
    else:
        return "Excelente"