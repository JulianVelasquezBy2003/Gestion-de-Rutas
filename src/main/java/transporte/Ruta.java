package transporte;

//Representa una ruta entre un origen y un destino con duración y precio base.
public class Ruta {

    private int idRuta;
    private String origen;
    private String destino;
    private double duracionEstimada;
    private double precioBase;

    public Ruta() { }

    //idRuta se asigna normalmente desde Persistencia.
    public Ruta(String origen, String destino, double duracionEstimada, double precioBase) {
        if (origen == null || origen.trim().isEmpty()) 
            throw new IllegalArgumentException("Origen no puede estar vacío.");
        if (destino == null || destino.trim().isEmpty()) 
            throw new IllegalArgumentException("Destino no puede estar vacío.");
        if (duracionEstimada < 0) 
            throw new IllegalArgumentException("Duración no puede ser negativa.");
        if (precioBase < 0) 
            throw new IllegalArgumentException("Precio base no puede ser negativo.");
        this.origen = origen.trim();
        this.destino = destino.trim();
        this.duracionEstimada = duracionEstimada;
        this.precioBase = precioBase;
    }

    // Getters y setters (con validaciones en setters)
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
        if (origen == null || origen.trim().isEmpty()) 
            throw new IllegalArgumentException("Origen no puede estar vacío.");
        this.origen = origen.trim();
    }

    public String getDestino() { 
        return destino; 
    }
    public void setDestino(String destino) {
        if (destino == null || destino.trim().isEmpty()) 
            throw new IllegalArgumentException("Destino no puede estar vacío.");
        this.destino = destino.trim();
    }

    public double getDuracionEstimada() { 
        return duracionEstimada; 
    }
    public void setDuracionEstimada(double duracionEstimada) {
        if (duracionEstimada < 0) 
            throw new IllegalArgumentException("Duración no puede ser negativa.");
        this.duracionEstimada = duracionEstimada;
    }

    public double getPrecioBase() { 
        return precioBase; 
    }
    public void setPrecioBase(double precioBase) {
        if (precioBase < 0) 
            throw new IllegalArgumentException("Precio base no puede ser negativo.");
        this.precioBase = precioBase;
    }

    @Override
    public String toString() {
        return String.format("%d | %s -> %s | %.1fh | S/ %.2f",idRuta, origen != null ? origen : "N/D", destino != null ? destino : "N/D",duracionEstimada, precioBase);
    }
}