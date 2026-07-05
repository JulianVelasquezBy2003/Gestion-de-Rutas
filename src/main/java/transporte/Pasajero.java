package transporte;

public class Pasajero {

    //Atributos de clase
    private String dni;
    private String nombre;
    private int edad;

    //Constructor
    public Pasajero(String dni, String nombre, int edad) {
        this.dni = dni;
        this.nombre = nombre;
        this.edad = edad;
    }

    // Getters & Setters
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }
    public void setEdad(int edad) {
        this.edad = edad;
    }

    // Metodos
    // indica si el pasasjero tiene descuento
    public double calcularPrecioFinal(double precio, int edad) {
        double precioFinal=0.0;
        if (edad < 18 || edad >= 60) {
            precioFinal = precio * 0.15;
        }
        return precioFinal;
    }

    //Metodo Funcion
    @Override
    public String toString() {
        return dni + " - " + nombre + " (" + edad + " años)";
    }
}
