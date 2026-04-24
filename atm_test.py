import unittest
from atm import Cajero

class TestCajero(unittest.TestCase):
    def setUp(self):
        # Se ejecuta antes de cada prueba para reiniciar el saldo a 1000
        self.cajero = Cajero(1000.0)

    def test_consultar_saldo(self):
        self.assertEqual(self.cajero.consultar_saldo(), 1000.0)

    def test_deposito_valido(self):
        exito, msj = self.cajero.depositar(500)
        self.assertTrue(exito)
        self.assertEqual(self.cajero.consultar_saldo(), 1500.0)

    def test_deposito_negativo(self):
        exito, msj = self.cajero.depositar(-100)
        self.assertFalse(exito)
        self.assertEqual(self.cajero.consultar_saldo(), 1000.0) # Saldo intacto

    def test_retiro_valido(self):
        exito, msj = self.cajero.retirar(300)
        self.assertTrue(exito)
        self.assertEqual(self.cajero.consultar_saldo(), 700.0)

    def test_retiro_insuficiente(self):
        exito, msj = self.cajero.retirar(2000)
        self.assertFalse(exito)
        self.assertEqual(self.cajero.consultar_saldo(), 1000.0) # Saldo intacto

if __name__ == "__main__":
    unittest.main()
