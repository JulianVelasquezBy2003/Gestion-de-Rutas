package transporte;

import datos.Validacion;

/*
Representa una venta de pasaje para un viaje.
@author Julian
 */
public class Venta {

    private static int siguienteId = 1;

    private int idVenta;
    private String fecha;
    private double precioFinal;
    private Pasajero pasajero;
    private Viaje viaje;

    public Venta() { 
    }

    public Venta(String fecha, double precioFinal, Pasajero pasajero, Viaje viaje) {
        setFecha(fecha);
        setPrecioFinal(precioFinal);
        setPasajero(pasajero);
        setViaje(viaje);
    }

    public static int generarSiguienteId() {
        return siguienteId++;
    }

    public static void resetearContador(int valor) {
        siguienteId = valor;
    }

    // Getters y Setters
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) {
        if (idVenta <= 0) throw new IllegalArgumentException("ID de venta debe ser positivo.");
        this.idVenta = idVenta;
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) {
        Validacion.validarTextoNoVacio(fecha, "Fecha");
        this.fecha = fecha.trim();
    }

    public double getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(double precioFinal) {
        if (precioFinal < 0) throw new IllegalArgumentException("Precio no puede ser negativo.");
        this.precioFinal = precioFinal;
    }

    public Pasajero getPasajero() { return pasajero; }
    public void setPasajero(Pasajero pasajero) {
        if (pasajero == null) throw new IllegalArgumentException("Pasajero no puede ser nulo.");
        this.pasajero = pasajero;
    }

    public Viaje getViaje() { return viaje; }
    public void setViaje(Viaje viaje) {
        if (viaje == null) throw new IllegalArgumentException("Viaje no puede ser nulo.");
        this.viaje = viaje;
    }

    public String generarComprobante() {
        if (pasajero == null || viaje == null || viaje.getRuta() == null) {
            throw new IllegalStateException("Datos incompletos para generar comprobante.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("  COMPROBANTE DE VENTA\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("N° Venta : ").append(idVenta).append("\n");
        sb.append("Fecha    : ").append(fecha).append("\n");
        sb.append("Pasajero : ").append(pasajero.getNombre()).append(" (DNI: ").append(pasajero.getDni()).append(")\n");
        sb.append("Ruta     : ").append(viaje.getRuta().getOrigen()).append(" -> ").append(viaje.getRuta().getDestino()).append("\n");
        sb.append("Hora     : ").append(viaje.getHora()).append("\n");
        sb.append("Total    : S/ ").append(String.format("%.2f", precioFinal)).append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Venta #%d - S/ %.2f", idVenta, precioFinal);
    }
}