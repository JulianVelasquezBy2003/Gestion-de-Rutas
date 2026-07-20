package modelo;

import java.time.LocalDate;
import java.util.Scanner;

import datos.Persistencia;
import datos.Validacion;
import transporte.Bus;
import transporte.Boleto;
import transporte.Pasajero;
import transporte.Venta;
import transporte.Viaje;

/*
Usuario con rol Cajero/Counter. 
Se encarga de la venta y reserva de pasajes: mostrar el mapa de asientos, 
registrar al pasajero, ocupar el asiento elegido y generar el comprobante de venta.
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

    /* 
    Venta / Reserva de pasaje 
     1. Muestra el mapa de asientos, pide la posicion deseada,
     2. Registra al pasajero, calcula el precio final (con descuentos si corresponde) y genera el comprobante.
     */
    public Venta venderPasaje(Viaje viaje, Scanner entrada) {
        if (viaje == null || viaje.getBus() == null || viaje.getRuta() == null) {
            System.out.println("❌ El viaje seleccionado no tiene bus o ruta asignados.");
            return null;
        }
        Bus bus = viaje.getBus();
        bus.mostrarAsientos();

        System.out.print("Ingrese la fila del asiento (Letra, ej. A): ");
        String textoFila = entrada.nextLine();
        System.out.print("Ingrese el numero de asiento (1 a " + bus.getAsientos()[0].length + "): ");
        String textoColumna = entrada.nextLine();
        System.out.print("DNI del pasajero (8 digitos): ");
        String dni = entrada.nextLine();
        System.out.print("Nombre del pasajero: ");
        String nombre = entrada.nextLine();
        System.out.print("Edad del pasajero: ");
        String textoEdad = entrada.nextLine();
        try {
            // Validar y parsear usando métodos estáticos de Validacion
            int fila = Validacion.validarYParsearFila(textoFila, bus.getAsientos().length);
            int columna = Validacion.validarYParsearColumna(textoColumna, bus.getAsientos()[0].length);
            Validacion.validarDniOExcepcion(dni);
            Validacion.validarTextoNoVacioOExcepcion(nombre, "Nombre del pasajero");
            int edad = Validacion.validarYParsearEdad(textoEdad);
            // Validar estado del asiento respecto al bus
            Validacion.validarAsientoConBus(bus, fila, columna);
            // Ocupar el asiento
            if (!bus.ocuparAsiento(fila, columna)) {
                throw new IllegalStateException("No se pudo completar la venta: el asiento ya no esta disponible.");
            }

            Pasajero pasajero = new Pasajero(dni.trim(), nombre.trim(), edad);
            // Calcular precio con descuentos si aplica
            double precioBase = viaje.getRuta().getPrecioBase();
            double precioFinal = pasajero.calcularPrecioFinal(precioBase, edad);
            // Registrar la venta
            String fechaHoy = LocalDate.now().toString();
            Venta venta = new Venta();
            venta.setIdVenta(Venta.generarSiguienteId());
            venta.setFecha(fechaHoy);
            venta.setPrecioFinal(precioFinal);
            venta.setPasajero(pasajero);
            venta.setViaje(viaje);

            // Generar boleto y comprobante
            Boleto boleto = new Boleto(Boleto.generarSiguienteId(), fechaHoy, fila, columna, precioFinal);
            System.out.println();
            boleto.emitirComprobante();
            System.out.println(venta.generarComprobante());
            System.out.println("\n✅ Venta registrada correctamente.\n");
            return venta;
        } catch (IllegalArgumentException | IllegalStateException datosInvalidos) {
            System.out.println("❌ " + datosInvalidos.getMessage());
            return null;
        } catch (Exception errorInesperado) {
            System.out.println("❌ No se pudo completar la venta: " + errorInesperado.getMessage());
            return null;
        }
    }

    // Pide un destino y muestra (sin vender) los viajes programados hacia ese destino, recorriendo la matriz/listado en Persistencia.
    public void mostrarViajesDisponibles(Persistencia persistencia, Scanner entrada) {
        System.out.print("Ingrese el destino a buscar: ");
        String destino = entrada.nextLine().trim();
        try {
            Validacion.validarTextoNoVacioOExcepcion(destino, "Destino");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
            return;
        }
        Viaje[] disponibles = persistencia.buscarViajesPorDestino(destino);
        if (disponibles == null || disponibles.length == 0) {
            System.out.println("⚠️ No se encontraron viajes programados hacia \"" + destino + "\".\n");
            return;
        }
        System.out.println("\n--- Viajes disponibles hacia " + destino + " ---");
        for (int i = 0; i < disponibles.length; i++) {
            imprimirResumenViaje(i + 1, disponibles[i]);
        }
        System.out.println();
    }

    // Pide un destino, muestra los viajes disponibles y permite elegir uno para iniciar el flujo completo de venta/reserva de pasaje.
    public void venderPasajePorDestino(Persistencia persistencia, Scanner entrada) {
        System.out.print("Ingrese el destino: ");
        String destino = entrada.nextLine().trim();
        Viaje[] disponibles = persistencia.buscarViajesPorDestino(destino);

        if (disponibles == null || disponibles.length == 0) {
            System.out.println("⚠️ No se encontraron viajes programados hacia \"" + destino + "\".\n");
            return;
        }
        System.out.println("\n--- Viajes disponibles hacia " + destino + " ---");
        for (int i = 0; i < disponibles.length; i++) {
            imprimirResumenViaje(i + 1, disponibles[i]);
        }

        int opcion = leerOpcion(entrada, "Elija el viaje (0 para cancelar): ", 0, disponibles.length);
        if (opcion == 0) {
            System.out.println("Operacion cancelada.\n");
            return;
        }

        Viaje viajeElegido = disponibles[opcion - 1];
        Venta venta = this.venderPasaje(viajeElegido, entrada);

        if (venta != null) {
            boolean registrada = persistencia.registrarVenta(venta);
            if (!registrada) {
                System.out.println("⚠️ La venta se realizo pero no se pudo guardar en el historial (limite alcanzado).");
            }
        }
    }

    // Imprime una linea de resumen de un viaje (ruta, fecha, hora, bus, precio y ocupacion actual),
    private void imprimirResumenViaje(int numero, Viaje viaje) {
        String origen = (viaje.getRuta() != null) ? viaje.getRuta().getOrigen() : "N/D";
        String destinoRuta = (viaje.getRuta() != null) ? viaje.getRuta().getDestino() : "N/D";
        double precio = (viaje.getRuta() != null) ? viaje.getRuta().getPrecioBase() : 0.0;
        String placa = (viaje.getBus() != null) ? viaje.getBus().getPlaca() : "N/D";
        int ocupados = (viaje.getBus() != null) ? viaje.getBus().contarAsientosOcupados() : 0;
        int capacidad = (viaje.getBus() != null) ? viaje.getBus().getCapacidad() : 0;

        System.out.printf("%d. %s -> %s | %s %s | Bus %s | S/ %.2f | Ocupacion: %d/%d%n",
                numero, origen, destinoRuta, viaje.getFecha(), viaje.getHora(), placa, precio, ocupados, capacidad);
    }

    //Lee un numero entero desde el Scanner dentro de un rango [min, max], reintentando en caso de error.
    private static int leerOpcion(Scanner entrada, String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String input = entrada.nextLine().trim();
            try {
                return Validacion.validarYParsearEntero(input, "Opción", min, max);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

}
