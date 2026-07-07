package transporte;

/*
 * Representa el boleto fisico/impreso asociado a una venta: numero de
 * asiento y precio pagado.
 *
 * @author Julian,Angela
 */
public class Boleto {

    // Contador simple en memoria para asignar IDs mientras no exista
    // todavia la persistencia real de boletos.
    private static int siguienteId = 1;

    //Atributos
    private int idBoleto;
    private String fechaVenta;
    private int asientoFila;
    private int asientoColumna;
    private double precioFinal;

    //Constructor
    public Boleto() {
    }

    public Boleto(int idBoleto, String fechaVenta, int asientoFila, int asientoColumna, double precioFinal) {
        this.idBoleto = idBoleto;
        this.fechaVenta = fechaVenta;
        this.asientoFila = asientoFila;
        this.asientoColumna = asientoColumna;
        this.precioFinal = precioFinal;
    }

    /**
     * Genera el proximo identificador disponible para un nuevo boleto.
     * Provisional, igual que en Venta, hasta conectar con Persistencia.
     */
    public static int generarSiguienteId() {
        return siguienteId++;
    }

    //Getters & Setters
    public int getIdBoleto() {
        return idBoleto;
    }
    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }
    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public int getAsientoFila() {
        return asientoFila;
    }
    public void setAsientoFila(int asientoFila) {
        this.asientoFila = asientoFila;
    }

    public int getAsientoColumna() {
        return asientoColumna;
    }
    public void setAsientoColumna(int asientoColumna) {
        this.asientoColumna = asientoColumna;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }
    public void setPrecioFinal(double precioFinal) {
        this.precioFinal = precioFinal;
    }

    /**
     * Convierte el indice de fila (0-based) a su letra correspondiente
     * (0 -> A, 1 -> B, ...), para mostrar el asiento en formato legible.
     */
    private String filaComoLetra() {
        return String.valueOf((char) ('A' + asientoFila));
    }

    //Metodos
    /**
     * Imprime en consola el comprobante/ticket del boleto.
     */
    public void emitirComprobante() {
        System.out.println("---------- BOLETO ----------");
        System.out.println("N° Boleto : " + idBoleto);
        System.out.println("Fecha     : " + fechaVenta);
        System.out.println("Asiento   : " + filaComoLetra() + (asientoColumna + 1));
        System.out.println("Precio    : S/ " + String.format("%.2f", precioFinal));
        System.out.println("-----------------------------");
    }
}