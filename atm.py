class Cajero:
    def __init__(self, saldo_inicial=1000.0):
        self.saldo = saldo_inicial

    def consultar_saldo(self):
        return self.saldo

    def depositar(self, monto):
        if monto <= 0:
            return False, "Error: El monto a depositar debe ser mayor a 0."
        self.saldo += monto
        return True, f"Deposito exitoso. Su nuevo saldo es: S/. {self.saldo:.2f}"

    def retirar(self, monto):
        if monto <= 0:
            return False, "Error: El monto a retirar debe ser mayor a 0."
        if monto > self.saldo:
            return False, "Error: Saldo insuficiente para realizar el retiro."
        self.saldo -= monto
        return True, f"Retiro exitoso. Su nuevo saldo es: S/. {self.saldo:.2f}"

def ejecutar_menu():
    cajero = Cajero(1000.0)
    while True:
        print("\n--- CAJERO AUTOMÁTICO ---")
        print("1. Consultar Saldo")
        print("2. Depositar Dinero")
        print("3. Retirar Dinero")
        print("4. Salir")
        opcion = input("Seleccione una opción: ")

        if opcion == '1':
            print(f"Su saldo actual es: S/. {cajero.consultar_saldo():.2f}")
        elif opcion == '2':
            try:
                monto = float(input("Ingrese la cantidad a depositar: S/. "))
                exito, mensaje = cajero.depositar(monto)
                print(mensaje)
            except ValueError:
                print("Error: Por favor, ingrese un monto numérico válido.")
        elif opcion == '3':
            try:
                monto = float(input("Ingrese la cantidad a retirar: S/. "))
                exito, mensaje = cajero.retirar(monto)
                print(mensaje)
            except ValueError:
                print("Error: Por favor, ingrese un monto numérico válido.")
        elif opcion == '4':
            print("Gracias por usar nuestro cajero automático. ¡Hasta luego!")
            break
        else:
            print("Error: Opción no válida. Por favor, seleccione del 1 al 4.")

if __name__ == "__main__":
    ejecutar_menu()
