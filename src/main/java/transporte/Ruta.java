package transporte;

public class Ruta {

    // Atributos
    private int idRuta;
    private String origen;
    private String destino;
    private int duracion;
    private double precioBase;

    // Constructor
    public Ruta(int idRuta, String origen, String destino, int duracionEstimada, double precioBase) {
        this.idRuta = idRuta;
        this.origen = origen;
        this.destino = destino;
        this.duracion = duracionEstimada;
        this.precioBase = precioBase;
    }

    // Getters & Setters
    public int getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getDuracionEstimada() {
        return duracion;
    }

    public void setDuracionEstimada(int duracionEstimada) {
        this.duracion = duracionEstimada;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }
    
    // Metodos
    //public String obtenerInfo()

    @Override
    public String toString() {
        return idRuta + " | " + origen + " -> " + destino + " | " + duracion + "h | S/ " + precioBase;
    }

}
