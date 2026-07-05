package modelo;

import transporte.Ruta;
import transporte.Viaje;

public class Administrador extends Usuario {

    // Atributos
    public Administrador(int id, String nombre, String contraseña, String rol) {
        super(id, nombre, contraseña, rol);
    }

    // Metodos
    public void registrarRuta(Ruta ruta) {
    }

    public void agregarHorario(Viaje[][] viajes) {
    }

    public Cajero crearCajero(String nombre, String contrasena) {
        return new Cajero(nombre, contrasena);
    }

    /*public Cajero crearCajero(String nombre, String contrasena) {
    Cajero cajero = new Cajero(nombre, contrasena);

    usuarios[cantidadUsuarios] = cajero;
    cantidadUsuarios++;

    return cajero;
} */

}
