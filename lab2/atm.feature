Feature: Proceso de Retiro en Cajero Automatico
  Scenario: Intento de retiro sin fondos suficientes
    Dado que mi saldo es 100
    Cuando intento retirar 150
    Entonces debo ver un mensaje de "Fondos Insuficientes"