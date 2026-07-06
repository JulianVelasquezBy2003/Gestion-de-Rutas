package gestionderutas;

import java.util.Scanner;

import modelo.Reporte;
import modelo.Usuario;
import modelo.Administrador;
import datos.Persistencia;

public class MenuPrincipal {

    public static void main(String[] args) {
        //Login
        Persistencia persistencia = new Persistencia();
        try {
            persistencia.cargarXML();
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo XML.");
            return;
        }
        Usuario[] usuarios = persistencia.getUsuarios();
        int cantidadUsuarios = persistencia.getCantidadUsuarios();
        Usuario usuarioLogueado = null;
        while (usuarioLogueado == null) {
            usuarioLogueado = login(usuarios, cantidadUsuarios);
        }

        //Mostrar menú según rol
        if (usuarioLogueado.getRol().equalsIgnoreCase("admin")) {
            menuAdministrador(usuarioLogueado);
        } else if (usuarioLogueado.getRol().equalsIgnoreCase("cajero")) {
            menuCajero(usuarioLogueado);
        } else {
            System.out.println("❌ Rol no reconocido.");
        }
    }

    //Metodo para el LOGIN
    public static Usuario login(Usuario[] usuarios, int cantidadUsuarios) {
        Scanner input = new Scanner(System.in);
        System.out.println("━━━━━━━━━━━━━━━━━━━━ Login ━━━━━━━━━━━━━━━━━━━━━━");
        System.out.print("Ingresar Nombre de Usuario: ");
        String nombre = input.nextLine();
        System.out.print("Contraseña: ");
        String contraseña = input.nextLine();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].getNombre().equals(nombre) && usuarios[i].getContrasena().equals(contraseña)) {
                System.out.println("Login exitoso. Bienvenido!\n");
                return usuarios[i];
            }
        }
        System.out.println("❌ Credenciales incorrectas.\n");
        return null;
    }

    //Metodo para el Menu Administrador
    public static void menuAdministrador(Usuario usuarioLogueado) {
        int opc = 0;
        do {
            Scanner input = new Scanner(System.in);
            System.out.println("━━━━━━━━━━━━━━ Menu Administrador ━━━━━━━━━━━━━━━━");
            System.out.println("1. Registrar Ruta");
            System.out.println("2. Agregar Horario");
            System.out.println("3. Agregar Nuevo Cajero");
            System.out.println("4. Salir");
            System.out.print("Ingresar opcion: ");
            opc = input.nextInt();

            switch (opc) {
                case 1 -> {
                    Administrador adm = new Administrador();
                    adm.registrarRuta();
                }
                case 2 -> {

                }
            }

        } while (opc != 4);

    }

    public static void menuCajero(Usuario usuarioLogueado) {
    }

    public static void verReporte(Reporte reporte) {
    }
}
