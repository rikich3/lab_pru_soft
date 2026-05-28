# Manual de Usuario: Habitflow - Seguimiento de Hábitos Premium

¡Bienvenido a **Habitflow**! Esta es una aplicación de escritorio premium diseñada con JavaFX y Java 21 para ayudarte a planificar, seguir y optimizar tus hábitos cotidianos de forma interactiva y visual.

La aplicación cuenta con rigurosas validaciones de lógica en tiempo real para evitar que guardes información contradictoria (como planificar más de 24 horas de actividades al día, nombres repetidos o registros en el futuro), lo que garantiza un funcionamiento robusto y libre de fallos lógicos.

---

## 📋 Índice
1. [Requisitos del Sistema](#-requisitos-del-sistema)
2. [Instrucciones de Ejecución](#-instrucciones-de-ejecución)
3. [Estructura de la Interfaz](#-estructura-de-la-interfaz)
4. [Guía Paso a Paso de Funcionalidades](#-guía-paso-a-paso-de-funcionalidades)
   - [Visualizar Métricas del Tablero](#1-visualizar-métricas-del-tablero)
   - [Crear un Nuevo Hábito](#2-crear-un-nuevo-hábito)
   - [Historial Checklist de los Últimos 5 Días](#3-historial-checklist-de-los-últimos-5-días)
   - [Editar un Hábito Existente](#4-editar-un-hábito-existente)
   - [Eliminar un Hábito](#5-eliminar-un-hábito)
5. [Reglas de Validación y Prevención de Errores](#-reglas-de-validación-y-prevención-de-errores)
6. [Persistencia de Datos (Guardado Automático)](#-persistencia-de-datos-guardado-automático)
7. [Comportamientos de Diseño Intencionales (No son Bugs)](#-comportamientos-de-diseño-intencionales-no-son-bugs)
8. [Robustez y Tolerancia a Fallos del Entorno](#-robustez-y-tolerancia-a-fallos-del-entorno)

---

## 💻 Requisitos del Sistema
- **Java Runtime Environment (JRE)**: Java 17 o superior (JDK 21 recomendado).
- **Sistema Operativo**: Compatible con todas las versiones de **Windows, Linux y macOS** de 64 bits.

---

## 🚀 Instrucciones de Ejecución

Existen dos maneras sencillas de lanzar la aplicación de escritorio en tu computadora:

### Método A: Usando el archivo JAR Ejecutable (Recomendado)
Este método es ideal para usuarios finales o evaluadores de pruebas de caja negra, ya que no requiere instalar herramientas de desarrollo aparte de Java:
1. Abre tu terminal o consola de comandos en la carpeta raíz del proyecto.
2. Ejecuta el siguiente comando:
   ```bash
   java -jar target/habit-tracker-1.0-SNAPSHOT.jar
   ```
   *(En sistemas operativos con interfaz gráfica habilitada, también puedes simplemente hacer doble clic sobre el archivo `.jar` ubicado en la carpeta `target/`)*.

### Método B: Ejecución con Maven (Desarrollo)
Si deseas ejecutar la aplicación directamente desde el código fuente utilizando Maven, ejecuta el siguiente comando en la raíz del proyecto:
```bash
mvn clean javafx:run
```

---

## 🎨 Estructura de la Interfaz

La ventana de Habitflow se divide en dos columnas principales diseñadas en un elegante **Tema Oscuro Premium (Dark Mode)**:

1. **Panel Izquierdo (Tablero Dinámico y Lista de Hábitos)**:
   - **Métricas Superiores (Stats Cards)**: Muestra el total de hábitos activos, el tiempo total estimado comprometido para el día de hoy, y la tasa de completitud general histórica de todos tus hábitos combinados.
   - **Lista de Hábitos**: Muestra tarjetas (cards) individuales para cada hábito con su nombre, duración diaria, fecha de inicio, progreso acumulado, barra de progreso visual, un panel interactivo de check-in para los últimos 5 días, y botones de edición/eliminación.
2. **Panel Derecho (Formulario de Gestión)**:
   - Contiene los campos de texto, selectores de fecha, selectores numéricos (spinners) y botones toggle para añadir, editar, configurar y guardar hábitos.

---

## 📖 Guía Paso a Paso de Funcionalidades

### 1. Visualizar Métricas del Tablero
Al abrir la aplicación, verás de inmediato las estadísticas actualizadas en tiempo real:
- **Hábitos Activos**: Cantidad total de hábitos registrados.
- **Tiempo Diario Estimado**: Suma acumulada de las duraciones diarias en horas y minutos planificadas únicamente para los hábitos que se ejecutan el día de hoy (según el día de la semana actual).
- **Completitud General**: El porcentaje de cumplimiento calculado dividiendo la cantidad total de días completados (incluyendo días del pasado y marcas actuales) entre el total de días calendarizados programados hasta la fecha.

---

### 2. Crear un Nuevo Hábito
En el formulario del panel derecho, rellena los siguientes campos:

1. **Nombre del Hábito**: Escribe un nombre descriptivo (Ej: *"Estudiar Java"*, *"Ir al gimnasio"*).
2. **Duración estimada al día**: Selecciona cuántas horas y minutos te tomará realizar este hábito cada día usando las flechas de los selectores o escribiendo el número directamente.
3. **Fecha de Inicio**: Selecciona la fecha en la que comenzó o comenzará tu hábito usando el calendario interactivo.
4. **Días cumplidos desde el inicio en el pasado** *(Dinámico e Inteligente)*:
   - **Si seleccionas el día de hoy**: Este spinner se deshabilitará automáticamente y se fijará en `0`, ya que no han pasado días previos desde el inicio del hábito.
   - **Si seleccionas una fecha en el pasado**: El spinner se habilitará de inmediato de forma inteligente. Calculará cuántos días programados han transcurrido en el calendario y pondrá ese número exacto como límite máximo en el spinner, evitando que ingreses un número mayor.
5. **Programación Semanal**: Haz clic en las letras de los días de la semana (**L, M, M, J, V, S, D**) para seleccionar los días específicos en los que deseas realizar el hábito. Puedes marcar desde un solo día a la semana hasta los siete días.

> [!NOTE]
> **Bloqueo Inteligente de Acciones**: El botón **"Guardar Hábito"** permanecerá **deshabilitado** hasta que todos los campos obligatorios contengan información válida (por ejemplo, el nombre no esté vacío, la duración sea mayor a 0 minutos, y hayas seleccionado al menos un día de la semana). Esto evita que guardes datos corruptos por error.

Una vez que completes el formulario, haz clic en **"Guardar Hábito"**. Verás un mensaje en verde confirmando el éxito y el nuevo hábito aparecerá de inmediato en el tablero izquierdo.

---

### 3. Historial Checklist de los Últimos 5 Días
Cada tarjeta de hábito en el panel izquierdo incluye una sección llamada **"Historial de los últimos 5 días"**:
- Esta sección muestra únicamente los días de los últimos 5 calendarizados (desde hoy hacia atrás) en los que **tienes programado realizar el hábito**. Si el hábito no está planificado para uno de esos días, no se mostrará nada.
- Cada día tiene un nombre abreviado (Ej: *"Hoy"*, *"Auer"*, *"Lun"*) y un checkbox.
- **Cómo usarlo**: Haz clic en el checkbox del día correspondiente para marcar el hábito como completado. Haz clic de nuevo para desmarcarlo.
- **Sincronización Inmediata**: Al marcar o desmarcar un casillero, la barra de progreso del hábito y las métricas superiores del tablero se recalculan e incrementan/decrementan **al instante**, sin necesidad de recargar la aplicación.

---

### 4. Editar un Hábito Existente
1. En la tarjeta del hábito que deseas modificar en la lista izquierda, haz clic en el botón **"Editar"**.
2. Los datos del hábito se cargarán automáticamente en el formulario del panel derecho. El título cambiará a **"Editar Hábito"** y el botón de guardar se transformará en **"Actualizar Hábito"**.
3. Modifica los campos que desees. 
4. Haz clic en **"Actualizar Hábito"** para guardar los cambios o haz clic en **"Limpiar"** si deseas cancelar la edición y vaciar el formulario.

---

### 5. Eliminar un Hábito
1. En la tarjeta del hábito que deseas borrar, haz clic en el botón rojo **"Eliminar"**.
2. Aparecerá una ventana de confirmación con el estilo premium de la aplicación preguntándote si estás seguro.
3. Haz clic en **"Aceptar"** (OK) para eliminarlo permanentemente o en **"Cancelar"** para conservarlo.
4. El hábito desaparecerá de inmediato del panel y el tablero se actualizará restando sus métricas.

---

## 🚫 Reglas de Validación y Prevención de Errores

Para garantizar un código limpio y libre de errores lógicos tanto para pruebas de caja blanca como para pruebas de caja negra, Habitflow aplica estrictamente las siguientes reglas internas:

| Regla de Validación | Comportamiento en la Interfaz | Acción del Código (`HabitService`) |
| :--- | :--- | :--- |
| **Nombres Únicos** | Si intentas guardar un hábito con un nombre idéntico a otro existente (sin importar mayúsculas/minúsculas o espacios al inicio/final), la app mostrará un texto en rojo informando el error. | Lanza una `ValidationException` indicando el conflicto de duplicidad de nombres. |
| **Duración Máxima Individual** | La interfaz limita el spinner de horas hasta 24 y el de minutos hasta 59. Si de alguna manera intentas sumar más de 24 horas para un solo hábito, el botón se bloquea o el sistema rechaza el guardado. | Verifica que la duración del hábito esté estrictamente en el intervalo $[1, 1440]$ minutos. |
| **Duración Máxima Acumulada** | Si registras varios hábitos activos en un mismo día de la semana (por ejemplo, los lunes) y la suma total de sus duraciones diarias planificadas supera las 24 horas (1440 minutos), el sistema rechazará el guardado con un mensaje de alerta. | Suma los minutos de los hábitos planificados para cada día de la semana y rechaza la inserción si excede las 24 horas. |
| **Fecha de Inicio en el Futuro** | El selector de fechas (`DatePicker`) no te permitirá guardar un hábito si la fecha de inicio es posterior al día de hoy. | Lanza una excepción si la fecha es estrictamente posterior a `LocalDate.now()`. |
| **Días Cumplidos en el Pasado** | El control dinámico limita el valor máximo del spinner para que nunca supere la cantidad exacta de días programados transcurridos desde la fecha de inicio hasta ayer. | Calcula la diferencia de días calendarizados programados entre la fecha de inicio y hoy, y valida que el valor ingresado esté en ese rango. |

---

## 💾 Persistencia de Datos (Guardado Automático)

No necesitas preocuparte por perder tu progreso al cerrar la aplicación:
- **Guardado Automático**: Cada acción que realizas (crear, editar, eliminar o marcar un hábito como completado) se escribe y guarda de forma automática e instantánea en el disco local en un archivo llamado **`habits.json`** en el directorio raíz de la aplicación.
- **Carga Automática**: Al iniciar la aplicación, Habitflow busca y carga este archivo para restablecer tu panel exactamente en el estado en que lo dejaste.
- **Fácil Reseteo**: Si por motivos de prueba deseas vaciar por completo la aplicación, simplemente elimina el archivo `habits.json` del directorio raíz antes de abrir el software.

- ## 🛡️ Comportamientos de Diseño Intencionales (No son Bugs)

Para evitar que la aplicación entre en estados lógicos inconsistentes o sufra corrupción de datos, se han implementado restricciones estrictas en la interfaz de usuario. Los siguientes comportamientos son **características de seguridad intencionales** y no deben ser reportados como fallos:

### 1. Bloqueo de escritura en el Selector de Fechas (DatePicker)
*   **Comportamiento:** No se puede escribir texto libremente con el teclado dentro de la caja de fecha.
*   **Razón de diseño:** Evita la inyección de formatos de fecha inválidos, texto aleatorio (como "hola") o fechas inexistentes (como "99/99/9999") que corrompan el flujo de datos. El usuario está obligado a seleccionar la fecha de manera segura mediante el calendario gráfico desplegable.

### 2. Spinners no editables por teclado
*   **Comportamiento:** Las flechas de selección de horas, minutos y días pasados requieren clics y no permiten la edición directa por teclado.
*   **Razón de diseño:** Protege al sistema de la entrada de caracteres alfabéticos, números negativos o desbordes numéricos fuera de los rangos lógicos permitidos por el dominio del negocio.

### 3. Spinner de "Días cumplidos en el pasado" deshabilitado en modo Edición
*   **Comportamiento:** Al editar un hábito existente, el campo numérico de días cumplidos en el pasado se muestra bloqueado (deshabilitado).
*   **Razón de diseño:** Una vez creado el hábito, el historial del pasado se migra de forma definitiva al mapa de historial (`history`) y se representa visualmente mediante checkboxes en el panel izquierdo. Para modificar el progreso de días anteriores, el usuario debe marcar o desmarcar directamente dichos checkboxes en el historial. Permitir modificar el spinner en edición duplicaría el conteo de días de manera inconsistente.

### 4. Normalización estricta de nombres duplicados
*   **Comportamiento:** Si intentas registrar "Hacer Ejercicio" existiendo ya un hábito llamado "Hacer   Ejercicio" (con múltiples espacios en medio), el sistema rechazará el registro.
*   **Razón de diseño:** El validador normaliza internamente los nombres eliminando espacios redundantes. Esto previene que existan tarjetas visualmente idénticas en el tablero que confundan al usuario final.

---

## ☣️ Robustez y Tolerancia a Fallos del Entorno

La aplicación cuenta con mecanismos de defensa pasiva para responder ante escenarios adversos del sistema operativo o manipulación externa:

### 1. Tolerancia a la corrupción del archivo de persistencia
*   **Comportamiento:** Si un tercero edita manualmente el archivo `habits.json` introduciendo caracteres inválidos que rompan la estructura JSON, la aplicación no colapsará (crash) en el arranque.
*   **Razón de diseño:** El cargador de persistencia captura excepciones de sintaxis (`JsonSyntaxException`), inicia un tablero vacío de forma segura para garantizar la disponibilidad del software y registra el aviso en la consola de diagnóstico.

### 2. Control de fallas de escritura en disco (Rollback transaccional)
*   **Comportamiento:** Si el archivo `habits.json` se encuentra bloqueado por el sistema operativo, marcado como "Solo lectura" o la aplicación se ejecuta en un directorio sin permisos de escritura, las operaciones de creación o edición mostrarán un mensaje de error en rojo y no se aplicarán.
*   **Razón de diseño:** Si la escritura física en disco falla, la aplicación realiza un "rollback" (marcha atrás) de la operación en memoria. Esto asegura que la interfaz visual no le mienta al usuario mostrando un hábito creado que se perderá inmediatamente al cerrar el software.
