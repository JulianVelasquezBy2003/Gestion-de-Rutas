package modelo;

import java.time.LocalDate;
import java.util.Scanner;

import datos.Validacion;
import transporte.Bus;
import transporte.Boleto;
import transporte.Pasajero;
import transporte.Venta;
import transporte.Viaje;

/**
 * Usuario con rol Cajero/Counter. Se encarga de la venta y reserva de
 * pasajes: mostrar el mapa de asientos, registrar al pasajero, ocupar el
 * asiento elegido y generar el comprobante de venta.
  */
public class Cajero extends Usuario {

    public Cajero() {
    }

    public Cajero(int id, String nombre, String contraseña) {
        super(id, nombre, contraseña);
    }

    public Cajero(int id, String nombre, String contraseña, String rol) {
        super(id, nombre, contraseña, rol);
    }

    // ---------- Venta / Reserva de pasaje ----------

    /**
     * Flujo completo de venta o reserva de un pasaje para un viaje ya
     * seleccionado: muestra el mapa de asientos, pide la posicion deseada,
     * registra al pasajero, calcula el precio final (con descuentos si
     * corresponde) y genera el comprobante.
     *
     * @param viaje viaje sobre el cual se vende el pasaje (debe tener Bus y Ruta asignados)
     * @param sc Scanner compartido para leer la entrada del usuario
     * @return la Venta registrada, o null si la operacion se cancelo o no se pudo completar
     */
    public Venta venderPasaje(Viaje viaje, Scanner sc) {
        if (viaje == null || viaje.getBus() == null || viaje.getRuta() == null) {
            System.out.println("❌ El viaje seleccionado no tiene bus o ruta asignados.");
            return null;
        }

        Bus bus = viaje.getBus();
        bus.mostrarAsientos();

        // ---- 1. Elegir un asiento libre ----
        int fila = -1;
        int columna = -1;
        boolean asientoDisponible = false;
        while (!asientoDisponible) {
            fila = leerFila(sc, bus);
            columna = leerColumna(sc, bus);
            if (bus.asientoLibre(fila, columna)) {
                asientoDisponible = true;
            } else {
                System.out.println("❌ Ese asiento no esta disponible. Elija otro.\n");
            }
        }

        // ---- 2. Registrar datos del pasajero ----
        Pasajero pasajero = leerPasajero(sc);

        // ---- 3. Ocupar el asiento (doble verificacion por seguridad) ----
        if (!bus.ocuparAsiento(fila, columna)) {
            System.out.println("❌ No se pudo completar la venta: el asiento ya no esta disponible.");
            return null;
        }

        // ---- 4. Calcular precio con descuentos si aplica ----
        double precioBase = viaje.getRuta().getPrecioBase();
        double precioFinal = pasajero.calcularPrecioFinal(precioBase, pasajero.getEdad());

        // ---- 5. Registrar la venta ----
        String fechaHoy = LocalDate.now().toString();

        Venta venta = new Venta();
        venta.setIdVenta(Venta.generarSiguienteId());
        venta.setFecha(fechaHoy);
        venta.setPrecioFinal(precioFinal);
        venta.setPasajero(pasajero);
        venta.setViaje(viaje);

        // ---- 6. Generar boleto y comprobante ----
        Boleto boleto = new Boleto(Boleto.generarSiguienteId(), fechaHoy, fila, columna, precioFinal);

        System.out.println();
        boleto.emitirComprobante();
        System.out.println(venta.generarComprobante());
        System.out.println("\n✅ Venta registrada correctamente.\n");

        return venta;
    }

    // ---------- Helpers de lectura de datos ----------

    /**
     * Pide la fila del asiento como letra (A, B, C...) y la convierte a
     * indice 0-based, reintentando ante entradas invalidas.
     */
    private int leerFila(Scanner sc, Bus bus) {
        int filaIndex = -1;
        int totalFilas = bus.getAsientos().length;

        while (filaIndex < 0 || filaIndex >= totalFilas) {
            System.out.print("Ingrese la fila del asiento (Letra, ej. A): ");
            String texto = sc.nextLine().trim().toUpperCase();

            if (texto.length() == 1 && Character.isLetter(texto.charAt(0))) {
                filaIndex = texto.charAt(0) - 'A';
            } else {
                filaIndex = -1;
            }

            if (filaIndex < 0 || filaIndex >= totalFilas) {
                System.out.println("❌ Fila invalida. Debe ser una letra entre A y "
                        + (char) ('A' + totalFilas - 1) + ".");
            }
        }
        return filaIndex;
    }

    /**
     * Pide el numero de columna del asiento (1-based tal como lo ve el
     * usuario) y lo convierte a indice 0-based, reintentando ante
     * entradas invalidas o fuera de rango.
     */
    private int leerColumna(Scanner sc, Bus bus) {
        int columnaIndex = -1;
        int totalColumnas = bus.getAsientos()[0].length;

        while (columnaIndex < 0 || columnaIndex >= totalColumnas) {
            System.out.print("Ingrese el numero de asiento (1 a " + totalColumnas + "): ");
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                columnaIndex = valor - 1;
            } catch (NumberFormatException e) {
                columnaIndex = -1;
            }

            if (columnaIndex < 0 || columnaIndex >= totalColumnas) {
                System.out.println("❌ Numero de asiento invalido. Debe estar entre 1 y " + totalColumnas + ".");
            }
        }
        return columnaIndex;
    }

    /**
     * Pide DNI, nombre y edad del pasajero, validando cada campo antes de
     * continuar (DNI de 8 digitos, nombre no vacio, edad numerica y
     * dentro de un rango razonable).
     */
    private Pasajero leerPasajero(Scanner sc) {
        Validacion validacion = new Validacion();

        String dni = "";
        boolean dniValido = false;
        while (!dniValido) {
            System.out.print("DNI del pasajero (8 digitos): ");
            dni = sc.nextLine().trim();
            dniValido = validacion.validarDni(dni);
            if (!dniValido) {
                System.out.println("❌ DNI invalido. Debe tener exactamente 8 digitos numericos.");
            }
        }

        String nombre = "";
        while (nombre.isEmpty()) {
            System.out.print("Nombre del pasajero: ");
            nombre = sc.nextLine().trim();
            if (nombre.isEmpty()) {
                System.out.println("❌ El nombre no puede estar vacio.");
            }
        }

        int edad = leerEdad(sc);

        return new Pasajero(dni, nombre, edad);
    }

    /**
     * Pide la edad del pasajero de forma segura, reintentando si el
     * usuario ingresa texto no numerico o un valor fuera de rango.
     */
    private int leerEdad(Scanner sc) {
        int edad = -1;
        while (edad < 0 || edad > 120) {
            System.out.print("Edad del pasajero: ");
            try {
                edad = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                edad = -1;
            }
            if (edad < 0 || edad > 120) {
                System.out.println("❌ Edad invalida. Ingrese un numero entre 0 y 120.");
            }
        }
        return edad;
    }
}