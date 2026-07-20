package transporte;

import datos.Validacion;

public class Pasajero {

    private String dni;
    private String nombre;
    private int edad;

    // Descuento aplicado a menores de 18 y mayores o iguales a 60
    private static final double DESCUENTO = 0.15;

    public Pasajero(String dni, String nombre, int edad) {
        Validacion.validarDniOExcepcion(dni);
        Validacion.validarTextoNoVacioOExcepcion(nombre, "Nombre del pasajero");
        if (edad < 0 || edad > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 0 y 120 años.");
        }
        this.dni = dni.trim();
        this.nombre = nombre.trim();
        this.edad = edad;
    }

    // Getters & Setters
    public String getDni() { return dni; }
    public void setDni(String dni) {
        Validacion.validarDniOExcepcion(dni);
        this.dni = dni.trim();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        Validacion.validarTextoNoVacioOExcepcion(nombre, "Nombre del pasajero");
        this.nombre = nombre.trim();
    }

    public int getEdad() { return edad; }
    public void setEdad(int edad) {
        if (edad < 0 || edad > 120) throw new IllegalArgumentException("La edad debe estar entre 0 y 120 años.");
        this.edad = edad;
    }

    // Calcula el precio final aplicando descuentos según la edad del pasajero (usa this.edad).
    public double calcularPrecioFinal(double precioBase, int edad) {
        if (precioBase < 0) throw new IllegalArgumentException("Precio base no puede ser negativo.");
        if (edad < 18 || edad >= 60) {
            return precioBase * (1.0 - DESCUENTO);
        }
        return precioBase;
    }

    @Override
    public String toString() {
        return dni + " - " + nombre + " (" + edad + " años)";
    }
}