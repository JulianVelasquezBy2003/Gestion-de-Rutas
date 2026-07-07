package transporte;

/*
 * @author Julian
 */
public class Venta {

    // Contador de memoria para ID
    
    private static int siguienteId = 1;

    //Atributos
    
    private int idVenta;
    private String fecha;
    private double precioFinal;
    private Pasajero pasajero;
    private Viaje viaje;

    //Constructor
    
    public Venta() {
    }

    /**
     * Genera el proximo identificador disponible para una nueva venta.
     */
    public static int generarSiguienteId() {
        return siguienteId++;
    }

    //Getters & Setters
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
        this.fecha = fecha;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }
    public void setPrecioFinal(double precioFinal) {
        this.precioFinal = precioFinal;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }
    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public Viaje getViaje() {
        return viaje;
    }
    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    // Metodos
    
    public String generarComprobante() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================\n");
        sb.append("      COMPROBANTE DE VENTA\n");
        sb.append("==================================\n");
        sb.append("N° Venta : ").append(idVenta).append("\n");
        sb.append("Fecha    : ").append(fecha != null ? fecha : "N/D").append("\n");

        if (pasajero != null) {
            sb.append("Pasajero : ").append(pasajero.getNombre())
              .append(" (DNI: ").append(pasajero.getDni()).append(")\n");
        } else {
            sb.append("Pasajero : N/D\n");
        }

        if (viaje != null && viaje.getRuta() != null) {
            sb.append("Ruta     : ").append(viaje.getRuta().getOrigen())
              .append(" -> ").append(viaje.getRuta().getDestino()).append("\n");
            sb.append("Hora     : ").append(viaje.getHora() != null ? viaje.getHora() : "N/D").append("\n");
        } else {
            sb.append("Ruta     : N/D\n");
        }

        sb.append("Total    : S/ ").append(String.format("%.2f", precioFinal)).append("\n");
        sb.append("==================================");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Venta #" + idVenta + " - S/ " + String.format("%.2f", precioFinal);
    }
}