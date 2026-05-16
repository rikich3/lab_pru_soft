import threading

class Atm:
    def __init__(self, saldo_inicial=1000.0):
        if not isinstance(saldo_inicial, (int, float)):
            raise TypeError("El saldo inicial debe ser numérico")
        if saldo_inicial < 0:
            raise ValueError("El saldo inicial no puede ser negativo")
        self.saldo = float(saldo_inicial)
        self.lock = threading.Lock()

    def consultar_saldo(self):
        return self.saldo

    def depositar(self, monto):
        if not isinstance(monto, (int, float)):
            raise TypeError("El monto debe ser numérico")
        if monto <= 0:
            raise ValueError("El monto a depositar debe ser mayor a 0")
        with self.lock:
            self.saldo += float(monto)
        return True, "Depósito exitoso"

    def retirar(self, monto):
        if not isinstance(monto, (int, float)):
            raise TypeError("El monto debe ser numérico")
        if monto <= 0:
            raise ValueError("El monto a retirar debe ser mayor a 0")
        with self.lock:
            if monto > self.saldo:
                raise ValueError("Saldo insuficiente para realizar el retiro")
            self.saldo -= float(monto)
        return True, "Retiro exitoso"
