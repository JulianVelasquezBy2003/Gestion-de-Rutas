package transporte;

/*
Representa una venta de pasaje para un viaje específico.
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
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha no puede estar vacía.");
        }
        if (precioFinal < 0) {
            throw new IllegalArgumentException("Precio final no puede ser negativo.");
        }
        if (pasajero == null) {
            throw new IllegalArgumentException("Pasajero no puede ser nulo.");
        }
        if (viaje == null) {
            throw new IllegalArgumentException("Viaje no puede ser nulo.");
        }
        this.fecha = fecha.trim();
        this.precioFinal = precioFinal;
        this.pasajero = pasajero;
        this.viaje = viaje;
    }

    public static int generarSiguienteId() {
        return siguienteId++;
    }

    public static void resetearContador(int valor) {
        siguienteId = valor;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha no puede estar vacía.");
        }
        this.fecha = fecha.trim();
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(double precioFinal) {
        if (precioFinal < 0) {
            throw new IllegalArgumentException("Precio final no puede ser negativo.");
        }
        this.precioFinal = precioFinal;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        if (pasajero == null) {
            throw new IllegalArgumentException("Pasajero no puede ser nulo.");
        }
        this.pasajero = pasajero;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        if (viaje == null) {
            throw new IllegalArgumentException("Viaje no puede ser nulo.");
        }
        this.viaje = viaje;
    }

    public String generarComprobante() {
        if (pasajero == null || viaje == null) {
            throw new IllegalStateException("No se puede generar comprobante: faltan datos de pasajero o viaje.");
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
        return "Venta #" + idVenta + " - S/ " + String.format("%.2f", precioFinal);
    }
}
