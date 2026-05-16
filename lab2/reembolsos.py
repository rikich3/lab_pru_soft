def calcular_reembolso(monto, horas, es_vip):
    if monto < 0:
        raise ValueError("El monto no puede ser negativo")
        
    if horas > 72:
        porcentaje = 1.0
    elif 24 <= horas <= 72:
        porcentaje = 0.5
    else:
        porcentaje = 0.0
        
    # Prioridad Regla VIP: Si es VIP y el reembolso es < 50%, se le da el 50%
    if es_vip and porcentaje < 0.5:
        porcentaje = 0.5
        
    return monto * porcentaje