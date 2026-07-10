package gestionderutas;

import java.util.Scanner;

import modelo.Administrador;
import modelo.Cajero;
import modelo.Reporte;
import modelo.Usuario;
import datos.Persistencia;
import transporte.Venta;
import transporte.Viaje;

/* Clase principal del sistema TransRoute. 
    - Contenedor el metodo main()
    - el login de usuarios 
    -Navegacion  del menu de Administrador o el menu de Cajero segun rol
@author Julian,Angela
 */
public class MenuPrincipal {

    // Un unico Scanner para todos los metodos
    private static final Scanner input = new Scanner(System.in);

    // Cantidad maxima de intentos de login antes de cerrar el programa.
    private static final int MAX_INTENTOS_LOGIN = 3;

    public static void main(String[] args) {

        //Carga de datos persistidos (XML)
        Persistencia persistencia = new Persistencia();
        try {
            persistencia.cargarXML();
        } catch (Exception e) {
            System.out.println("❌ Error al cargar los datos del sistema (usuarios.xml).");
            System.out.println("Detalle: " + e.getMessage());
            System.out.println("El programa no puede continuar sin la informacion de usuarios.");
            return; // No se puede continuar sin usuarios cargados
        }

        // Las rutas y la matriz de viajes no son indispensables para el login, asi que si fallan solo se avisa y el programa continua.
        try {
            persistencia.cargarRutas();
        } catch (Exception errorCargarRutas) {
            System.out.println("⚠️ No se pudieron cargar las rutas (rutas.xml): " + errorCargarRutas.getMessage());
        }

        try {
            persistencia.cargarViajes();
        } catch (Exception errorCargarViajes) {
            System.out.println("⚠️ No se pudo cargar la matriz de horarios (viajes.xml): " + errorCargarViajes.getMessage());
        }

        try {
            persistencia.cargarVentas();
        } catch (Exception errorCargarVentas) {
            System.out.println("⚠️ No se pudo cargar el historial de ventas (ventas.xml): " + errorCargarVentas.getMessage());
        }

        Usuario[] usuarios = persistencia.getUsuarios();
        int cantidadUsuarios = persistencia.getCantidadUsuarios();

        // Login con limite de intentos
        Usuario usuarioLogueado = null;
        int intentos = 0;
        while (usuarioLogueado == null && intentos < MAX_INTENTOS_LOGIN) {
            usuarioLogueado = login(usuarios, cantidadUsuarios);
            intentos++;
            if (usuarioLogueado == null && intentos < MAX_INTENTOS_LOGIN) {
                System.out.println("Intento " + intentos + " de " + MAX_INTENTOS_LOGIN + ".\n");
            }
        }
        if (usuarioLogueado == null) {
            System.out.println("❌ Numero maximo de intentos alcanzado. Cerrando el sistema.");
            return;
        }

        // Menu segun rol
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

        // Guardado automatico al salir 
        try {
            persistencia.guardarDatos();
            System.out.println("✅ Datos guardados correctamente (asientos y ventas).");
        } catch (Exception e) {
            System.out.println("❌ No se pudieron guardar los datos antes de salir: " + e.getMessage());
        }
        System.out.println("Sesion finalizada. Hasta pronto, " + usuarioLogueado.getNombre() + ".");
    }

    /* Solicita usuario y contrasena por consola y valida contra el arreglo de usuarios cargado desde el XML.
    usuarios:  arreglo de usuarios cargados en memoria
    cantidadUsuarios: cantidad de posiciones validas dentro del arreglo
    return el Usuario si las credenciales son correctas, null en caso contrario
     */
    public static Usuario login(Usuario[] usuarios, int cantidadUsuarios) {
        System.out.println("┏━━━━━━━━━━━━━━━━ Login ━━━━━━━━━━━━━━━━━━┓");
        System.out.print("┃ Ingresar Nombre de Usuario: ");
        String nombre = input.nextLine().trim();
        System.out.print("┃ Contraseña: ");
        String contrasena = input.nextLine().trim();
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

        if (nombre.isEmpty() || contrasena.isEmpty()) {
            System.out.println("❌ Usuario y contraseña no pueden estar vacios.\n");
            return null;
        }
        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].getNombre().equals(nombre) && usuarios[i].getContrasena().equals(contrasena)) {
                System.out.println("✅ Login exitoso. Bienvenido, " + usuarios[i].getNombre() + "!\n");
                return usuarios[i];
            }
        }
        System.out.println("❌ Credenciales incorrectas.\n");
        return null;
    }

    // Menu de opciones para el usuario con rol Administrador*/
    public static void menuAdministrador(Usuario usuarioLogueado, Persistencia persistencia) {
        Administrador adm = new Administrador();
        int opc;
        do {
            String menuAdmin = """
                          ┏━━━━━━ Menu Administrador ━━━━━━┓      
                          ┃ 1. Registrar Ruta              ┃      
                          ┃ 2. Agregar Horario             ┃      
                          ┃ 3. Agregar Nuevo Cajero        ┃      
                          ┃ 4. Ver Reportes                ┃      
                          ┃ 5. Salir                       ┃      
                          ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛      
                          """;
            System.out.print(menuAdmin);
            opc = leerOpcion("Ingresar opcion: ", 1, 5);

            try {
                switch (opc) {
                    case 1 ->
                        adm.registrarRuta(persistencia, input);
                    case 2 ->
                        adm.agregarHorario(persistencia, input);
                    case 3 ->
                        System.out.println("⚠️ Agregar Nuevo Cajero: funcionalidad en desarrollo.");
                    case 4 ->
                        verReporte(new Reporte());
                    case 5 ->
                        System.out.println("Saliendo del menu administrador...");
                }
            } catch (Exception e) {
                // Evita que un error inesperado dentro de una opcion tumbe todo el programa
                System.out.println("❌ Ocurrio un error al ejecutar la opcion: " + e.getMessage());
            }
            System.out.println();
        } while (opc != 5);
    }

    // Menu de opciones para el usuario con rol Cajero.
    public static void menuCajero(Usuario usuarioLogueado, Persistencia persistencia) {
        Cajero cajero = new Cajero();
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
            opc = leerOpcion("Ingresar opcion: ", 1, 4);

            try {
                switch (opc) {
                    case 1 ->
                        mostrarViajesDisponibles(persistencia);
                    case 2 ->
                        venderPasajePorDestino(persistencia, cajero);
                    case 3 ->
                        verReporte(new Reporte());
                    case 4 ->
                        System.out.println("Saliendo del menu cajero...");
                }
            } catch (Exception e) {
                System.out.println("❌ Ocurrio un error al ejecutar la opcion: " + e.getMessage());
            }
            System.out.println();
        } while (opc != 4);
    }

    /* Pide un destino y muestra (sin vender) los viajes programados hacia ese destino, recorriendo la matriz de horarios (Destinos x Frecuencias).
    persistencia: contiene la matriz de viajes ya cargada
     */
    private static void mostrarViajesDisponibles(Persistencia persistencia) {
        System.out.print("Ingrese el destino a buscar: ");
        String destino = input.nextLine().trim();
        Viaje[] disponibles = persistencia.buscarViajesPorDestino(destino);

        if (disponibles.length == 0) {
            System.out.println("⚠️ No se encontraron viajes programados hacia \"" + destino + "\".\n");
            return;
        }
        System.out.println("\n--- Viajes disponibles hacia " + destino + " ---");
        for (int i = 0; i < disponibles.length; i++) {
            imprimirResumenViaje(i + 1, disponibles[i]);
        }
        System.out.println();
    }

    /* Pide un destino, muestra los viajes disponibles y permite elegir uno para iniciar el flujo completo de venta/reserva de pasaje.
    persistencia: contiene la matriz de viajes ya cargada
    cajero: instancia que ejecuta el flujo de venta
     */
    private static void venderPasajePorDestino(Persistencia persistencia, Cajero cajero) {
        System.out.print("Ingrese el destino: ");
        String destino = input.nextLine().trim();

        Viaje[] disponibles = persistencia.buscarViajesPorDestino(destino);

        if (disponibles.length == 0) {
            System.out.println("⚠️ No se encontraron viajes programados hacia \"" + destino + "\".\n");
            return;
        }

        System.out.println("\n--- Viajes disponibles hacia " + destino + " ---");
        for (int i = 0; i < disponibles.length; i++) {
            imprimirResumenViaje(i + 1, disponibles[i]);
        }

        int opcion = leerOpcion("Elija el viaje (0 para cancelar): ", 0, disponibles.length);
        if (opcion == 0) {
            System.out.println("Operacion cancelada.\n");
            return;
        }

        Viaje viajeElegido = disponibles[opcion - 1];
        Venta venta = cajero.venderPasaje(viajeElegido, input);

        if (venta != null) {
            boolean registrada = persistencia.registrarVenta(venta);
            if (!registrada) {
                System.out.println("⚠️ La venta se realizo pero no se pudo guardar en el historial (limite alcanzado).");
            }
        }
    }

    /*Imprime una linea de resumen de un viaje (ruta, fecha, hora, bus, precio y ocupacion actual), protegiendose de datos nulos.
     */
    private static void imprimirResumenViaje(int numero, Viaje viaje) {
        String origen = (viaje.getRuta() != null) ? viaje.getRuta().getOrigen() : "N/D";
        String destinoRuta = (viaje.getRuta() != null) ? viaje.getRuta().getDestino() : "N/D";
        double precio = (viaje.getRuta() != null) ? viaje.getRuta().getPrecioBase() : 0.0;
        String placa = (viaje.getBus() != null) ? viaje.getBus().getPlaca() : "N/D";
        int ocupados = (viaje.getBus() != null) ? viaje.getBus().contarAsientosOcupados() : 0;
        int capacidad = (viaje.getBus() != null) ? viaje.getBus().getCapacidad() : 0;

        System.out.printf("%d. %s -> %s | %s %s | Bus %s | S/ %.2f | Ocupacion: %d/%d%n",
                numero, origen, destinoRuta, viaje.getFecha(), viaje.getHora(), placa, precio, ocupados, capacidad);
    }

    /*  Punto de entrada para mostrar reportes (de ventas u ocupacion). La logica interna de Reporte todavia esta pendiente de implementar.
    reporte : instancia de Reporte a mostrar
     */
    public static void verReporte(Reporte reporte) {
        System.out.println("⚠️ Modulo de reportes: funcionalidad en desarrollo.");
    }

    /* Lee un numero entero desde consola de forma segura, dentro de un rango [min, max], 
    reintentando en caso de que el usuario ingrese texto invalido (evita que el programa se caiga con InputMismatchException).
    mensaje:  texto que se muestra al pedir el dato
    min: valor minimo aceptado (inclusive)
    max: valor maximo aceptado (inclusive)
    return el entero valido ingresado por el usuario
     */
    private static int leerOpcion(String mensaje, int min, int max) {
        int valor = -1;
        boolean valido = false;

        while (!valido) {
            System.out.print(mensaje);
            try {
                valor = Integer.parseInt(input.nextLine().trim());
                if (valor < min || valor > max) {
                    System.out.println("❌ Ingrese un numero entre " + min + " y " + max + ".");
                } else {
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada invalida. Debe ingresar solo numeros.");
            }
        }
        return valor;
    }
}
