package modelo;

import transporte.Venta;
import transporte.Viaje;

/*
Genera reportes de ventas y ocupación para Administrador y Cajero
@author Julian, Angela
 */
public final class Reporte {

    private Reporte() {
        throw new AssertionError("No instanciar");
    }
    
    // Genera un reporte de ventas a partir del arreglo de ventas
    public static String generarReporteVentas(Venta[] ventas) {
        if (ventas == null || ventas.length == 0) {
            return "No hay ventas registradas.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("━━ Reporte de Ventas ━━\n");
        double total = 0.0;
        for (int i = 0; i < ventas.length; i++) {
            Venta v = ventas[i];
            if (v == null) continue;
            sb.append(String.format("%d) ID: %d | Fecha: %s | Precio: S/ %.2f | Pasajero: %s%n",
                    i + 1, v.getIdVenta(), v.getFecha(), v.getPrecioFinal(), v.getPasajero() != null ? v.getPasajero().getNombre() : "N/D"));
            total += v.getPrecioFinal();
        }
        sb.append(String.format("Total ventas: S/ %.2f%n", total));
        return sb.toString();
    }

    // Genera un reporte de ocupación a partir de un arreglo de viajes
    public static String generarReporteOcupacion(Viaje[] viajes) {
        if (viajes == null || viajes.length == 0) {
            return "No hay viajes cargados.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("━━ Reporte de Ocupación ━━\n");
        for (int i = 0; i < viajes.length; i++) {
            Viaje v = viajes[i];
            if (v == null) continue;
            int ocupados = (v.getBus() != null) ? v.getBus().contarAsientosOcupados() : 0;
            int capacidad = (v.getBus() != null) ? v.getBus().getCapacidad() : 0;
            sb.append(String.format("%d) %s -> %s | %s %s | Ocupación: %d/%d%n",
                    i + 1,
                    v.getRuta() != null ? v.getRuta().getOrigen() : "N/D", v.getRuta() != null ? v.getRuta().getDestino() : "N/D",
                    v.getFecha(), v.getHora(), ocupados, capacidad));
        }
        return sb.toString();
    }
}