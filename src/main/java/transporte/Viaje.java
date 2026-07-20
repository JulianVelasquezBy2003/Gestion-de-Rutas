package transporte;

import datos.Validacion;

/*
Representa un viaje programado con ruta, bus, fecha y hora.
@author Julian
 */
public class Viaje {

    private int idViaje;
    private String fecha;
    private String hora;
    private Ruta ruta;
    private Bus bus;

    public Viaje() { }

    public Viaje(String fecha, String hora, Ruta ruta, Bus bus) {
        setFecha(fecha);
        setHora(hora);
        setRuta(ruta);
        setBus(bus);
    }

    public int getIdViaje() { return idViaje; }
    public void setIdViaje(int idViaje) { this.idViaje = idViaje; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) {
        Validacion.validarTextoNoVacio(fecha, "Fecha");
        this.fecha = fecha.trim();
    }

    public String getHora() { return hora; }
    public void setHora(String hora) {
        Validacion.validarHora(hora);
        this.hora = hora.trim();
    }

    public Ruta getRuta() { return ruta; }
    public void setRuta(Ruta ruta) {
        if (ruta == null) throw new IllegalArgumentException("Ruta no puede ser nula.");
        this.ruta = ruta;
    }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) {
        if (bus == null) throw new IllegalArgumentException("Bus no puede ser nulo.");
        this.bus = bus;
    }

    public double calcularOcupacion() {
        if (bus == null) return 0;
        int capacidad = bus.getCapacidad();
        if (capacidad == 0) return 0;
        return (bus.contarAsientosOcupados() * 100.0) / capacidad;
    }

    public boolean tieneAsientosDisponibles() {
        return bus != null && bus.contarAsientosOcupados() < bus.getCapacidad();
    }

    @Override
    public String toString() {
        return String.format("Viaje #%d | %s %s | %s | Bus: %s",
                idViaje, fecha, hora, ruta != null ? ruta.toString() : "N/D", bus != null ? bus.getPlaca() : "N/D");
    }
}