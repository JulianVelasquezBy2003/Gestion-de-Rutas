package transporte;

/*
Representa el boleto físico/impreso asociado a una venta.
@author Julian
 */
public class Boleto {

    private static int siguienteId = 1;

    private int idBoleto;
    private String fechaVenta;
    private int asientoFila;
    private int asientoColumna;
    private double precioFinal;

    public Boleto() { 
    }
    public Boleto(int idBoleto, String fechaVenta, int asientoFila, int asientoColumna, double precioFinal) {
        setIdBoleto(idBoleto);
        setFechaVenta(fechaVenta);
        setAsientoFila(asientoFila);
        setAsientoColumna(asientoColumna);
        setPrecioFinal(precioFinal);
    }

    public static int generarSiguienteId() {
        return siguienteId++;
    }

    // Getters y Setters
    public int getIdBoleto() { return idBoleto; }
    public void setIdBoleto(int idBoleto) {
        if (idBoleto <= 0) throw new IllegalArgumentException("ID de boleto debe ser positivo.");
        this.idBoleto = idBoleto;
    }

    public String getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(String fechaVenta) {
        if (fechaVenta == null || fechaVenta.trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha de venta no puede estar vacía.");
        }
        this.fechaVenta = fechaVenta.trim();
    }

    public int getAsientoFila() { return asientoFila; }
    public void setAsientoFila(int asientoFila) {
        if (asientoFila < 0) throw new IllegalArgumentException("Fila no puede ser negativa.");
        this.asientoFila = asientoFila;
    }

    public int getAsientoColumna() { return asientoColumna; }
    public void setAsientoColumna(int asientoColumna) {
        if (asientoColumna < 0) throw new IllegalArgumentException("Columna no puede ser negativa.");
        this.asientoColumna = asientoColumna;
    }

    public double getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(double precioFinal) {
        if (precioFinal < 0) throw new IllegalArgumentException("Precio no puede ser negativo.");
        this.precioFinal = precioFinal;
    }

    private String filaComoLetra() {
        return String.valueOf((char) ('A' + asientoFila));
    }

    public void emitirComprobante() {
        System.out.println("━━━━━━━━━ BOLETO ━━━━━━━━━");
        System.out.println(" N° Boleto : " + idBoleto);
        System.out.println(" Fecha     : " + fechaVenta);
        System.out.println(" Asiento   : " + filaComoLetra() + (asientoColumna + 1));
        System.out.println(" Precio    : S/ " + String.format("%.2f", precioFinal));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}