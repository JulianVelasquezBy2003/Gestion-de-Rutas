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
Cajero: gestiona la venta y reserva de pasajes
- Muestra asientos disponibles
- Valida datos del pasajero 
- Calcula precio con descuentos
- Genera comprobantes
@author Julian
 */
public class Cajero extends Usuario {
    //Constructores
    public Cajero() {
    }
    public Cajero(int id, String nombre, String contrasena) {
        super(id, nombre, contrasena);
    }
    public Cajero(int id, String nombre, String contrasena, String rol) {
        super(id, nombre, contrasena, rol);
    }

    // ------------------------------ VENTA DE PASAJE ------------------------------
    /*
     Realiza la venta de un pasaje con todas las validaciones
    - Muestra mapa de asientos
    - Solicita fila (letra), columna (número), DNI, nombre y edad
    - Valida asiento libre, DNI (8 dígitos), nombre (≥2 letras), edad (0-120) 
    - Calcula precio final con descuentos
    - Ocupa el asiento y registra la venta
     */
    public Venta venderPasaje(Viaje viaje, Scanner entrada) {
        if (viaje == null || viaje.getBus() == null || viaje.getRuta() == null) {
            System.out.println("❌ Viaje, bus o ruta no válidos.");
            return null;
        }
        Bus bus = viaje.getBus();
        // Verificar que el mapa de asientos exista
        if (bus.getAsientos() == null || bus.getAsientos().length == 0) {
            System.out.println("❌ El bus no tiene un mapa de asientos válido.");
            return null;
        }
        // Verifica que la primera fila exista
        if (bus.getAsientos()[0] == null || bus.getAsientos()[0].length == 0) {
            System.out.println("❌ El mapa de asientos del bus está corrupto.");
            return null;
        }
        bus.mostrarAsientos();
        System.out.print("Fila (letra, ej. A): ");
        String textoFila = entrada.nextLine().trim();
        System.out.print("Número de asiento (1-" + bus.getAsientos()[0].length + "): ");
        String textoColumna = entrada.nextLine().trim();
        System.out.print("DNI (8 dígitos): ");
        String dni = entrada.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = entrada.nextLine().trim();
        System.out.print("Edad: ");
        String textoEdad = entrada.nextLine().trim();

        try {
            int fila = Validacion.parsearFila(textoFila, bus.getAsientos().length);
            int columna = Validacion.parsearColumna(textoColumna, bus.getAsientos()[0].length);
            Validacion.validarDni(dni);
            Validacion.validarTextoNoVacio(nombre, "Nombre");
            if (nombre.length() < 2) {
                throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres.");
            }
            if (nombre.matches(".*\\d.*")) {
                throw new IllegalArgumentException("El nombre no puede contener números.");
            }
            int edad = Validacion.parsearEdad(textoEdad);
            // Valida asiento (exista y esté libre)
            Validacion.validarAsientoEnBus(bus, fila, columna);
            // Ocupar asiento (solo si está libre)
            if (!bus.ocuparAsiento(fila, columna)) {
                throw new IllegalStateException("El asiento ya fue ocupado.");
            }

            Pasajero pasajero = new Pasajero(dni, nombre, edad);
            double precioBase = viaje.getRuta().getPrecioBase();
            double precioFinal = pasajero.calcularPrecioFinal(precioBase);
            String fechaHoy = LocalDate.now().toString();
            Venta venta = new Venta();
            venta.setIdVenta(Venta.generarSiguienteId());
            venta.setFecha(fechaHoy);
            venta.setPrecioFinal(precioFinal);
            venta.setPasajero(pasajero);
            venta.setViaje(viaje);
            // Emitir comprobantes
            Boleto boleto = new Boleto(Boleto.generarSiguienteId(), fechaHoy, fila, columna, precioFinal);
            System.out.println();
            boleto.emitirComprobante();
            System.out.println(venta.generarComprobante());
            System.out.println("\n✔ Venta registrada.\n");
            return venta;
        } catch (Exception e) {
            System.out.println("✖ " + e.getMessage());
            // Intentar liberar asiento si se ocupó y falló después
            try {
                int fila = Validacion.parsearFila(textoFila, bus.getAsientos().length);
                int columna = Validacion.parsearColumna(textoColumna, bus.getAsientos()[0].length);
                bus.liberarAsiento(fila, columna);
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    // --------------------------- MOSTRAR VIAJES DISPONIBLES ---------------------------
    // Muestra los viajes disponibles hacia un destino. Busca en la matriz de viajes y lista los que coincidan.
    public void mostrarViajesDisponibles(Persistencia persistencia, Scanner entrada) {
        System.out.print("Ingrese el destino: ");
        String destino = entrada.nextLine().trim();
        try {
            Validacion.validarTextoNoVacio(destino, "Destino");
            if (destino.length() < 2) {
                throw new IllegalArgumentException("El destino debe tener al menos 2 caracteres.");
            }
            if (destino.matches(".*\\d.*")) {
                throw new IllegalArgumentException("El destino no debe contener números.");
            }
        } catch (Exception destinoInvalido) {
            System.out.println("✖ " + destinoInvalido.getMessage());
            return;
        }

        Viaje[] disponibles = buscarViajesPorDestino(persistencia, destino);
        if (disponibles.length == 0) {
            System.out.println("⚠️ No hay viajes hacia \"" + destino + "\".\n");
            return;
        }

        System.out.println("\n━━━ Viajes hacia " + destino + " ━━━");
        for (int i = 0; i < disponibles.length; i++) {
            imprimirResumenViaje(i + 1, disponibles[i]);
        }
        System.out.println();
    }

    // ------------------------------- PROCESO DE VENTA -------------------------------
    // Busca destino, muestra viajes, elige uno y vende pasaje. Guarda los cambios en XML (asientos y venta)
    public void venderPasajePorDestino(Persistencia persistencia, Scanner entrada) {
        System.out.print("Ingrese el destino: ");
        String destino = entrada.nextLine().trim();
        try {
            Validacion.validarTextoNoVacio(destino, "Destino");
            if (destino.length() < 2) {
                throw new IllegalArgumentException("El destino debe tener al menos 2 caracteres.");
            }
        } catch (Exception destinoInvalido) {
            System.out.println("✖ " + destinoInvalido.getMessage());
            return;
        }
        Viaje[] disponibles = buscarViajesPorDestino(persistencia, destino);
        if (disponibles.length == 0) {
            System.out.println("⚠️ No hay viajes hacia \"" + destino + "\".\n");
            return;
        }
        System.out.println("\n━━━ Viajes hacia " + destino + " ━━━");
        for (int i = 0; i < disponibles.length; i++) {
            imprimirResumenViaje(i + 1, disponibles[i]);
        }
        int opcion = leerOpcion(entrada, "Elija viaje (0 para cancelar): ", 0, disponibles.length);
        if (opcion == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        Viaje elegido = disponibles[opcion - 1];
        if (elegido == null || !elegido.tieneAsientosDisponibles()) {
            System.out.println("✖ El viaje no está disponible o está lleno.");
            return;
        }

        Venta venta = this.venderPasaje(elegido, entrada);
        if (venta != null) {
            // ✅ Registrar venta en memoria
            if (!persistencia.registrarVenta(venta)) {
                System.out.println("⚠️ Venta realizada pero no se guardó en memoria (límite alcanzado).");
            }

            // ✅ Persistir cambios: asientos ocupados y venta en XML
            try {
                persistencia.guardarViajes();   // Guarda el mapa de asientos actualizado
                persistencia.guardarVentas();   // Guarda el historial de ventas
                System.out.println("✔ Cambios guardados en el sistema.");
            } catch (Exception errorGuardadoViajes_Ventas) {
                System.out.println("⚠️ No se pudieron guardar los cambios en XML: " + errorGuardadoViajes_Ventas.getMessage());
            }
        }
    }

    // ---------------------------- MÉTODOS AUXILIARES ----------------------------
    // Busca viajes cuyo destino coincida (ignoreCase). Recorre la matriz de viajes y devuelve un arreglo compacto
    private Viaje[] buscarViajesPorDestino(Persistencia persistencia, String destino) {
        Viaje[][] matriz = persistencia.getViajes();
        int max = Persistencia.getMaxDestinos() * Persistencia.getMaxHorarios();
        Viaje[] resultado = new Viaje[max];
        int cont = 0;
        for (int f = 0; f < Persistencia.getMaxDestinos(); f++) {
            for (int c = 0; c < Persistencia.getMaxHorarios(); c++) {
                Viaje v = matriz[f][c];
                if (v != null && v.getRuta() != null
                        && v.getRuta().getDestino().equalsIgnoreCase(destino)) {
                    resultado[cont++] = v;
                }
            }
        }
        Viaje[] viajes = new Viaje[cont];
        System.arraycopy(resultado, 0, viajes, 0, cont);
        return viajes;
    }

    /*
    Imprime un resumen del viaje con formato: 
    N. Origen -> Destino | Fecha Hora | Bus PLACA | S/ Precio | Ocupación: X/Y
     */
    private void imprimirResumenViaje(int numero, Viaje viaje) {
        if (viaje == null) {
            System.out.println(numero + ". [Viaje no válido]");
            return;
        }
        String origen = (viaje.getRuta() != null) ? viaje.getRuta().getOrigen() : "N/D";
        String destino = (viaje.getRuta() != null) ? viaje.getRuta().getDestino() : "N/D";
        double precio = (viaje.getRuta() != null) ? viaje.getRuta().getPrecioBase() : 0.0;
        String placa = (viaje.getBus() != null) ? viaje.getBus().getPlaca() : "N/D";
        int ocupados = (viaje.getBus() != null) ? viaje.getBus().contarAsientosOcupados() : 0;
        int capacidad = (viaje.getBus() != null) ? viaje.getBus().getCapacidad() : 0;
        String fecha = (viaje.getFecha() != null) ? viaje.getFecha() : "N/D";
        String hora = (viaje.getHora() != null) ? viaje.getHora() : "N/D";
        System.out.printf("%d. %s -> %s | %s %s | Bus %s | S/ %.2f | Ocupación: %d/%d%n",
                numero, origen, destino, fecha, hora, placa, precio, ocupados, capacidad);
    }

    // Lee una opción entera dentro de un rango, con reintentos
    private int leerOpcion(Scanner entrada, String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String input = entrada.nextLine().trim();
            try {
                return Validacion.parsearEntero(input, "Opción", min, max);
            } catch (Exception opcionInvalida) {
                System.out.println("✖ " + opcionInvalida.getMessage());
            }
        }
    }
}
