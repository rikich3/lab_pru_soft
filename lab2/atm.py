class ATM:
    def __init__(self, saldo_inicial):
        self.saldo = saldo_inicial
    
    def retirar(self, monto):
        if monto > self.saldo:
            return "Fondos Insuficientes"
        self.saldo -= monto
        return "Retiro Exitoso"