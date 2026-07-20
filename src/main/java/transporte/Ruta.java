package transporte;

import datos.Validacion;

/*
Representa una ruta con origen, destino, duración y precio base.
@author Julian
 */
public class Ruta {

    private int idRuta;
    private String origen;
    private String destino;
    private double duracionEstimada;
    private double precioBase;

    public Ruta() { }

    public Ruta(String origen, String destino, double duracionEstimada, double precioBase) {
        setOrigen(origen);
        setDestino(destino);
        setDuracionEstimada(duracionEstimada);
        setPrecioBase(precioBase);
    }

    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) {
        Validacion.validarTextoNoVacio(origen, "Origen");
        this.origen = origen.trim();
    }

    public String getDestino() { return destino; }
    public void setDestino(String destino) {
        Validacion.validarTextoNoVacio(destino, "Destino");
        this.destino = destino.trim();
    }

    public double getDuracionEstimada() { return duracionEstimada; }
    public void setDuracionEstimada(double duracionEstimada) {
        if (duracionEstimada < 0) throw new IllegalArgumentException("Duración no puede ser negativa.");
        this.duracionEstimada = duracionEstimada;
    }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) {
        if (precioBase < 0) throw new IllegalArgumentException("Precio base no puede ser negativo.");
        this.precioBase = precioBase;
    }

    @Override
    public String toString() {
        return String.format("%d | %s -> %s | %.1fh | S/ %.2f", idRuta, origen, destino, duracionEstimada, precioBase);
    }
}