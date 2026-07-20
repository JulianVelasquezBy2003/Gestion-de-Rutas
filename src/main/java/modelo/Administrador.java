package modelo;

import java.util.Scanner;

import datos.Persistencia;
import datos.Validacion;
import transporte.Bus;
import transporte.Ruta;
import transporte.Viaje;

/*
Administrador: configura la logística del sistema
- Registra rutas, agrega horarios, gestiona cajeros y contraseñas
@author Angela
 */
public class Administrador extends Usuario {

    // Constantes para límites de la matriz
    private static final int MAX_DESTINOS = Persistencia.getMaxDestinos();
    private static final int MAX_HORARIOS = Persistencia.getMaxHorarios();

    //Constructores
    public Administrador() {
    }

    public Administrador(int id, String nombre, String contrasena, String rol) {
        super(id, nombre, contrasena, rol);
    }

    // ----------------------------- GESTIÓN DE RUTAS -----------------------------
    /*
     Registra una nueva ruta con validaciones completas.
     - Origen y destino: texto no vacío y distintos.
     - Duración: entre 0.1 y 1000 horas.
     - Precio: entre 0.1 y 1,000,000 soles.
     - No permite duplicados (mismo origen/destino).
     - Guarda inmediatamente en el archivo XML usando guardarRuta()
     */
    public void registrarRuta(Persistencia persistencia, Scanner entrada) {
        System.out.println("\n━━━━━━━━ Registrar Nueva Ruta ━━━━━━━━");

        String origen = "";
        while (true) {
            System.out.print("Origen: ");
            origen = entrada.nextLine().trim();
            try {
                Validacion.validarTextoNoVacio(origen, "Origen");
                break;
            } catch (IllegalArgumentException noIngresoTexto) {
                System.out.println("✖ " + noIngresoTexto.getMessage());
            }
        }
        String destino = "";
        while (true) {
            System.out.print("Destino: ");
            destino = entrada.nextLine().trim();
            try {
                Validacion.validarTextoNoVacio(destino, "Destino");
                if (origen.equalsIgnoreCase(destino)) {
                    throw new IllegalArgumentException("Origen y destino no pueden ser iguales.");
                }
                break;
            } catch (IllegalArgumentException noIngresoTexto) {
                System.out.println("✖ " + noIngresoTexto.getMessage());
            }
        }
        double duracion = 0;
        while (true) {
            System.out.print("Duración estimada (horas): ");
            String texto = entrada.nextLine().trim();
            try {
                duracion = Validacion.parsearDecimal(texto, "Duración", 0.1, 1000.0);
                break;
            } catch (IllegalArgumentException duracionInvalida) {
                System.out.println("✖ " + duracionInvalida.getMessage());
            }
        }
        double precio = 0;
        while (true) {
            System.out.print("Precio Base (S/): ");
            String texto = entrada.nextLine().trim();
            try {
                precio = Validacion.parsearDecimal(texto, "Precio", 0.1, 1_000_000.0);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("✖ " + e.getMessage());
            }
        }
        // Validar ruta duplicada (ya tenemos origen y destino válidos)
        if (rutaExiste(persistencia, origen, destino)) {
            System.out.println("✖ Ya existe una ruta de \"" + origen + "\" a \"" + destino + "\".");
            return;
        }
        try {
            Ruta nuevaRuta = new Ruta(origen, destino, duracion, precio);
            persistencia.guardarRuta(nuevaRuta);
            System.out.println("✔ Ruta registrada (ID " + nuevaRuta.getIdRuta() + ").");
        } catch (Exception rutaYaExistente) {
            System.out.println("✖ " + rutaYaExistente.getMessage());
        }
    }

    // Verifica si ya existe una ruta con el mismo origen y destino.
    private boolean rutaExiste(Persistencia persistencia, String origen, String destino) {
        for (int i = 0; i < persistencia.getCantidadRutas(); i++) {
            Ruta r = persistencia.getRutas()[i];
            if (r.getOrigen().equalsIgnoreCase(origen) && r.getDestino().equalsIgnoreCase(destino)) {
                return true;
            }
        }
        return false;
    }

    // ----------------------------- AGREGAR HORARIOS -----------------------------
    /*
    Asigna un bus a una ruta en una fecha y hora específicas
    - Muestra rutas disponibles
    - Valida fecha (YYYY-MM-DD), hora (HH:MM), placa (3 letras + 3 números)
    - Busca fila (destino) y columna (horario) libres en la matriz
    - No permite duplicados (mismo destino, fecha y hora)
    - Si la placa ya existe, reutiliza el bus; si no, crea uno nuevo
    - Verifica que el bus no esté ocupado en el mismo horario
    - Guarda inmediatamente la matriz de viajes y los buses en XML
     */
    public void agregarHorario(Persistencia persistencia, Scanner entrada) {
        // Validar que haya rutas
        if (persistencia.getCantidadRutas() == 0) {
            System.out.println("⚠️ No hay rutas. Registre una primero (opción 1).");
            return;
        }
        // Mostrar rutas disponibles
        System.out.println("\n━━━━━━━━ Rutas disponibles ━━━━━━━━");
        Ruta[] rutas = persistencia.getRutas();
        for (int i = 0; i < persistencia.getCantidadRutas(); i++) {
            Ruta r = rutas[i];
            System.out.printf("%d. %s -> %s | S/ %.2f%n", r.getIdRuta(), r.getOrigen(), r.getDestino(), r.getPrecioBase());
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        int idRuta = 0;
        Ruta ruta = null;
        while (true) {
            System.out.print("ID de ruta: ");
            String texto = entrada.nextLine().trim();
            try {
                idRuta = Validacion.parsearEntero(texto, "ID de ruta", 1, 1_000_000);
                ruta = persistencia.buscarRutaPorId(idRuta);
                if (ruta == null) {
                    System.out.println("✖ No existe ruta con ID " + idRuta + ".");
                    continue;
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("✖ " + e.getMessage());
            }
        }
        String fecha = "";
        while (true) {
            System.out.print("Fecha (YYYY-MM-DD): ");
            fecha = entrada.nextLine().trim();
            try {
                validarFecha(fecha);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("✖ " + e.getMessage());
            }
        }
        String hora = "";
        while (true) {
            System.out.print("Hora (HH:MM): ");
            hora = entrada.nextLine().trim();
            try {
                Validacion.validarHora(hora);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("✖ " + e.getMessage());
            }
        }
        String placa = "";
        Bus bus = null;
        while (true) {
            System.out.print("Placa del bus (ABC-123): ");
            placa = entrada.nextLine().trim();
            try {
                validarPlaca(placa);
                // Buscar si ya existe un bus con esa placa
                bus = buscarBusPorPlaca(persistencia, placa);
                if (bus != null) {
                    System.out.println("✔ Bus encontrado (ID " + bus.getIdBus() + ", capacidad " + bus.getCapacidad() + ").");
                    break;
                } else {
                    // Bus nuevo: capacidad fija de 40 (estándar del sistema)
                    bus = new Bus(0, placa, 40);  // ID 0 se asignará después
                    System.out.println("✔ Nuevo bus creado con capacidad 40.");
                    break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("✖ " + e.getMessage());
            }
        }
        // Verificar que el bus no esté ocupado en ese horario
        if (busOcupadoEnHorario(persistencia, bus.getIdBus(), fecha, hora)) {
            System.out.println("✖ El bus " + bus.getPlaca() + " ya está ocupado en esa fecha y hora.");
            return;
        }

        // Procedemos a crear el viaje
        try {
            // Buscar fila (destino) y columna (horario) libres
            int fila = buscarFilaDestino(persistencia, ruta.getDestino());
            if (fila == -1) {
                throw new IllegalStateException("Máximo de " + MAX_DESTINOS + " destinos alcanzado.");
            }
            int columna = buscarColumnaLibre(persistencia, fila);
            if (columna == -1) {
                throw new IllegalStateException("Máximo de " + MAX_HORARIOS + " horarios para \"" + ruta.getDestino() + "\".");
            }
            //Evita que haya viajes duplicados
            if (viajeDuplicado(persistencia, ruta.getDestino(), fecha, hora)) {
                throw new IllegalArgumentException("Ya hay viaje a \"" + ruta.getDestino() + "\" el " + fecha + " a las " + hora + ".");
            }
            // Crear viaje
            Viaje viaje = new Viaje();
            viaje.setIdViaje(persistencia.siguienteIdViaje());
            viaje.setFecha(fecha);
            viaje.setHora(hora);
            viaje.setRuta(ruta);
            viaje.setBus(bus);
            if (!persistencia.agregarViaje(fila, columna, viaje)) {
                throw new IllegalStateException("La posición en la matriz ya estaba ocupada.");
            }
            // Guardar buses y viajes en XML
            if (bus.getIdBus() == 0) {
                int nuevoId = obtenerSiguienteIdBus(persistencia);
                bus.setIdBus(nuevoId);
                persistencia.guardarBus(bus);
            } else {
            }
            // Guarda la matriz actualizada en XML
            persistencia.guardarViajes();
            System.out.println("✔ Viaje agregado (ID " + viaje.getIdViaje() + ") a " + ruta.getDestino() + " (" + fecha + " " + hora + ").");
        } catch (Exception e) {
            System.out.println("✖ " + e.getMessage());
        }
    }

    // Busca un bus por su placa
    private Bus buscarBusPorPlaca(Persistencia persistencia, String placa) {
        Bus[] buses = persistencia.getBuses();
        for (int i = 0; i < persistencia.getCantidadBuses(); i++) {
            if (buses[i].getPlaca().equalsIgnoreCase(placa)) {
                return buses[i];
            }
        }
        return null;
    }

    // Verifica si un bus ya está ocupado en una fecha y hora determinadas.
    private boolean busOcupadoEnHorario(Persistencia persistencia, int idBus, String fecha, String hora) {
        Viaje[][] matriz = persistencia.getViajes();
        for (int f = 0; f < MAX_DESTINOS; f++) {
            for (int c = 0; c < MAX_HORARIOS; c++) {
                Viaje v = matriz[f][c];
                if (v != null && v.getBus() != null && v.getBus().getIdBus() == idBus
                        && v.getFecha().equals(fecha) && v.getHora().equals(hora)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Obtiene el siguiente ID disponible para un bus nuevo.
    private int obtenerSiguienteIdBus(Persistencia persistencia) {
        int max = 0;
        Bus[] buses = persistencia.getBuses();
        for (int i = 0; i < persistencia.getCantidadBuses(); i++) {
            if (buses[i].getIdBus() > max) {
                max = buses[i].getIdBus();
            }
        }
        return max + 1;
    }

    //  Busca una fila existente para el destino, o la primera fila vacía.
    private int buscarFilaDestino(Persistencia persistencia, String destino) {
        Viaje[][] matriz = persistencia.getViajes();
        // Buscar fila existente
        for (int f = 0; f < MAX_DESTINOS; f++) {
            for (int c = 0; c < MAX_HORARIOS; c++) {
                if (matriz[f][c] != null && matriz[f][c].getRuta().getDestino().equalsIgnoreCase(destino)) {
                    return f;
                }
            }
        }
        // Buscar primera fila vacía
        for (int f = 0; f < MAX_DESTINOS; f++) {
            boolean vacia = true;
            for (int c = 0; c < MAX_HORARIOS; c++) {
                if (matriz[f][c] != null) {
                    vacia = false;
                    break;
                }
            }
            if (vacia) {
                return f;
            }
        }
        return -1;
    }

    // Devuelve la primera columna libre en una fila.
    private int buscarColumnaLibre(Persistencia persistencia, int fila) {
        Viaje[][] matriz = persistencia.getViajes();
        for (int c = 0; c < MAX_HORARIOS; c++) {
            if (matriz[fila][c] == null) {
                return c;
            }
        }
        return -1;
    }

    // Verifica si ya existe viaje con mismo destino, fecha y hora.
    private boolean viajeDuplicado(Persistencia persistencia, String destino, String fecha, String hora) {
        Viaje[][] matriz = persistencia.getViajes();
        for (int f = 0; f < MAX_DESTINOS; f++) {
            for (int c = 0; c < MAX_HORARIOS; c++) {
                Viaje v = matriz[f][c];
                if (v != null && v.getRuta().getDestino().equalsIgnoreCase(destino)
                        && v.getFecha().equals(fecha) && v.getHora().equals(hora)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ----------------------------- GESTIÓN DE CAJEROS -----------------------------
    /*
     Crea un nuevo usuario con rol "cajero"
     - Nombre: al menos 3 caracteres alfanuméricos
     - Contraseña: mínimo 6 caracteres
     - No permite nombres duplicados ni exceder el límite
     - Guarda inmediatamente en el archivo XML con guardarUsuario()
     */
    public void agregarCajero(Persistencia persistencia, Scanner entrada) {
        System.out.println("\n━━━━━━━━ Registrar Nuevo Cajero ━━━━━━━━");
        String nombre = "";
        while (true) {
            System.out.print("Nombre de usuario: ");
            nombre = entrada.nextLine().trim();
            try {
                Validacion.validarTextoNoVacio(nombre, "Nombre");
                if (nombre.length() < 3) {
                    throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres.");
                }
                if (!nombre.matches("^[a-zA-Z0-9_]+$")) {
                    throw new IllegalArgumentException("Solo letras, números y guión bajo.");
                }
                if (persistencia.buscarUsuarioPorNombre(nombre) != null) {
                    throw new IllegalArgumentException("El usuario \"" + nombre + "\" ya existe.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
        String contrasena = "";
        while (true) {
            System.out.print("Contraseña: ");
            contrasena = entrada.nextLine().trim();
            try {
                Validacion.validarContrasena(contrasena);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
        try {
            int id = persistencia.siguienteIdUsuario();
            Usuario nuevo = new Cajero(id, nombre, contrasena, "cajero");
            persistencia.guardarUsuario(nuevo);
            System.out.println("✔Cajero \"" + nombre + "\" registrado (ID " + id + ").");
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ----------------------------- CAMBIAR CONTRASEÑA -----------------------------
    /*
     Cambia la contraseña de cualquier usuario
     - Valida que el usuario exista
     - Nueva contraseña: mínimo 6 caracteres y distinta a la actual
     - Utiliza el método de Persistencia que actualiza XML y memoria
     */
    public void cambiarContrasenaUsuario(Persistencia persistencia, Scanner entrada) {
        System.out.println("\n━━━━━━━━ Cambiar Contraseña ━━━━━━━━");
        mostrarUsuarios(persistencia);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String nombre = "";
        Usuario usuario = null;
        while (true) {
            System.out.print("Nombre de usuario: ");
            nombre = entrada.nextLine().trim();
            try {
                Validacion.validarTextoNoVacio(nombre, "Nombre");
                usuario = persistencia.buscarUsuarioPorNombre(nombre);
                if (usuario == null) {
                    throw new IllegalArgumentException("No existe \"" + nombre + "\".");
                }
                break;
            } catch (IllegalArgumentException nombreErroneo) {
                System.out.println("✖ " + nombreErroneo.getMessage());
            }
        }
        String nueva = "";
        while (true) {
            System.out.print("Nueva contraseña: ");
            nueva = entrada.nextLine().trim();
            try {
                Validacion.validarContrasena(nueva);
                if (nueva.equals(usuario.getContrasena())) {
                    throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("✖ " + e.getMessage());
            }
        }
        try {
            persistencia.cambiarContrasenaUsuario(nombre, nueva);
            System.out.println("✔ Contraseña de \"" + nombre + "\" actualizada.");
        } catch (Exception e) {
            System.out.println("✖ " + e.getMessage());
        }
    }

    // Muestra lista de usuarios (solo para referencia).
    private void mostrarUsuarios(Persistencia persistencia) {
        Usuario[] usuarios = persistencia.getUsuarios();
        for (int i = 0; i < persistencia.getCantidadUsuarios(); i++) {
            Usuario u = usuarios[i];
            System.out.println(u.getId() + ". " + u.getNombre() + " (" + u.getRol() + ")");
        }
    }

    // ------------------------ VALIDACIONES PRIVADAS ------------------------
    // Valida que la fecha tenga formato YYYY-MM-DD y sea razonable
    private void validarFecha(String fecha) {
        if (!fecha.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Formato YYYY-MM-DD.");
        }
        String[] p = fecha.split("-");
        int año = Integer.parseInt(p[0]);
        int mes = Integer.parseInt(p[1]);
        int dia = Integer.parseInt(p[2]);
        if (mes < 1 || mes > 12 || dia < 1 || dia > 31 || año < 2000) {
            throw new IllegalArgumentException("Fecha inválida (año≥2000, mes 1-12, día 1-31).");
        }
    }

    // Valida que la placa tenga formato: 3 letras + 3 números (con o sin guión)
    private void validarPlaca(String placa) {
        if (!placa.matches("^[A-Za-z]{3}-?\\d{3}$")) {
            throw new IllegalArgumentException("Formato: 3 letras + 3 números (ej. ABC-123).");
        }
    }
}
