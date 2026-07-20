package transporte;

/*
Representa un viaje específico con una ruta y bus asignados.
 @author Julian
 */
public class Viaje {

    private int idViaje;
    private String fecha;
    private String hora;
    private Ruta ruta;
    private Bus bus;

    public Viaje() {
    }

    public Viaje(String fecha, String hora, Ruta ruta, Bus bus) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha no puede estar vacía.");
        }
        if (hora == null || hora.trim().isEmpty()) {
            throw new IllegalArgumentException("Hora no puede estar vacía.");
        }
        if (ruta == null) {
            throw new IllegalArgumentException("Ruta no puede ser nula.");
        }
        if (bus == null) {
            throw new IllegalArgumentException("Bus no puede ser nulo.");
        }
        this.fecha = fecha.trim();
        this.hora = hora.trim();
        this.ruta = ruta;
        this.bus = bus;
    }

    public int getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
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

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        if (hora == null || hora.trim().isEmpty()) {
            throw new IllegalArgumentException("Hora no puede estar vacía.");
        }
        this.hora = hora.trim();
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("Ruta no puede ser nula.");
        }
        this.ruta = ruta;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        if (bus == null) {
            throw new IllegalArgumentException("Bus no puede ser nulo.");
        }
        this.bus = bus;
    }

    //Calcula el porcentaje de ocupación del bus en este viaje.
    public double calcularOcupacion() {
        if (bus == null) {
            throw new IllegalStateException("Bus no asignado al viaje.");
        }

        int capacidadTotal = bus.getCapacidad();
        int asientosOcupados = bus.contarAsientosOcupados();

        if (capacidadTotal == 0) {
            return 0;
        }

        return (asientosOcupados * 100.0) / capacidadTotal;
    }
    
    // Verifica si hay asientos disponibles en el bus para este viaje.
    public boolean tieneAsientosDisponibles() {
        if (bus == null) {
            return false;
        }
        return bus.contarAsientosOcupados() < bus.getCapacidad();
    }

    @Override
    public String toString() {
        return String.format("Viaje #%d | %s %s | %s | Bus: %s",
                idViaje,
                fecha != null ? fecha : "N/D",
                hora != null ? hora : "N/D",
                ruta != null ? ruta.toString() : "N/D",
                bus != null ? bus.getPlaca() : "N/D");
    }
}
