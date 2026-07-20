package gestionderutas;

import java.util.Scanner;

import modelo.Administrador;
import modelo.Cajero;
import modelo.Reporte;
import modelo.Usuario;
import transporte.Viaje;
import datos.Persistencia;
import datos.Validacion;

/*
Clase principal del sistema TransRoute.
Contiene el método main(), control de login y navegación de menús.
@author Julian, Angela, Yulisa
 */
public class MenuPrincipal {
    //Scanner General 
    private static final Scanner input = new Scanner(System.in);
    //Numero de intentos del LOGIN
    private static final int INTENTOS_LOGIN = 3;

    public static void main(String[] args) {
        Persistencia persistencia = new Persistencia(); 
        try {
            persistencia.cargarTodos();
            System.out.println("✔ Datos cargados correctamente.");
        } catch (Exception errorCargarPersistencia) {
            System.out.println("✖ Error crítico al cargar los datos del sistema.");
            System.out.println("Detalle: " + errorCargarPersistencia.getMessage());
            System.out.println("El programa no puede continuar.");
            return;
        }

        //------------------ LOGIN ------------------
        Usuario usuarioLogueado = null;
        int intentos = 0;
        while (usuarioLogueado == null && intentos < INTENTOS_LOGIN) {
            usuarioLogueado = login(persistencia);
            intentos++;
            if (usuarioLogueado == null && intentos < INTENTOS_LOGIN) {
                System.out.println("Intento " + intentos + " de " + INTENTOS_LOGIN + ".\n");
            }
        }
        if (usuarioLogueado == null) {
            System.out.println("✖ Número máximo de intentos alcanzado. Cerrando el sistema...");
            return;
        }
        String rol = usuarioLogueado.getRol();
        if (rol == null) {
            System.out.println("✖ El usuario no tiene un rol asignado. Contacte al administrador.");
            return;
        }
        switch (rol.toLowerCase()) {
            case "admin" -> menuAdministrador(usuarioLogueado, persistencia);
            case "cajero" -> menuCajero(usuarioLogueado, persistencia);
            default -> System.out.println("✖ Rol no reconocido: " + rol);
        }
        try {
            persistencia.guardarTodos();
            System.out.println(" ✔ Datos guardados correctamente.");
        } catch (Exception errorDeGuardado) {
            System.out.println("✖ No se pudieron guardar los datos: " + errorDeGuardado.getMessage());
        }
        System.out.println("Sesión finalizada. Hasta pronto, " + usuarioLogueado.getNombre() + ".");
    }

    private static Usuario login(Persistencia persistencia) {
        System.out.println("┏━━━━━━━━━━━━━━━━ Login ━━━━━━━━━━━━━━━━━━┓");
        System.out.print("  Ingresar Nombre de Usuario: ");
        String nombre = input.nextLine().trim();
        System.out.print("  Contraseña: ");
        String contrasena = input.nextLine().trim();
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        try {
            Validacion.validarTextoNoVacio(nombre, "Usuario");
            Validacion.validarTextoNoVacio(contrasena, "Contraseña");
        } catch (IllegalArgumentException noIngresoTexto) {
            System.out.println("✖ " + noIngresoTexto.getMessage() + "\n");
            return null;
        }

        Usuario[] usuarios = persistencia.getUsuarios();
        int cantidad = persistencia.getCantidadUsuarios();
        for (int i = 0; i < cantidad; i++) {
            if (usuarios[i].getNombre().equals(nombre) && usuarios[i].verificarContrasena(contrasena)) {
                System.out.println("✔ Login exitoso. ¡Bienvenido, " + usuarios[i].getNombre() + "!\n");
                return usuarios[i];
            }
        }
        System.out.println("✖ Credenciales incorrectas.\n");
        return null;
    }

    // ------------------------ MENU ADMINISTRADOR --------------------------------
    private static void menuAdministrador(Usuario usuarioLogueado, Persistencia persistencia) {
        Administrador adm = new Administrador();
        int opc;
        do {
            String menuAdmin = """
                    ┏━━━━━━ Menu Administrador ━━━━━━┓      
                    ┃ 1. Registrar Ruta              ┃      
                    ┃ 2. Agregar Horario             ┃      
                    ┃ 3. Agregar Nuevo Cajero        ┃      
                    ┃ 4. Cambiar Contraseña          ┃      
                    ┃ 5. Ver Reportes                ┃      
                    ┃ 6. Salir                       ┃      
                    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛      
                    """;
            System.out.print(menuAdmin);
            opc = leerOpcion("Ingresar opción: ", 1, 6);
            try {
                switch (opc) {
                    case 1 -> adm.registrarRuta(persistencia, input);
                    case 2 -> adm.agregarHorario(persistencia, input);
                    case 3 -> adm.agregarCajero(persistencia, input);
                    case 4 -> adm.cambiarContrasenaUsuario(persistencia, input);
                    case 5 -> mostrarReportes(persistencia);
                    case 6 -> System.out.println("Saliendo del menú administrador...");
                }
            } catch (Exception opcionInvalida) {
                System.out.println("✖ Error al ejecutar la opción: " + opcionInvalida.getMessage());
            }
            System.out.println();
        } while (opc != 6);
    }

    // ------------------------ MENU CAJERO --------------------------------
    private static void menuCajero(Usuario usuarioLogueado, Persistencia persistencia) {
        Cajero cajero = new Cajero();
        int opc;
        do {
            String menuCajero = """
                    ┏━━━━━━━━━ Menu Cajero ━━━━━━━━━┓       
                    ┃ 1. Buscar Viajes Disponibles  ┃       
                    ┃ 2. Vender / Reservar Pasaje   ┃       
                    ┃ 3. Ver Reportes               ┃       
                    ┃ 4. Salir                      ┃       
                    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛       
                    """;
            System.out.print(menuCajero);
            opc = leerOpcion("Ingresar opción: ", 1, 4);
            try {
                switch (opc) {
                    case 1 -> cajero.mostrarViajesDisponibles(persistencia, input);
                    case 2 -> cajero.venderPasajePorDestino(persistencia, input);
                    case 3 -> mostrarReportes(persistencia);
                    case 4 -> System.out.println("Saliendo del menú cajero...");
                }
            } catch (Exception opcionInvalida) {
                System.out.println("✖ Error al ejecutar la opción: " + opcionInvalida.getMessage());
            }
            System.out.println();
        } while (opc != 4);
    }

    // ------------------------ MENU REPORTES --------------------------------
    private static void mostrarReportes(Persistencia persistencia) {
        int opc;
        do {
            System.out.println("""
                    ┏━━━━━ Reportes ━━━━━━━┓
                    ┃ 1. Ventas            ┃
                    ┃ 2. Ocupación         ┃
                    ┃ 3. Volver            ┃
                    ┗━━━━━━━━━━━━━━━━━━━━━━┛
                    """);
            opc = leerOpcion("Opción: ", 1, 3);
            try {
                switch (opc) {
                    case 1 -> System.out.println(Reporte.generarReporteVentas(persistencia.getVentasRegistradas()));
                    case 2 -> System.out.println(Reporte.generarReporteOcupacion(obtenerTodosLosViajes(persistencia)));
                    case 3 -> System.out.println("Volviendo al menú principal...");
                }
            } catch (Exception errorGenerarReporte) {
                System.out.println("✖ Error al generar reporte: " + errorGenerarReporte.getMessage());
            }
            System.out.println();
        } while (opc != 3);
    }

    // Obtiene todos los viajes de la matriz de Persistencia en un arreglo 1D.
    private static Viaje[] obtenerTodosLosViajes(Persistencia persistencia) {
        Viaje[][] matriz = persistencia.getViajes();
        int max = Persistencia.getMaxDestinos() * Persistencia.getMaxHorarios();
        Viaje[] resultado = new Viaje[max];
        int cont = 0;
        for (int f = 0; f < Persistencia.getMaxDestinos(); f++) {
            for (int c = 0; c < Persistencia.getMaxHorarios(); c++) {
                if (matriz[f][c] != null) {
                    resultado[cont++] = matriz[f][c];
                }
            }
        }
        Viaje[] viajes = new Viaje[cont];
        System.arraycopy(resultado, 0, viajes, 0, cont);
        return viajes;
    }

    //verifica las opciones en un rango para evitar bucles infinitos
    private static int leerOpcion(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = input.nextLine();
            try {
                return Validacion.parsearEntero(linea, "Opción", min, max);
            } catch (IllegalArgumentException e) {
                System.out.println("✖ " + e.getMessage());
            }
        }
    }
}