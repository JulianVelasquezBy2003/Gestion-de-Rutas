package modelo;

import java.util.Scanner;

import datos.Persistencia;
import transporte.Venta;
import transporte.Viaje;

//Clase Reporte: genera y muestra reportes por consola.
public class Reporte {

    //Genera un reporte de ventas a partir de un arreglo de Venta.
    public String verReporteVentas(Venta[] ventas) {
        if (ventas == null || ventas.length == 0) {
            return "No hay ventas registradas.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- Reporte de Ventas ---\n");
        double total = 0.0;
        for (int i = 0; i < ventas.length; i++) {
            Venta v = ventas[i];
            if (v == null) continue;
            sb.append(String.format("%d) ID: %d | Fecha: %s | Precio: S/ %.2f | Pasajero: %s%n",
                    i + 1,
                    v.getIdVenta(),
                    v.getFecha(),
                    v.getPrecioFinal(),
                    v.getPasajero() != null ? v.getPasajero().getNombre() : "N/D"));
            total += v.getPrecioFinal();
        }
        sb.append(String.format("Total ventas: S/ %.2f%n", total));
        return sb.toString();
    }

    //Genera un reporte de ocupacion a partir de un arreglo de Viaje.
    public String verReporteOcupacion(Viaje[] viajes) {
        if (viajes == null || viajes.length == 0) {
            return "No hay viajes cargados.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- Reporte de Ocupacion ---\n");
        for (int i = 0; i < viajes.length; i++) {
            Viaje v = viajes[i];
            if (v == null) continue;
            int ocupados = (v.getBus() != null) ? v.getBus().contarAsientosOcupados() : 0;
            int capacidad = (v.getBus() != null) ? v.getBus().getCapacidad() : 0;
            sb.append(String.format("%d) %s -> %s | %s %s | Ocupacion: %d/%d%n",
                    i + 1,
                    v.getRuta() != null ? v.getRuta().getOrigen() : "N/D",
                    v.getRuta() != null ? v.getRuta().getDestino() : "N/D",
                    v.getFecha(),
                    v.getHora(),
                    ocupados,
                    capacidad));
        }
        return sb.toString();
    }

    // Submenu de reportes para el Administrador.
    public void mostrarMenuReportes(Persistencia persistencia, Scanner input) {
        int opc;
        do {
            String menuReportes = """
                          ┏━━━━━ Menu de Reportes ━━━━━━━━┓      
                          ┃ 1. Reporte de Ventas          ┃      
                          ┃ 2. Reporte de Ocupacion       ┃      
                          ┃ 3. Volver                     ┃      
                          ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛      
                          """;
            System.out.print(menuReportes);
            opc = leerOpcion(input, "Ingresar opcion: ", 1, 3);

            try {
                switch (opc) {
                    case 1 ->
                        System.out.println(this.verReporteVentas(persistencia.getVentasRegistradas()));
                    case 2 ->
                        System.out.println(this.verReporteOcupacion(persistencia.getTodosLosViajes()));
                    case 3 ->
                        System.out.println("Volviendo al menu administrador...");
                }
            } catch (Exception errorAlMostrarReporte) {
                System.out.println("❌ No se pudo generar el reporte: " + errorAlMostrarReporte.getMessage());
            }
            System.out.println();
        } while (opc != 3);
    }

    //Muestra directamente el reporte de ventas (usado desde el menu del Cajero).
    public void mostrarReporteVentas(Persistencia persistencia, Scanner input) {
        try {
            System.out.println(this.verReporteVentas(persistencia.getVentasRegistradas()));
        } catch (Exception errorAlMostrarReporte) {
            System.out.println("❌ No se pudo generar el reporte de ventas: " + errorAlMostrarReporte.getMessage());
        }
    }

    // Lee un numero entero desde el Scanner dentro de un rango [min, max], reintentando en caso de error.
    private static int leerOpcion(Scanner input, String mensaje, int min, int max) {
        int valor = -1;
        boolean valido = false;

        while (!valido) {
            System.out.print(mensaje);
            try {
                valor = Integer.parseInt(input.nextLine().trim());
                if (valor < min || valor > max) {
                    System.out.println("❌ Ingrese un numero entre " + min + " y " + max + ".");
                } else {
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada invalida. Debe ingresar solo numeros.");
            }
        }
        return valor;
    }
}