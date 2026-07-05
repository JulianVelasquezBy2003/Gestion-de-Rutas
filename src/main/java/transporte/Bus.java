package transporte;

public class Bus {

    //Atributos
    private int idBus;
    private String placa;
    private int capacidad;
    private char[][] asientos;

    //Constructor
    public Bus() {
    }

    //Metodos
    public int getIdBus() {
        return idBus;
    }
    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }

    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getCapacidad() {
        return capacidad;
    }
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public char[][] getAsientos() {
        return asientos;
    }
    public void setAsientos(char[][] asientos) {
        this.asientos = asientos;
    }

    //Metodos para el mapa de Asientos
    public void mostrarAsientos() {
    }
    public void ocuparAsiento(int fila, int columna) {
    }

}
