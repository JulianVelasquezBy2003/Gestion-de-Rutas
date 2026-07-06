package modelo;
import transporte.Viaje;
import transporte.Pasajero;

public class Cajero extends Usuario {

    public Cajero() {
    }

    public Cajero(int id, String nombre, String contraseña) {
        super(id, nombre, contraseña);
    }

    public Cajero(int id, String nombre, String contraseña, String rol) {
        super(id, nombre, contraseña, rol);
    }
    

    // Metodos
    //Debe mostrar frecuencias y precios diponibles
    /*
    public Viaje[] mostrarHorario(String destino) {
    }
    
    public void mostrarAsientos(Viaje viaje) {
    }

    public Pasajero registarPasajero() {

    } */

}
