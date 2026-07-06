package transporte;

public class Ruta {

    // Atributos
    private int idRuta;
    private String origen;
    private String destino;
    private double duracionEstimada;
    private double precioBase;

    // Constructor

    public Ruta() {
    }

    public Ruta(String origen, String destino, double duracionEstimada, double precioBase) {
        this.origen = origen;
        this.destino = destino;
        this.duracionEstimada = duracionEstimada;
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

    public double getDuracionEstimada() {
        return duracionEstimada;
    }
    public void setDuracionEstimada(double duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public double getPrecioBase() {
        return precioBase;
    }
    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    // Metodos
    
    @Override
    public String toString() {
        return idRuta + " | " + origen + " -> " + destino + " | " + duracionEstimada + "h | S/ " + precioBase;
    }

}
