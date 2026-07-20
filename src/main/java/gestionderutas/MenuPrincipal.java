package gestionderutas;

import java.util.Scanner;

import modelo.Administrador;
import modelo.Cajero;
import modelo.Reporte;
import modelo.Usuario;
import datos.Persistencia;
import datos.Validacion;

/*
Clase principal del sistema TransRoute. - Contenedor del método main() -
Login de usuarios - Navegación del menú de Administrador o Cajero según rol
@author Julian, Angela
 */
public class MenuPrincipal {

    // Un único Scanner para todos los métodos
    private static final Scanner input = new Scanner(System.in);

    // Cantidad máxima de intentos de login antes de cerrar el programa
    private static final int INTENTOS_LOGIN = 3;

    public static void main(String[] args) {
        // Carga de datos persistidos (XML)
        Persistencia persistencia = new Persistencia();
        try {
            persistencia.cargarXML();
        } catch (Exception errorCargarUsuarios) {
            System.out.println("❌ Error al cargar los datos del sistema (usuarios.xml).");
            System.out.println("Detalle: " + errorCargarUsuarios.getMessage());
            System.out.println("El programa no puede continuar sin la información de usuarios.");
            return; // No se puede continuar sin usuarios cargados
        }

        // Carga de datos adicionales (no críticos)
        cargarDatosAdicionales(persistencia);

        Usuario[] usuarios = persistencia.getUsuarios();
        int cantidadUsuarios = persistencia.getCantidadUsuarios();

        // Login con límite de intentos
        Usuario usuarioLogueado = null;
        int intentos = 0;
        while (usuarioLogueado == null && intentos < INTENTOS_LOGIN) {
            usuarioLogueado = login(usuarios, cantidadUsuarios);
            intentos++;
            if (usuarioLogueado == null && intentos < INTENTOS_LOGIN) {
                System.out.println("Intento " + intentos + " de " + INTENTOS_LOGIN + ".\n");
            }
        }

        if (usuarioLogueado == null) {
            System.out.println("❌ Número máximo de intentos alcanzado. Cerrando el sistema.");
            return;
        }

        // Menú según rol
        String rol = usuarioLogueado.getRol();
        if (rol == null) {
            System.out.println("❌ El usuario no tiene un rol asignado. Contacte al administrador.");
            return;
        }

        switch (rol.toLowerCase()) {
            case "admin" ->
                menuAdministrador(usuarioLogueado, persistencia);
            case "cajero" ->
                menuCajero(usuarioLogueado, persistencia);
            default ->
                System.out.println("❌ Rol no reconocido: " + rol);
        }

        // Guardado automático al salir
        guardarDatos(persistencia);
        System.out.println("Sesión finalizada. Hasta pronto, " + usuarioLogueado.getNombre() + ".");
    }

    // Carga datos adicionales (rutas, viajes, ventas) de forma segura. Si alguno falla, continúa sin detener el programa.
    private static void cargarDatosAdicionales(Persistencia persistencia) {
        try {
            persistencia.cargarRutas();
        } catch (Exception e) {
            System.out.println("⚠️ No se pudieron cargar las rutas (rutas.xml): " + e.getMessage());
        }
        try {
            persistencia.cargarViajes();
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar la matriz de horarios (viajes.xml): " + e.getMessage());
        }
        try {
            persistencia.cargarVentas();
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar el historial de ventas (ventas.xml): " + e.getMessage());
        }
    }

    //Guarda todos los datos antes de salir del programa.
    private static void guardarDatos(Persistencia persistencia) {
        try {
            persistencia.guardarDatos();
            System.out.println("✅ Datos guardados correctamente (asientos y ventas).");
        } catch (Exception e) {
            System.out.println("❌ No se pudieron guardar los datos antes de salir: " + e.getMessage());
        }
    }

    //Solicita usuario y contraseña por consola y valida contra el arreglo de usuarios.
    public static Usuario login(Usuario[] usuarios, int cantidadUsuarios) {
        System.out.println("┏━━━━━━━━━━━━━━━━ Login ━━━━━━━━━━━━━━━━━━┓");
        System.out.print("┃ Ingresar Nombre de Usuario: ");
        String nombre = input.nextLine().trim();
        System.out.print("┃ Contraseña: ");
        String contrasena = input.nextLine().trim();
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

        try {
            Validacion.validarTextoNoVacioOExcepcion(nombre, "Usuario");
            Validacion.validarTextoNoVacioOExcepcion(contrasena, "Contraseña");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage() + "\n");
            return null;
        }

        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].getNombre().equals(nombre)
                    && usuarios[i].verificarContrasena(contrasena)) {
                System.out.println("✅ Login exitoso. ¡Bienvenido, " + usuarios[i].getNombre() + "!\n");
                return usuarios[i];
            }
        }
        System.out.println("❌ Credenciales incorrectas.\n");
        return null;
    }

    //Menú de opciones para el usuario con rol Administrador.
    public static void menuAdministrador(Usuario usuarioLogueado, Persistencia persistencia) {
        Administrador adm = new Administrador();
        Reporte reporte = new Reporte();
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
            // Leer opción usando validación estática
            opc = leerOpcion("Ingresar opción: ", 1, 6);
            try {
                switch (opc) {
                    case 1 ->
                        adm.registrarRuta(persistencia, input);
                    case 2 ->
                        adm.agregarHorario(persistencia, input);
                    case 3 ->
                        adm.agregarCajero(persistencia, input);
                    case 4 ->
                        adm.cambiarContrasenaUsuario(persistencia, input);
                    case 5 ->
                        reporte.mostrarMenuReportes(persistencia, input);
                    case 6 ->
                        System.out.println("Saliendo del menú administrador...");
                }
            } catch (Exception opcionInvalida) {
                System.out.println("❌ Ocurrió un error al ejecutar la opción: " + opcionInvalida.getMessage());
            }
            System.out.println();
        } while (opc != 6);
    }

    //Menú de opciones para el usuario con rol Cajero.
    public static void menuCajero(Usuario usuarioLogueado, Persistencia persistencia) {
        Cajero cajero = new Cajero();
        Reporte reporte = new Reporte();
        int opc;
        do {
            String menuCajero = """
                    ┏━━━━━━━━━ Menu Cajero ━━━━━━━━━┓       
                    ┃ 1. Buscar Viajes Disponibles  ┃       
                    ┃ 2. Vender / Reservar Pasaje   ┃       
                    ┃ 3. Ver Reporte de Ventas      ┃       
                    ┃ 4. Salir                      ┃       
                    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛       
                    """;
            System.out.print(menuCajero);
            // Leer opción usando validación estática
            opc = leerOpcion("Ingresar opción: ", 1, 4);
            try {
                switch (opc) {
                    case 1 ->
                        cajero.mostrarViajesDisponibles(persistencia, input);
                    case 2 ->
                        cajero.venderPasajePorDestino(persistencia, input);
                    case 3 ->
                        reporte.mostrarReporteVentas(persistencia, input);
                    case 4 ->
                        System.out.println("Saliendo del menú cajero...");
                }
            } catch (Exception opcionInvalida) {
                System.out.println("❌ Ocurrió un error al ejecutar la opción: " + opcionInvalida.getMessage());
            }
            System.out.println();
        } while (opc != 4);
    }

    /* 
    Lee un número entero desde consola de forma segura, dentro de un rango [min, max], reintentando 
    en caso de que el usuario ingrese texto inválido.
     */
    private static int leerOpcion(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String linea = input.nextLine();
            try {
                return Validacion.validarYParsearEntero(linea, "Opción", min, max);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }
}
