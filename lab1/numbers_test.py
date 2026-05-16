import unittest
from unittest.mock import patch
import io
# Importamos las funciones desde nuestro archivo numbers.py
from numbers import check_parity, main

class TestParityIdentifier(unittest.TestCase):
    def test_check_parity_logic(self):
        """Prueba la lógica de identificación de pares e impares (incluye 0 y negativos)."""
        self.assertEqual(check_parity(4), "par")
        self.assertEqual(check_parity(7), "impar")
        self.assertEqual(check_parity(-2), "par")
        self.assertEqual(check_parity(0), "par")
        self.assertEqual(check_parity(-5), "impar")

    @patch('builtins.input', side_effect=['3', '10', '-3', '0'])
    @patch('sys.stdout', new_callable=io.StringIO)
    def test_main_flow(self, mock_stdout, mock_input):
        main()
        output = mock_stdout.getvalue()
        # Verificamos que la salida contenga los resultados correctos
        self.assertIn("El número 10 es par.", output)
        self.assertIn("El número -3 es impar.", output)
        self.assertIn("El número 0 es par.", output)

if __name__ == '__main__':
    unittest.main()
