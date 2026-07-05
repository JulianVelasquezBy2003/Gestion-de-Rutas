package transporte;

public class Venta {
    //Atributos
    private int idVenta;
    private String fecha;
    private double precioFinal;
    private Pasajero pasajero;
    private Viaje viaje;
    //Constructor
    public Venta() {
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

    //Boleta
    public String generarComprobante(){
        String comprobante = "";
        return comprobante;
    }
    /* @Override
    public String toString(){
        return;
    } */
}
