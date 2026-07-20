package transporte;

import datos.Validacion;

/*
Representa un pasajero con sus datos personales.
@author Julian
 */
public class Pasajero {

    private static final double DESCUENTO = 0.15;

    private String dni;
    private String nombre;
    private int edad;

    public Pasajero(String dni, String nombre, int edad) {
        setDni(dni);
        setNombre(nombre);
        setEdad(edad);
    }

    public String getDni() { return dni; }
    public void setDni(String dni) {
        Validacion.validarDni(dni);
        this.dni = dni.trim();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        Validacion.validarTextoNoVacio(nombre, "Nombre");
        this.nombre = nombre.trim();
    }

    public int getEdad() { return edad; }
    public void setEdad(int edad) {
        if (edad < 0 || edad > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 0 y 120 años.");
        }
        this.edad = edad;
    }

    // Calcula el precio final aplicando descuento a menores de 18 y mayores de 60
    public double calcularPrecioFinal(double precioBase) {
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