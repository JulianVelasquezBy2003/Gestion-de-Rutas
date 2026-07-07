package modelo;

import java.util.Scanner;

import datos.Persistencia;
import transporte.Bus;
import transporte.Ruta;
import transporte.Viaje;

/**
 * Usuario con rol Administrador. Se encarga de la configuracion logistica:
 * registrar rutas, horarios y cuentas de cajero.
 */
public class Administrador extends Usuario {

    //Constructores
    public Administrador() {
    }

    public Administrador(int id, String nombre, String contraseña, String rol) {
        super(id, nombre, contraseña, rol);
    }

    // Metodos

    /**
     * Pide los datos de una nueva ruta por consola y la guarda en
     * rutas.xml. Reutiliza el Scanner y el objeto Persistencia que le pasa
     * MenuPrincipal (en vez de crear los suyos propios), para no perder
     * sincronizacion con el resto de la sesion: despues de guardar, vuelve
     * a cargar las rutas para que el arreglo en memoria quede al dia.
     *
     * @param persistencia usado para guardar la ruta y refrescar el arreglo en memoria
     * @param sc Scanner compartido para leer la entrada del usuario
     */
    public void registrarRuta(Persistencia persistencia, Scanner sc) {
        String origen = leerTextoNoVacio(sc, "Desde donde es su Origen: ");
        String destino = leerTextoNoVacio(sc, "Cual es su Destino: ");
        double duracion = leerDecimal(sc, "Cual seria su duracion estimada (horas): ");
        double precio = leerDecimal(sc, "Su Precio Base (S/): ");

        Ruta nuevaRuta = new Ruta(origen, destino, duracion, precio);

        try {
            persistencia.guardarRuta(nuevaRuta);
            // Refresca el arreglo de rutas en memoria para que la nueva
            // ruta este disponible de inmediato en el resto de la sesion.
            persistencia.cargarRutas();
            System.out.println("✅ Ruta registrada correctamente (ID " + nuevaRuta.getIdRuta() + ").");
        } catch (Exception ex) {
            System.out.println("❌ No se pudo guardar la ruta: " + ex.getMessage());
        }
    }

    /**
     * Agrega un nuevo horario (Viaje) a la matriz Destinos x Frecuencias.
     * El administrador elige una ruta ya registrada; el sistema ubica
     * automaticamente la fila de la matriz correspondiente al destino de
     * esa ruta (o reserva una fila nueva si es el primer horario hacia
     * ese destino), y busca la primera columna (frecuencia horaria) libre
     * dentro de esa fila. Luego pide los datos del bus asignado y guarda
     * el horario en viajes.xml.
     *
     * @param persistencia contiene las rutas y la matriz de viajes en memoria
     * @param sc Scanner compartido para leer la entrada del usuario
     */
    public void agregarHorario(Persistencia persistencia, Scanner sc) {
        if (persistencia.getCantidadRutas() == 0) {
            System.out.println("⚠️ No hay rutas registradas todavia. Registre una ruta primero (opcion 1).");
            return;
        }

        System.out.println("\n━━━━━━━━━━ Rutas disponibles ━━━━━━━━-");
        Ruta[] rutas = persistencia.getRutas();
        for (int i = 0; i < persistencia.getCantidadRutas(); i++) {
            Ruta r = rutas[i];
            System.out.println(r.getIdRuta() + ". " + r.getOrigen() + " -> " + r.getDestino()
                    + " | S/ " + r.getPrecioBase());
        }

        Ruta rutaElegida = null;
        while (rutaElegida == null) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.print("Ingresar numero de ruta disponible: ");
            try {
                int idRuta = Integer.parseInt(sc.nextLine().trim());
                rutaElegida = persistencia.buscarRutaPorId(idRuta);
                if (rutaElegida == null) {
                    System.out.println("❌ No existe ese numero de ruta.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Ingrese un numero valido.");
            }
        }

        int fila = persistencia.obtenerOCrearFilaParaDestino(rutaElegida.getDestino());
        if (fila == -1) {
            System.out.println("❌ La matriz de destinos ya esta llena (maximo " + Persistencia.getMaxDestinos()+ " destinos). No se puede agregar un destino nuevo.");
            return;
        }

        int columna = persistencia.primeraColumnaLibre(fila);
        if (columna == -1) {
            System.out.println("❌ Ya se alcanzo el maximo de frecuencias (" + Persistencia.getMaxHorarios()+ ") para el destino \"" + rutaElegida.getDestino() + "\".");
            return;
        }

        String fecha = leerTextoNoVacio(sc, "Fecha del viaje (ej. 2026-07-10): ");
        String hora = leerTextoNoVacio(sc, "Hora del viaje (ej. 08:00): ");

        int idBus = leerEntero(sc, "Numero de bus: ");
        String placa = leerTextoNoVacio(sc, "Placa del bus: ");
        int capacidad = leerEntero(sc, "Capacidad del bus (N° de pasajeros): ");

        Bus bus = new Bus(idBus, placa, capacidad);

        Viaje viaje = new Viaje();
        viaje.setIdViaje(persistencia.siguienteIdViaje());
        viaje.setFecha(fecha);
        viaje.setHora(hora);
        viaje.setRuta(rutaElegida);
        viaje.setBus(bus);

        boolean agregado = persistencia.agregarViaje(fila, columna, viaje);
        if (!agregado) {
            System.out.println("❌ No se pudo agregar el horario: la posicion ya estaba ocupada.");
            return;
        }

        try {
            persistencia.guardarViajes();
            System.out.println("✅ Horario agregado correctamente hacia " + rutaElegida.getDestino()+ " (Viaje ID " + viaje.getIdViaje() + ", " + fecha + " " + hora + ").");
        } catch (Exception e) {
            System.out.println("⚠️ El horario se agrego en memoria pero no se pudo guardar en viajes.xml: "+ e.getMessage());
        }
    }

    /*public Cajero crearCajero(String nombre, String contrasena) {
    Cajero cajero = new Cajero(nombre, contrasena);

    usuarios[cantidadUsuarios] = cajero;
    cantidadUsuarios++;

    return cajero;
} */

    // ---------- Helpers de lectura ----------

    private String leerTextoNoVacio(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        String texto = sc.nextLine().trim();
        while (texto.isEmpty()) {
            System.out.print("❌ Este dato no puede estar vacio. Ingrese nuevamente: ");
            texto = sc.nextLine().trim();
        }
        return texto;
    }

    private double leerDecimal(Scanner sc, String mensaje) {
        double valor = -1;
        while (valor < 0) {
            System.out.print(mensaje);
            try {
                valor = Double.parseDouble(sc.nextLine().trim());
                if (valor < 0) {
                    System.out.println("❌ El valor no puede ser negativo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada invalida. Debe ingresar un numero.");
                valor = -1;
            }
        }
        return valor;
    }

    private int leerEntero(Scanner sc, String mensaje) {
        int valor = -1;
        while (valor < 0) {
            System.out.print(mensaje);
            try {
                valor = Integer.parseInt(sc.nextLine().trim());
                if (valor < 0) {
                    System.out.println("❌ El valor no puede ser negativo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada invalida. Debe ingresar un numero entero.");
                valor = -1;
            }
        }
        return valor;
    }
}