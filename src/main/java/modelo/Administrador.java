package modelo;

import java.util.Scanner;

import datos.Persistencia;
import datos.Validacion;
import transporte.Bus;
import transporte.Ruta;
import transporte.Viaje;

// Usuario con rol Administrador. Se encarga de la configuracion logistica: registrar rutas, horarios y cuentas de cajero
public class Administrador extends Usuario {
    
    //Constructores
    public Administrador() {
    }

    public Administrador(int id, String nombre, String contraseña, String rol) {
        super(id, nombre, contraseña, rol);
    }

    // Metodos
    /* 
    1. Pide los datos de una nueva ruta por consola y la guarda en rutas.xml. 
    2. Reutiliza el Scanner y el objeto Persistencia que le pasa
    3. sincronizacion con el resto de la sesion: despues de guardar, 
    vuelve a cargar las rutas para que el arreglo en memoria quede al dia.
     */
    public void registrarRuta(Persistencia persistencia, Scanner entrada) {
        System.out.println("\n━━━━━━━━ Registrar Nueva Ruta ━━━━━━━━");
        System.out.print("Desde donde es su Origen: ");
        String origen = entrada.nextLine().trim();
        System.out.print("Cual es su Destino: ");
        String destino = entrada.nextLine().trim();
        System.out.print("Cual seria su duracion estimada (horas): ");
        String textoDuracion = entrada.nextLine().trim();
        System.out.print("Su Precio Base (S/): ");
        String textoPrecio = entrada.nextLine().trim();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Validacion.validarTextoNoVacioOExcepcion(origen, "Origen");
            Validacion.validarTextoNoVacioOExcepcion(destino, "Destino");

            double duracion = Validacion.validarYParsearDecimal(textoDuracion, "Duracion estimada (horas)", 0.0, 1000.0);
            double precio = Validacion.validarYParsearDecimal(textoPrecio, "Precio base", 0.0, 1_000_000.0);

            Ruta nuevaRuta = new Ruta(origen, destino, duracion, precio);
            persistencia.guardarRuta(nuevaRuta);
            // Refresca el arreglo de rutas en memoria para que la nueva
            // ruta este disponible de inmediato en el resto de la sesion.
            persistencia.cargarRutas();
            System.out.println("✅ Ruta registrada correctamente (ID " + nuevaRuta.getIdRuta() + ").");
        } catch (IllegalArgumentException datosInvalidos) {
            System.out.println("❌ " + datosInvalidos.getMessage());
        } catch (Exception errorAlGuardarRuta) {
            System.out.println("❌ No se pudo guardar la ruta: " + errorAlGuardarRuta.getMessage());
        }
    }

    /*
    1. Agrega un nuevo horario (Viaje) a la matriz Destinos x Frecuencias.
    2. El administrador elige una ruta ya registrada; el sistema ubica automaticamente la fila de la matriz 
    correspondiente al destino de esa ruta (o reserva una fila nueva si es el primer horario hacia ese destino),
    y busca la primera columna (frecuencia horaria) libre dentro de esa fila. Luego pide los datos del 
    bus asignado y guarda el horario en viajes.xml.
     */
    public void agregarHorario(Persistencia persistencia, Scanner entrada) {
        if (persistencia.getCantidadRutas() == 0) {
            System.out.println("⚠️ No hay rutas registradas todavia. Registre una ruta primero (opcion 1).");
            return;
        }

        System.out.println("\n━━━━━━━━━━ Rutas disponibles ━━━━━━━━-");
        Ruta[] rutas = persistencia.getRutas();
        for (int i = 0; i < persistencia.getCantidadRutas(); i++) {
            Ruta ruta = rutas[i];
            System.out.println(ruta.getIdRuta() + ". " + ruta.getOrigen() + " -> " + ruta.getDestino() + " | S/ " + ruta.getPrecioBase());
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.print("Ingresar numero de ruta disponible: ");
        String textoIdRuta = entrada.nextLine().trim();
        System.out.print("Fecha del viaje (ej. 2026-07-10): ");
        String fecha = entrada.nextLine().trim();
        System.out.print("Hora del viaje (ej. 08:00): ");
        String hora = entrada.nextLine().trim();
        System.out.print("Numero de bus: ");
        String textoIdBus = entrada.nextLine().trim();
        System.out.print("Placa del bus: ");
        String placa = entrada.nextLine().trim();
        System.out.print("Capacidad del bus (N° de pasajeros): ");
        String textoCapacidad = entrada.nextLine().trim();

        try {
            int idRuta = Validacion.validarYParsearEntero(textoIdRuta, "Numero de ruta", 1, 1_000_000);
            Ruta rutaElegida = persistencia.buscarRutaPorId(idRuta);
            if (rutaElegida == null) {
                throw new IllegalArgumentException("No existe una ruta con el numero " + idRuta + ".");
            }
            int fila = persistencia.obtenerOCrearFilaParaDestino(rutaElegida.getDestino());
            if (fila == -1) {
                throw new IllegalStateException("La matriz de destinos ya esta llena (maximo " + Persistencia.getMaxDestinos() + " destinos). No se puede agregar un destino nuevo.");
            }
            int columna = persistencia.primeraColumnaLibre(fila);
            if (columna == -1) {
                throw new IllegalStateException("Ya se alcanzo el maximo de frecuencias (" + Persistencia.getMaxHorarios() + ") para el destino \"" + rutaElegida.getDestino() + "\".");
            }
            Validacion.validarTextoNoVacioOExcepcion(fecha, "Fecha");
            Validacion.validarTextoNoVacioOExcepcion(hora, "Hora");
            Validacion.validarTextoNoVacioOExcepcion(placa, "Placa");
            int idBus = Validacion.validarYParsearEntero(textoIdBus, "Numero de bus", 0, 1_000_000);
            int capacidad = Validacion.validarYParsearEntero(textoCapacidad, "Capacidad del bus", 1, 1_000_000);
            Bus bus = new Bus(idBus, placa, capacidad);
            Viaje viaje = new Viaje();
            viaje.setIdViaje(persistencia.siguienteIdViaje());
            viaje.setFecha(fecha);
            viaje.setHora(hora);
            viaje.setRuta(rutaElegida);
            viaje.setBus(bus);
            boolean agregado = persistencia.agregarViaje(fila, columna, viaje);
            if (!agregado) {
                throw new IllegalStateException("La posicion en la matriz de horarios ya estaba ocupada.");
            }
            persistencia.guardarViajes();
            System.out.println("✅ Horario agregado correctamente hacia " + rutaElegida.getDestino() + " (Viaje ID " + viaje.getIdViaje() + ", " + fecha + " " + hora + ").");
        } catch (IllegalArgumentException | IllegalStateException datosInvalidos) {
            System.out.println("❌ " + datosInvalidos.getMessage());
        } catch (Exception errorAlAgregarHorario) {
            System.out.println("❌ No se pudo agregar el horario: " + errorAlAgregarHorario.getMessage());
        }
    }

    /*
    Registra un nuevo usuario con rol "cajero": pide nombre y contrasena, valida los datos y evita 
    duplicados lanzando excepciones propias (IllegalArgumentException/IllegalStateException) que se 
    resuelven todas en un unico catch, en lugar de usar metodos de validacion repetidos.
     */
    public void agregarCajero(Persistencia persistencia, Scanner entrada) {
        System.out.println("\n━━━━━━━━ Registrar Nuevo Cajero ━━━━━━━━");
        System.out.print("Nombre de usuario del cajero: ");
        String nombre = entrada.nextLine().trim();
        System.out.print("Contraseña del cajero: ");
        String contrasena = entrada.nextLine().trim();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Validacion.validarTextoNoVacioOExcepcion(nombre, "Nombre de usuario");
            Validacion.validarTextoNoVacioOExcepcion(contrasena, "Contraseña");
            if (persistencia.existeUsuario(nombre)) {
                throw new IllegalArgumentException("Ya existe un usuario registrado con el nombre \"" + nombre + "\".");
            }
            if (persistencia.getCantidadUsuarios() >= Persistencia.getMaxUsuarios()) {
                throw new IllegalStateException("Se alcanzo el maximo de usuarios permitidos (" + Persistencia.getMaxUsuarios() + ").");
            }
            int idNuevo = persistencia.siguienteIdUsuario();
            Cajero nuevoCajero = new Cajero(idNuevo, nombre, contrasena, "cajero");
            persistencia.guardarUsuario(nuevoCajero);
            // Refresca el arreglo de usuarios en memoria para que el nuevo cajero pueda iniciar sesion de inmediato.
            persistencia.cargarXML();
            System.out.println("✅ Cajero \"" + nombre + "\" registrado correctamente (ID " + idNuevo + ").");
        } catch (IllegalArgumentException | IllegalStateException datosInvalidos) {
            System.out.println("❌ " + datosInvalidos.getMessage());
        } catch (Exception errorAlGuardarCajero) {
            System.out.println("❌ No se pudo registrar el cajero: " + errorAlGuardarCajero.getMessage());
        }
    }

    /*
    Permite al administrador cambiar la contrasena de cualquier usuario registrado (admin o cajero),
    ubicandolo por su nombre de usuario. Reutiliza el mismo enfoque de try/catch con excepciones 
    propias en lugar de metodos de validacion repetidos.
     */
    public void cambiarContrasenaUsuario(Persistencia persistencia, Scanner entrada) {
        System.out.println("\n━━━━━━━━ Cambiar Contraseña de Usuario ━━━━━━━━");
        Usuario[] usuarios = persistencia.getUsuarios();
        for (int i = 0; i < persistencia.getCantidadUsuarios(); i++) {
            System.out.println(usuarios[i].getId() + ". " + usuarios[i].getNombre() + " (" + usuarios[i].getRol() + ")");
        }
        System.out.print("Nombre de usuario a modificar: ");
        String nombre = entrada.nextLine().trim();
        System.out.print("Nueva contraseña: ");
        String nuevaContrasena = entrada.nextLine().trim();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Validacion.validarTextoNoVacioOExcepcion(nuevaContrasena, "Nueva contraseña");
            if (persistencia.buscarUsuarioPorNombre(nombre) == null) {
                throw new IllegalArgumentException("No existe un usuario registrado con el nombre \"" + nombre + "\".");
            }
            persistencia.cambiarContrasenaUsuario(nombre, nuevaContrasena);
            System.out.println("✅ Contraseña de \"" + nombre + "\" actualizada correctamente.");
        } catch (IllegalArgumentException datosInvalidos) {
            System.out.println("❌ " + datosInvalidos.getMessage());
        } catch (Exception errorAlCambiarContrasena) {
            System.out.println("❌ No se pudo cambiar la contraseña: " + errorAlCambiarContrasena.getMessage());
        }
    }

}