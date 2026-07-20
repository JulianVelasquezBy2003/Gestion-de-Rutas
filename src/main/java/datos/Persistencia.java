package datos;

import java.io.File;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import modelo.Usuario;
import transporte.Bus;
import transporte.Pasajero;
import transporte.Ruta;
import transporte.Venta;
import transporte.Viaje;

/*
Esta clase se encarga de guardar y recuperar toda la información del sistema
usando archivos XML. Es el "archivo" donde se almacenan usuarios, rutas, viajes, buses y ventas.
@author Julian, Angela, Yulisa
 */
public class Persistencia {

    // Carpeta donde se guardan los archivos XML
    private static final String CARPETA_XML = "src/main/resources/xml/";

    // Cantidad máxima de usuarios que podemos tener.
    private static final int MAX_USUARIOS = 100;
    //Cantidad máxima de rutas
    private static final int MAX_RUTAS = 50;
    // Cantidad máxima de buses
    private static final int MAX_BUSES = 20;
    // Número máximo de destinos diferentes (filas de la matriz de viajes)
    private static final int MAX_DESTINOS = 10;
    // Número máximo de horarios por destino (columnas de la matriz)
    private static final int MAX_HORARIOS = 5;
    // Cantidad máxima de ventas que podemos guardar
    private static final int MAX_VENTAS = 500;

    // Lista de todos los usuarios registrados
    private Usuario[] usuarios = new Usuario[MAX_USUARIOS];
    private int cantidadUsuarios = 0;

    // Lista de todas las rutas disponibles
    private Ruta[] rutas = new Ruta[MAX_RUTAS];
    private int cantidadRutas = 0;

    // Lista de todos los buses (cada bus tiene su mapa de asientos)
    private Bus[] buses = new Bus[MAX_BUSES];
    private int cantidadBuses = 0;

    /*
    Matriz de viajes: cada fila es un destino y cada columna es un horario.
    Si una celda está vacía (null), significa que no hay viaje en ese horario.
     */
    private Viaje[][] viajes = new Viaje[MAX_DESTINOS][MAX_HORARIOS];

    // Historial de todas las ventas realizadas
    private Venta[] ventas = new Venta[MAX_VENTAS];
    private int cantidadVentas = 0;

    //----------------- MÉTODOS PARA MANEJO DE LOS XML ----------------
    // Devuelve el archivo XML con el nombre indicado
    private File archivoXml(String nombre) {
        return new File(CARPETA_XML + nombre);
    }
    // Crea un "lector" de XML
    private DocumentBuilder crearParser() throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    // Guarda un documento XML en disco con formato bonito (indentado)
    private void guardarDocumento(Document doc, String nombre) throws Exception {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        t.transform(new DOMSource(doc), new StreamResult(new File(CARPETA_XML + nombre)));
    }

    // -------------------------- CARGAR TODO AL INICIAR ---------------------
    /// Lee todos los archivos XML y los carga en memoria
    public void cargarTodos() throws Exception {
        cargarUsuarios();
        cargarRutas();
        cargarBuses();      // Primero los buses
        cargarViajes();     // Luego los viajes (necesitan buses)
        cargarVentas();     // Finalmente las ventas (necesitan viajes)
    }

    //--------------------------------  USUARIOS --------------------------------
    private void cargarUsuarios() throws Exception {
        Document doc = crearParser().parse(archivoXml("usuarios.xml"));
        doc.getDocumentElement().normalize();
        NodeList lista = doc.getElementsByTagName("usuario");
        cantidadUsuarios = 0;
        for (int i = 0; i < lista.getLength() && cantidadUsuarios < MAX_USUARIOS; i++) {
            Element el = (Element) lista.item(i);
            int id = Integer.parseInt(el.getAttribute("id"));
            String nombre = el.getElementsByTagName("nombre").item(0).getTextContent();
            String pass = el.getElementsByTagName("contrasena").item(0).getTextContent();
            String rol = el.getAttribute("rol");
            usuarios[cantidadUsuarios++] = new Usuario(id, nombre, pass, rol);
        }
    }
    public Usuario[] getUsuarios() { 
        return usuarios; 
    }
    public int getCantidadUsuarios() { 
        return cantidadUsuarios; 
    }
    public Usuario buscarUsuarioPorNombre(String nombre) {
        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].getNombre().equalsIgnoreCase(nombre)) return usuarios[i];
        }
        return null;
    }
    public int siguienteIdUsuario() {
        int max = 0;
        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].getId() > max) max = usuarios[i].getId();
        }
        return max + 1;
    }

    // Guarda un nuevo usuario (se añade al final del archivo)
    public void guardarUsuario(Usuario usuario) throws Exception {
        if (cantidadUsuarios >= MAX_USUARIOS)
            throw new IllegalStateException("Demasiados usuarios (máximo " + MAX_USUARIOS + ").");
        Document doc = crearParser().parse(archivoXml("usuarios.xml"));
        doc.getDocumentElement().normalize();
        Element raiz = (Element) doc.getElementsByTagName("usuarios").item(0);
        Element nuevo = doc.createElement("usuario");
        nuevo.setAttribute("id", String.valueOf(usuario.getId()));
        nuevo.setAttribute("rol", usuario.getRol());
        Element nom = doc.createElement("nombre");
        nom.setTextContent(usuario.getNombre());
        Element pass = doc.createElement("contrasena");
        pass.setTextContent(usuario.getContrasena());
        nuevo.appendChild(nom);
        nuevo.appendChild(pass);
        raiz.appendChild(nuevo);
        guardarDocumento(doc, "usuarios.xml");
        // También lo guardamos en memoria
        usuarios[cantidadUsuarios++] = usuario;
    }

    // Cambia la contraseña de un usuario
    public void cambiarContrasenaUsuario(String nombre, String nueva) throws Exception {
        Document doc = crearParser().parse(archivoXml("usuarios.xml"));
        doc.getDocumentElement().normalize();
        NodeList lista = doc.getElementsByTagName("usuario");
        boolean encontrado = false;
        for (int i = 0; i < lista.getLength() && !encontrado; i++) {
            Element el = (Element) lista.item(i);
            String nom = el.getElementsByTagName("nombre").item(0).getTextContent();
            if (nom.equalsIgnoreCase(nombre)) {
                el.getElementsByTagName("contrasena").item(0).setTextContent(nueva);
                encontrado = true;
            }
        }
        if (!encontrado) throw new IllegalArgumentException("Usuario '" + nombre + "' no existe.");
        guardarDocumento(doc, "usuarios.xml");
        // Actualizar en memoria
        Usuario u = buscarUsuarioPorNombre(nombre);
        if (u != null) u.setContrasena(nueva);
    }

    // ---------------------------- RUTAS ----------------------------

    private void cargarRutas() throws Exception {
        Document doc = crearParser().parse(archivoXml("rutas.xml"));
        doc.getDocumentElement().normalize();
        NodeList lista = doc.getElementsByTagName("ruta");
        cantidadRutas = 0;
        for (int i = 0; i < lista.getLength() && cantidadRutas < MAX_RUTAS; i++) {
            Element el = (Element) lista.item(i);
            int id = Integer.parseInt(el.getAttribute("id"));
            String origen = el.getElementsByTagName("origen").item(0).getTextContent();
            String destino = el.getElementsByTagName("destino").item(0).getTextContent();
            double duracion = Double.parseDouble(el.getElementsByTagName("duracionEstimada").item(0).getTextContent());
            double precio = Double.parseDouble(el.getElementsByTagName("precioBase").item(0).getTextContent());
            Ruta r = new Ruta(origen, destino, duracion, precio);
            r.setIdRuta(id);
            rutas[cantidadRutas++] = r;
        }
    }

    public Ruta[] getRutas() { 
        return rutas; 
    }
    public int getCantidadRutas() { 
        return cantidadRutas; 
    }
    public Ruta buscarRutaPorId(int id) {
        for (int i = 0; i < cantidadRutas; i++) {
            if (rutas[i].getIdRuta() == id) return rutas[i];
        }
        return null;
    }

    // Guarda una nueva ruta (se añade al final)
    public void guardarRuta(Ruta ruta) throws Exception {
        if (cantidadRutas >= MAX_RUTAS)
            throw new IllegalStateException("Demasiadas rutas (máximo " + MAX_RUTAS + ").");
        Document doc = crearParser().parse(archivoXml("rutas.xml"));
        doc.getDocumentElement().normalize();
        Element raiz = (Element) doc.getElementsByTagName("rutas").item(0);
        Element nuevo = doc.createElement("ruta");
        int nuevoId = doc.getElementsByTagName("ruta").getLength() + 1;
        nuevo.setAttribute("id", String.valueOf(nuevoId));
        Element orig = doc.createElement("origen");
        orig.setTextContent(ruta.getOrigen());
        Element dest = doc.createElement("destino");
        dest.setTextContent(ruta.getDestino());
        Element dur = doc.createElement("duracionEstimada");
        dur.setTextContent(String.valueOf(ruta.getDuracionEstimada()));
        Element prec = doc.createElement("precioBase");
        prec.setTextContent(String.valueOf(ruta.getPrecioBase()));
        nuevo.appendChild(orig);
        nuevo.appendChild(dest);
        nuevo.appendChild(dur);
        nuevo.appendChild(prec);
        raiz.appendChild(nuevo);
        ruta.setIdRuta(nuevoId);
        guardarDocumento(doc, "rutas.xml");
        rutas[cantidadRutas++] = ruta;
    }

    // ------------------------------ BUSES ------------------------------
    // Lee todos los buses desde buses.xml. Cada bus tiene su placa, capacidad y el mapa de asientos (libres/ocupados).
    public void cargarBuses() throws Exception {
        Document doc = crearParser().parse(archivoXml("buses.xml"));
        doc.getDocumentElement().normalize();
        NodeList lista = doc.getElementsByTagName("bus");
        cantidadBuses = 0;
        for (int i = 0; i < lista.getLength() && cantidadBuses < MAX_BUSES; i++) {
            Element el = (Element) lista.item(i);
            int id = Integer.parseInt(el.getAttribute("id"));
            String placa = el.getElementsByTagName("placa").item(0).getTextContent();
            int capacidad = Integer.parseInt(el.getElementsByTagName("capacidad").item(0).getTextContent());
            Bus bus = new Bus(id, placa, capacidad);
            // Leer el mapa de asientos
            Element asientosEl = (Element) el.getElementsByTagName("asientos").item(0);
            if (asientosEl != null) {
                char[][] mapa = new char[Bus.FILAS][Bus.COLUMNAS];
                // Por defecto todos libres
                for (char[] fila : mapa) Arrays.fill(fila, 'O');
                NodeList filas = asientosEl.getElementsByTagName("fila");
                for (int j = 0; j < filas.getLength(); j++) {
                    Element filaEl = (Element) filas.item(j);
                    int idx = Integer.parseInt(filaEl.getAttribute("index"));
                    String contenido = filaEl.getTextContent().trim();
                    if (idx < Bus.FILAS) {
                        for (int k = 0; k < contenido.length() && k < Bus.COLUMNAS; k++) {
                            mapa[idx][k] = contenido.charAt(k);
                        }
                    }
                }
                bus.setAsientos(mapa);
            }
            buses[cantidadBuses++] = bus;
        }
    }
    // Busca un bus por su ID.
    public Bus buscarBusPorId(int id) {
        for (int i = 0; i < cantidadBuses; i++) {
            if (buses[i].getIdBus() == id) return buses[i];
        }
        return null;
    }

    // Guarda TODOS los buses en buses.xml (sobrescribe el archivo completo). Se usa al cerrar el programa.
    public void guardarBuses() throws Exception {
        Document doc = crearDocumentoVacio("buses");
        Element raiz = doc.getDocumentElement();
        for (int i = 0; i < cantidadBuses; i++) {
            Bus b = buses[i];
            Element el = doc.createElement("bus");
            el.setAttribute("id", String.valueOf(b.getIdBus()));
            Element placa = doc.createElement("placa");
            placa.setTextContent(b.getPlaca());
            Element cap = doc.createElement("capacidad");
            cap.setTextContent(String.valueOf(b.getCapacidad()));
            el.appendChild(placa);
            el.appendChild(cap);
            // Guardar asientos
            Element asientosEl = doc.createElement("asientos");
            char[][] mapa = b.getAsientos();
            for (int f = 0; f < mapa.length; f++) {
                Element filaEl = doc.createElement("fila");
                filaEl.setAttribute("index", String.valueOf(f));
                filaEl.setTextContent(new String(mapa[f]));
                asientosEl.appendChild(filaEl);
            }
            el.appendChild(asientosEl);
            raiz.appendChild(el);
        }
        guardarDocumento(doc, "buses.xml");
    }

    // Guarda un bus individual (lo añade o actualiza en memoria y luego reescribe todo buses.xml para mantener el formato
    public void guardarBus(Bus bus) throws Exception {
        if (bus == null) return;
        // Ver si ya existe
        boolean existe = false;
        for (int i = 0; i < cantidadBuses; i++) {
            if (buses[i].getIdBus() == bus.getIdBus()) {
                buses[i] = bus;
                existe = true;
                break;
            }
        }
        if (!existe) {
            if (cantidadBuses < MAX_BUSES) {
                buses[cantidadBuses++] = bus;
            } else {
                throw new IllegalStateException("Límite de buses alcanzado (" + MAX_BUSES + ").");
            }
        }
        guardarBuses(); // Reescribe todo el archivo
    }

    /*
    Agrega un bus solo en memoria (sin guardar en disco)
    Útil cuando se crea un bus dentro de un viaje y después se persiste con guardarBus()
     */
    public boolean agregarBus(Bus bus) {
        if (cantidadBuses >= MAX_BUSES) return false;
        buses[cantidadBuses++] = bus;
        return true;
    }
    
public Bus[] getBuses() {
    return buses;
}

public int getCantidadBuses() {
    return cantidadBuses;
}

public int siguienteIdBus() {
    int max = 0;
    for (int i = 0; i < cantidadBuses; i++) {
        if (buses[i].getIdBus() > max) {
            max = buses[i].getIdBus();
        }
    }
    return max + 1;
}

    // --------------------------------- VIAJES (MATRIZ DE HORARIOS) ------------------------
    /*
    Lee los viajes desde viajes.xml.
    Ahora cada viaje solo guarda el ID del bus (no el bus completo).
    Si encuentra un bus incrustado (formato antiguo), lo convierte y lo añade a la lista de buses.
     */
    private void cargarViajes() throws Exception {
        Document doc = crearParser().parse(archivoXml("viajes.xml"));
        doc.getDocumentElement().normalize();
        viajes = new Viaje[MAX_DESTINOS][MAX_HORARIOS];
        NodeList lista = doc.getElementsByTagName("viaje");
        for (int i = 0; i < lista.getLength(); i++) {
            Element el = (Element) lista.item(i);
            int fila = Integer.parseInt(el.getAttribute("fila"));
            int col = Integer.parseInt(el.getAttribute("columna"));
            if (fila < 0 || fila >= MAX_DESTINOS || col < 0 || col >= MAX_HORARIOS) {
                System.out.println("⚠️ Viaje con ID " + el.getAttribute("id") + " está fuera de la matriz y se omite.");
                continue;
            }
            Viaje v = new Viaje();
            v.setIdViaje(Integer.parseInt(el.getAttribute("id")));
            v.setFecha(el.getElementsByTagName("fecha").item(0).getTextContent());
            v.setHora(el.getElementsByTagName("hora").item(0).getTextContent());
            int idRuta = Integer.parseInt(el.getElementsByTagName("idRuta").item(0).getTextContent());
            v.setRuta(buscarRutaPorId(idRuta));
            // Buscar referencia al bus (nuevo formato)
            Element idBusEl = (Element) el.getElementsByTagName("idBus").item(0);
            if (idBusEl != null) {
                int idBus = Integer.parseInt(idBusEl.getTextContent());
                v.setBus(buscarBusPorId(idBus));
            } else {
                // Formato antiguo: bus incrustado
                Element busEl = (Element) el.getElementsByTagName("bus").item(0);
                if (busEl != null) {
                    Bus bus = parseBus(busEl);
                    // Si no existe en la lista, lo agregamos
                    if (buscarBusPorId(bus.getIdBus()) == null) {
                        if (cantidadBuses < MAX_BUSES) {
                            buses[cantidadBuses++] = bus;
                        } else {
                            System.out.println("⚠️ No se pudo agregar bus " + bus.getIdBus() + " (límite alcanzado)");
                        }
                    }
                    v.setBus(bus);
                }
            }
            viajes[fila][col] = v;
        }
    }

    // Convierte un elemento XML <bus> en un objeto Bus (para compatibilidad con formato antiguo)
    private Bus parseBus(Element busEl) {
        int id = Integer.parseInt(busEl.getElementsByTagName("id").item(0).getTextContent());
        String placa = busEl.getElementsByTagName("placa").item(0).getTextContent();
        int cap = Integer.parseInt(busEl.getElementsByTagName("capacidad").item(0).getTextContent());
        Bus bus = new Bus(id, placa, cap);
        Element asientosEl = (Element) busEl.getElementsByTagName("asientos").item(0);
        if (asientosEl != null) {
            char[][] mapa = new char[Bus.FILAS][Bus.COLUMNAS];
            for (char[] f : mapa) Arrays.fill(f, 'O');
            NodeList filas = asientosEl.getElementsByTagName("fila");
            for (int i = 0; i < filas.getLength(); i++) {
                Element filaEl = (Element) filas.item(i);
                int idx = Integer.parseInt(filaEl.getAttribute("index"));
                String vals = filaEl.getTextContent().trim();
                if (idx < Bus.FILAS) {
                    for (int j = 0; j < vals.length() && j < Bus.COLUMNAS; j++) {
                        mapa[idx][j] = vals.charAt(j);
                    }
                }
            }
            bus.setAsientos(mapa);
        }
        return bus;
    }

    public Viaje[][] getViajes() { return viajes; }
    // Devuelve todos los viajes que van a un destino específico
    public Viaje[] buscarViajesPorDestino(String destino) {
        Viaje[] resultado = new Viaje[MAX_HORARIOS];
        int encontrados = 0;
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            String dest = obtenerDestinoDeFila(fila);
            if (dest != null && dest.equalsIgnoreCase(destino)) {
                for (int col = 0; col < MAX_HORARIOS; col++) {
                    if (viajes[fila][col] != null) {
                        resultado[encontrados++] = viajes[fila][col];
                    }
                }
                break;
            }
        }
        return Arrays.copyOf(resultado, encontrados);
    }

    private String obtenerDestinoDeFila(int fila) {
        for (int col = 0; col < MAX_HORARIOS; col++) {
            if (viajes[fila][col] != null && viajes[fila][col].getRuta() != null) {
                return viajes[fila][col].getRuta().getDestino();
            }
        }
        return null;
    }

    // Busca una fila libre para un nuevo destino, o devuelve la fila existente si ya está
    public int obtenerOCrearFilaParaDestino(String destino) {
        for (int f = 0; f < MAX_DESTINOS; f++) {
            String d = obtenerDestinoDeFila(f);
            if (d != null && d.equalsIgnoreCase(destino)) return f;
        }
        for (int f = 0; f < MAX_DESTINOS; f++) {
            if (obtenerDestinoDeFila(f) == null) return f;
        }
        return -1; // No hay espacio
    }
    // Devuelve la primera columna libre en una fila
    public int primeraColumnaLibre(int fila) {
        if (fila < 0 || fila >= MAX_DESTINOS) return -1;
        for (int col = 0; col < MAX_HORARIOS; col++) {
            if (viajes[fila][col] == null) return col;
        }
        return -1;
    }

    public int siguienteIdViaje() {
        int max = 0;
        for (int f = 0; f < MAX_DESTINOS; f++) {
            for (int c = 0; c < MAX_HORARIOS; c++) {
                if (viajes[f][c] != null && viajes[f][c].getIdViaje() > max) {
                    max = viajes[f][c].getIdViaje();
                }
            }
        }
        return max + 1;
    }

    // Añade un viaje en una posición concreta de la matriz
    public boolean agregarViaje(int fila, int columna, Viaje viaje) {
        if (fila < 0 || fila >= MAX_DESTINOS || columna < 0 || columna >= MAX_HORARIOS) return false;
        if (viajes[fila][columna] != null) return false;
        viajes[fila][columna] = viaje;
        return true;
    }

    public Viaje[] getTodosLosViajes() {
        Viaje[] resultado = new Viaje[MAX_DESTINOS * MAX_HORARIOS];
        int i = 0;
        for (int f = 0; f < MAX_DESTINOS; f++) {
            for (int c = 0; c < MAX_HORARIOS; c++) {
                if (viajes[f][c] != null) resultado[i++] = viajes[f][c];
            }
        }
        return Arrays.copyOf(resultado, i);
    }

    public static int getMaxDestinos() { return MAX_DESTINOS; }
    public static int getMaxHorarios() { return MAX_HORARIOS; }

    public Viaje buscarViajePorId(int id) {
        for (int f = 0; f < MAX_DESTINOS; f++) {
            for (int c = 0; c < MAX_HORARIOS; c++) {
                if (viajes[f][c] != null && viajes[f][c].getIdViaje() == id) return viajes[f][c];
            }
        }
        return null;
    }

    // Guarda todos los viajes en viajes.xml. Ahora solo guarda el ID del bus, no el bus completo
    public void guardarViajes() throws Exception {
        Document doc = crearDocumentoVacio("viajes");
        Element raiz = doc.getDocumentElement();
        for (int f = 0; f < MAX_DESTINOS; f++) {
            for (int c = 0; c < MAX_HORARIOS; c++) {
                Viaje v = viajes[f][c];
                if (v == null) continue;
                Element el = doc.createElement("viaje");
                el.setAttribute("id", String.valueOf(v.getIdViaje()));
                el.setAttribute("fila", String.valueOf(f));
                el.setAttribute("columna", String.valueOf(c));
                Element fecha = doc.createElement("fecha");
                fecha.setTextContent(v.getFecha());
                Element hora = doc.createElement("hora");
                hora.setTextContent(v.getHora());
                Element idRuta = doc.createElement("idRuta");
                idRuta.setTextContent(String.valueOf(v.getRuta() != null ? v.getRuta().getIdRuta() : -1));
                Element idBus = doc.createElement("idBus");
                idBus.setTextContent(String.valueOf(v.getBus() != null ? v.getBus().getIdBus() : -1));
                el.appendChild(fecha);
                el.appendChild(hora);
                el.appendChild(idRuta);
                el.appendChild(idBus);
                raiz.appendChild(el);
            }
        }
        guardarDocumento(doc, "viajes.xml");
    }

    // ------------------------------------- VENTAS -------------------------------------
    private void cargarVentas() throws Exception {
        Document doc = crearParser().parse(archivoXml("ventas.xml"));
        doc.getDocumentElement().normalize();
        NodeList lista = doc.getElementsByTagName("venta");
        cantidadVentas = 0;
        for (int i = 0; i < lista.getLength() && cantidadVentas < MAX_VENTAS; i++) {
            Element el = (Element) lista.item(i);
            Venta v = new Venta();
            v.setIdVenta(Integer.parseInt(el.getAttribute("id")));
            v.setFecha(el.getElementsByTagName("fecha").item(0).getTextContent());
            v.setPrecioFinal(Double.parseDouble(el.getElementsByTagName("precioFinal").item(0).getTextContent()));
            int idViaje = Integer.parseInt(el.getElementsByTagName("idViaje").item(0).getTextContent());
            v.setViaje(buscarViajePorId(idViaje));
            Element p = (Element) el.getElementsByTagName("pasajero").item(0);
            if (p != null) {
                String dni = p.getElementsByTagName("dni").item(0).getTextContent();
                String nombre = p.getElementsByTagName("nombre").item(0).getTextContent();
                int edad = Integer.parseInt(p.getElementsByTagName("edad").item(0).getTextContent());
                v.setPasajero(new Pasajero(dni, nombre, edad));
            }
            ventas[cantidadVentas++] = v;
        }
    }

    public boolean registrarVenta(Venta venta) {
        if (venta == null || cantidadVentas >= MAX_VENTAS) return false;
        ventas[cantidadVentas++] = venta;
        return true;
    }

    public Venta[] getVentas() { return ventas; }
    public int getCantidadVentas() { return cantidadVentas; }
    public Venta[] getVentasRegistradas() { return Arrays.copyOf(ventas, cantidadVentas); }

    public void guardarVentas() throws Exception {
        Document doc = crearDocumentoVacio("ventas");
        Element raiz = doc.getDocumentElement();
        for (int i = 0; i < cantidadVentas; i++) {
            Venta v = ventas[i];
            Element el = doc.createElement("venta");
            el.setAttribute("id", String.valueOf(v.getIdVenta()));
            Element fecha = doc.createElement("fecha");
            fecha.setTextContent(v.getFecha());
            Element precio = doc.createElement("precioFinal");
            precio.setTextContent(String.valueOf(v.getPrecioFinal()));
            Element idViaje = doc.createElement("idViaje");
            idViaje.setTextContent(String.valueOf(v.getViaje() != null ? v.getViaje().getIdViaje() : -1));
            el.appendChild(fecha);
            el.appendChild(precio);
            el.appendChild(idViaje);
            if (v.getPasajero() != null) {
                Element p = doc.createElement("pasajero");
                Element dni = doc.createElement("dni");
                dni.setTextContent(v.getPasajero().getDni());
                Element nombre = doc.createElement("nombre");
                nombre.setTextContent(v.getPasajero().getNombre());
                Element edad = doc.createElement("edad");
                edad.setTextContent(String.valueOf(v.getPasajero().getEdad()));
                p.appendChild(dni);
                p.appendChild(nombre);
                p.appendChild(edad);
                el.appendChild(p);
            }
            raiz.appendChild(el);
        }
        guardarDocumento(doc, "ventas.xml");
    }

    // GUARDAR TODO AL CERRAR

    public void guardarTodos() throws Exception {
        guardarUsuarios();
        guardarRutas();
        guardarBuses();
        guardarViajes();
        guardarVentas();
    }

    //  MÉTODOS AUXILIARES 

    private Document crearDocumentoVacio(String raiz) throws Exception {
        Document doc = crearParser().newDocument();
        doc.appendChild(doc.createElement(raiz));
        return doc;
    }

    private void guardarUsuarios() throws Exception {
        Document doc = crearDocumentoVacio("usuarios");
        Element raiz = doc.getDocumentElement();
        for (int i = 0; i < cantidadUsuarios; i++) {
            Usuario u = usuarios[i];
            Element el = doc.createElement("usuario");
            el.setAttribute("id", String.valueOf(u.getId()));
            el.setAttribute("rol", u.getRol());
            Element nom = doc.createElement("nombre");
            nom.setTextContent(u.getNombre());
            Element pass = doc.createElement("contrasena");
            pass.setTextContent(u.getContrasena());
            el.appendChild(nom);
            el.appendChild(pass);
            raiz.appendChild(el);
        }
        guardarDocumento(doc, "usuarios.xml");
    }

    private void guardarRutas() throws Exception {
        Document doc = crearDocumentoVacio("rutas");
        Element raiz = doc.getDocumentElement();
        for (int i = 0; i < cantidadRutas; i++) {
            Ruta r = rutas[i];
            Element el = doc.createElement("ruta");
            el.setAttribute("id", String.valueOf(r.getIdRuta()));
            Element orig = doc.createElement("origen");
            orig.setTextContent(r.getOrigen());
            Element dest = doc.createElement("destino");
            dest.setTextContent(r.getDestino());
            Element dur = doc.createElement("duracionEstimada");
            dur.setTextContent(String.valueOf(r.getDuracionEstimada()));
            Element prec = doc.createElement("precioBase");
            prec.setTextContent(String.valueOf(r.getPrecioBase()));
            el.appendChild(orig);
            el.appendChild(dest);
            el.appendChild(dur);
            el.appendChild(prec);
            raiz.appendChild(el);
        }
        guardarDocumento(doc, "rutas.xml");
    }
}