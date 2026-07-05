package transporte;

public class Viaje {

    // Atributos
    private int idViaje;
    private String fecha;
    private String hora;
    private Ruta ruta;
    private Bus bus;

    //Constructor
    public Viaje() {
    }

    //Getters & Setters
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
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }

    public Ruta getRuta() {
        return ruta;
    }
    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Bus getBus() {
        return bus;
    }
    public void setBus(Bus bus) {
        this.bus = bus;
    }
    
    //
    public double calcularOcupacion(){
        double ocupacion = 0;
        return ocupacion;
    }
    
    /* @Override
    public String toString(){
        return;
    } */
}
