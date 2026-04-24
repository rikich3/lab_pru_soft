def check_parity(number):
    """Evalúa si un número es par o impar."""
    if number % 2 == 0:
        return "par"
    else:
        return "impar"

def main():
    try:
        # 1. Solicitar cuántos números desea ingresar
        cantidad_str = input("¿Cuántos números deseas ingresar? ")
        cantidad = int(cantidad_str)
        
        if cantidad <= 0:
            print("Por favor, ingresa un número mayor a 0.")
            return
            
        lista_numeros = []
        # 2. Pedir al usuario que ingrese cada número
        print(f"\nPor favor, ingresa los {cantidad} números enteros:")
        
        for i in range(cantidad):
            num = int(input(f"Número {i + 1}: "))
            lista_numeros.append(num)
            
        # 3. Recorrer la lista e imprimir si es par o impar
        print("\n--- Resultados ---")
        for num in lista_numeros:
            resultado = check_parity(num)
            print(f"El número {num} es {resultado}.")
            
    except ValueError:
        print("Error: Ingreso no válido. Debes ingresar números enteros.")

if __name__ == "__main__":
    main()
