package modelo;

import datos.Persistencia;
import java.util.Scanner;
import transporte.Ruta;
import transporte.Viaje;

public class Administrador extends Usuario {

    //Constructores
    public Administrador() {
    }

    public Administrador(int id, String nombre, String contraseña, String rol) {
        super(id, nombre, contraseña, rol);
    }

    // Metodos
    public void registrarRuta() {
        Scanner input = new Scanner(System.in);
        System.out.print("Ingresar cantidad de rutas a agregar: ");
        int cant = input.nextInt();
        input.nextLine();
        if (cant >= 1) {
            System.out.print("Desde donde es su Origen: ");
            String origen = input.nextLine();
            System.out.print("Cual es su Destino: ");
            String destino = input.nextLine();
            System.out.print("Cual seria su duracion estimada: ");
            double duracion = input.nextDouble();
            System.out.print("Su Precio Base: ");
            double precio = input.nextDouble();
            Ruta nuevaRuta = new Ruta(origen, destino, duracion, precio);
            Persistencia persistencia = new Persistencia();
            try {
                persistencia.guardarRuta(nuevaRuta);
            } catch (Exception ex) {
                System.getLogger(Administrador.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            System.out.println("Ruta registrada correctamente.");
        } else {
            System.out.println("Ingresar una cantidad valida");
        }
    }

    public void agregarHorario(Viaje[][] viajes) {
    }

    /*public Cajero crearCajero(String nombre, String contrasena) {
    Cajero cajero = new Cajero(nombre, contrasena);

    usuarios[cantidadUsuarios] = cajero;
    cantidadUsuarios++;

    return cajero;
} */
}
