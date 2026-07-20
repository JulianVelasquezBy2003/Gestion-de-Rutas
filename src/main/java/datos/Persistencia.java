package datos;

import java.io.File;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import modelo.Usuario;
import transporte.Bus;
import transporte.Pasajero;
import transporte.Ruta;
import transporte.Venta;
import transporte.Viaje;

/*
 Gestiona la persistencia de datos del sistema en archivos XML.
 - Cargar datos desde XML al iniciar (usuarios, rutas, viajes, ventas)
 - Guardar cambios realizados durante la sesión
 - Mantener arreglos en memoria con límites de tamaño fijo
 - Reconstruir relaciones entre entidades (viajes con rutas, ventas con viajes)

Estructura de datos:
 - Usuario[]: arreglo plano (1D) con máximo 100 usuarios
 - Ruta[]: arreglo plano (1D) con máximo 50 rutas
 - Viaje[][]: matriz 2D (destinos x horarios) de 10x5
 - Venta[]: arreglo plano (1D) con máximo 500 ventas
 @author Julian, Angela
 */
public class Persistencia {
    //Ruta de archivos XML
    private static final String CARPETA_XML = "src/main/resources/xml/";
    
    // Límites de tamaño para arreglos de tamaño fijo
    private static final int MAX_USUARIOS = 100;
    private static final int MAX_RUTAS = 50;
    private static final int MAX_DESTINOS = 10;      // Filas de la matriz de viajes
    private static final int MAX_HORARIOS = 5;       // Columnas de la matriz de viajes
    private static final int MAX_VENTAS = 500;

    // Almacena todos los usuarios del sistema 
    private Usuario[] usuarios = new Usuario[MAX_USUARIOS];
    private int cantidadUsuarios = 0;

    // Almacena todas las rutas disponibles 
    private Ruta[] rutas = new Ruta[MAX_RUTAS];
    private int cantidadRutas = 0;

    /*
     Matriz de viajes: filas representan destinos, columnas representan franjas horarias.
     Estructura: viajes[destino][horario] = Viaje
     */
    private Viaje[][] viajes = new Viaje[MAX_DESTINOS][MAX_HORARIOS];

    // Almacena el historial de ventas de la sesión
    private Venta[] ventas = new Venta[MAX_VENTAS];
    private int cantidadVentas = 0;

    // Obtiene la ruta de un archivo XML desde la carpeta de recursos.
    private File archivoXml(String nombreArchivo) {
        return new File(CARPETA_XML + nombreArchivo);
    }

    // Crea un parser XML (DocumentBuilder) para leer/escribir documentos XML.
    private DocumentBuilder crearParserXml() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder();
    }

    // Escribe un documento XML a disco con indentación automática.
    private void escribirDocumento(Document documento, String rutaArchivo) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(documento);
        StreamResult result = new StreamResult(new File(rutaArchivo));
        transformer.transform(source, result);
    }

    /*
     Limpia recursivamente los nodos de texto que solo contienen espacios/saltos de línea.
     Evita que se duplique la indentación al reescribir archivos XML existentes.
     */
    private void limpiarEspaciosEnBlanco(Node nodo) {
        NodeList hijos = nodo.getChildNodes();
        for (int i = hijos.getLength() - 1; i >= 0; i--) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.TEXT_NODE && hijo.getTextContent().trim().isEmpty()) {
                nodo.removeChild(hijo);
            } else if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                limpiarEspaciosEnBlanco(hijo);
            }
        }
    }

    // USUARIOS
    // Carga todos los usuarios desde usuarios.xml en memoria.
    public void cargarXML() throws Exception {
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.parse(archivoXml("usuarios.xml"));
        documento.getDocumentElement().normalize();
        NodeList listaUsuarios = documento.getElementsByTagName("usuario");
        cantidadUsuarios = 0;
        for (int i = 0; i < listaUsuarios.getLength() && cantidadUsuarios < MAX_USUARIOS; i++) {
            Element usuarioEl = (Element) listaUsuarios.item(i);
            // Extrae atributos e hijos del elemento usuario
            int id = Integer.parseInt(usuarioEl.getAttribute("id"));
            String nombre = usuarioEl.getElementsByTagName("nombre").item(0).getTextContent();
            String contrasena = usuarioEl.getElementsByTagName("contrasena").item(0).getTextContent();
            String rol = usuarioEl.getAttribute("rol");
            usuarios[cantidadUsuarios] = new Usuario(id, nombre, contrasena, rol);
            cantidadUsuarios++;
        }
    }

    public Usuario[] getUsuarios() {
        return usuarios;
    }

    public int getCantidadUsuarios() {
        return cantidadUsuarios;
    }

    public static int getMaxUsuarios() {
        return MAX_USUARIOS;
    }

    // Busca un usuario por su nombre (sin distinguir mayúsculas/minúsculas).
    public Usuario buscarUsuarioPorNombre(String nombre) {
        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].getNombre().equalsIgnoreCase(nombre)) {
                return usuarios[i];
            }
        }
        return null;
    }

    // Verifica si un usuario con ese nombre ya existe en el sistema.
    public boolean existeUsuario(String nombre) {
        return buscarUsuarioPorNombre(nombre) != null;
    }

    /* 
    Calcula el siguiente ID disponible para un nuevo usuario.
    Recorre todos los usuarios cargados y retorna maxId + 1.
     */
    public int siguienteIdUsuario() {
        int maxId = 0;
        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].getId() > maxId) {
                maxId = usuarios[i].getId();
            }
        }
        return maxId + 1;
    }

    /*
     Guarda un nuevo usuario en usuarios.xml y en memoria.
     Se agrega al final del archivo, manteniendo el formato XML existente.
     */
    public void guardarUsuario(Usuario usuario) throws Exception {
        if (cantidadUsuarios >= MAX_USUARIOS) {
            throw new IllegalStateException("Se alcanzó el máximo de usuarios permitidos (" + MAX_USUARIOS + ").");
        }
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.parse(archivoXml("usuarios.xml"));
        documento.getDocumentElement().normalize();
        limpiarEspaciosEnBlanco(documento.getDocumentElement());
        Element usuariosEl = (Element) documento.getElementsByTagName("usuarios").item(0);
        Element nuevoUsuario = documento.createElement("usuario");
        nuevoUsuario.setAttribute("id", String.valueOf(usuario.getId()));
        nuevoUsuario.setAttribute("rol", usuario.getRol());
        Element nombre = documento.createElement("nombre");
        nombre.setTextContent(usuario.getNombre());
        Element contrasena = documento.createElement("contrasena");
        contrasena.setTextContent(usuario.getContrasena());
        nuevoUsuario.appendChild(nombre);
        nuevoUsuario.appendChild(contrasena);
        usuariosEl.appendChild(nuevoUsuario);
        escribirDocumento(documento, CARPETA_XML + "usuarios.xml");
        // También agregar en memoria para que sea inmediato
        usuarios[cantidadUsuarios] = usuario;
        cantidadUsuarios++;
    }

    /*
     Cambia la contraseña de un usuario existente.
     Actualiza tanto el archivo XML como la referencia en memoria.
     */
    public void cambiarContrasenaUsuario(String nombre, String nuevaContrasena) throws Exception {
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.parse(archivoXml("usuarios.xml"));
        documento.getDocumentElement().normalize();
        limpiarEspaciosEnBlanco(documento.getDocumentElement());
        NodeList listaUsuarios = documento.getElementsByTagName("usuario");
        boolean actualizado = false;
        for (int i = 0; i < listaUsuarios.getLength() && !actualizado; i++) {
            Element usuarioEl = (Element) listaUsuarios.item(i);
            String nombreXml = usuarioEl.getElementsByTagName("nombre").item(0).getTextContent();
            if (nombreXml.equalsIgnoreCase(nombre)) {
                usuarioEl.getElementsByTagName("contrasena").item(0).setTextContent(nuevaContrasena);
                actualizado = true;
            }
        }
        if (!actualizado) {
            throw new IllegalArgumentException("No existe un usuario registrado con el nombre \"" + nombre + "\".");
        }
        escribirDocumento(documento, CARPETA_XML + "usuarios.xml");
        // Actualizar también en memoria
        Usuario usuarioEnMemoria = buscarUsuarioPorNombre(nombre);
        if (usuarioEnMemoria != null) {
            usuarioEnMemoria.setContrasena(nuevaContrasena);
        }
    }

    // RUTAS
    /*
     Carga todas las rutas desde rutas.xml en memoria.
     Es recomendable cargar rutas antes de cargar viajes (para poder enlazarlas).
     */
    public void cargarRutas() throws Exception {
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.parse(archivoXml("rutas.xml"));
        documento.getDocumentElement().normalize();
        NodeList listaRutas = documento.getElementsByTagName("ruta");
        cantidadRutas = 0;
        for (int i = 0; i < listaRutas.getLength() && cantidadRutas < MAX_RUTAS; i++) {
            Element rutaEl = (Element) listaRutas.item(i);
            int id = Integer.parseInt(rutaEl.getAttribute("id"));
            String origen = rutaEl.getElementsByTagName("origen").item(0).getTextContent();
            String destino = rutaEl.getElementsByTagName("destino").item(0).getTextContent();
            double duracion = Double.parseDouble(rutaEl.getElementsByTagName("duracionEstimada").item(0).getTextContent());
            double precio = Double.parseDouble(rutaEl.getElementsByTagName("precioBase").item(0).getTextContent());
            Ruta ruta = new Ruta(origen, destino, duracion, precio);
            ruta.setIdRuta(id);
            rutas[cantidadRutas] = ruta;
            cantidadRutas++;
        }
    }

    public Ruta[] getRutas() {
        return rutas;
    }

    public int getCantidadRutas() {
        return cantidadRutas;
    }

    //Busca una ruta por su ID.
    public Ruta buscarRutaPorId(int id) {
        for (int i = 0; i < cantidadRutas; i++) {
            if (rutas[i].getIdRuta() == id) {
                return rutas[i];
            }
        }
        return null;
    }

    /*
    Guarda una nueva ruta en rutas.xml.
    Se asigna automáticamente el siguiente ID.
     */
    public void guardarRuta(Ruta ruta) throws Exception {
        if (cantidadRutas >= MAX_RUTAS) {
            throw new IllegalStateException("Se alcanzó el máximo de rutas permitidas (" + MAX_RUTAS + ").");
        }
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.parse(archivoXml("rutas.xml"));
        documento.getDocumentElement().normalize();
        limpiarEspaciosEnBlanco(documento.getDocumentElement());
        Element rutasEl = (Element) documento.getElementsByTagName("rutas").item(0);
        Element nuevaRuta = documento.createElement("ruta");
        int nuevoId = documento.getElementsByTagName("ruta").getLength() + 1;
        nuevaRuta.setAttribute("id", String.valueOf(nuevoId));
        Element origen = documento.createElement("origen");
        origen.setTextContent(ruta.getOrigen());
        Element destino = documento.createElement("destino");
        destino.setTextContent(ruta.getDestino());
        Element duracion = documento.createElement("duracionEstimada");
        duracion.setTextContent(String.valueOf(ruta.getDuracionEstimada()));
        Element precio = documento.createElement("precioBase");
        precio.setTextContent(String.valueOf(ruta.getPrecioBase()));
        nuevaRuta.appendChild(origen);
        nuevaRuta.appendChild(destino);
        nuevaRuta.appendChild(duracion);
        nuevaRuta.appendChild(precio);
        rutasEl.appendChild(nuevaRuta);
        ruta.setIdRuta(nuevoId);
        escribirDocumento(documento, CARPETA_XML + "rutas.xml");
        // Agregar también en memoria
        rutas[cantidadRutas] = ruta;
        cantidadRutas++;
    }

    // VIAJES (MATRIZ DE HORARIOS)
    
    /*
     Carga la matriz de viajes (Destinos x Horarios) desde viajes.xml.
     Estructura:
     - Cada viaje indica su posición en la matriz mediante atributos fila/columna
     - Cada viaje contiene una referencia a una Ruta (por idRuta)
     - Cada viaje contiene un Bus con su mapa de asientos
     */
    public void cargarViajes() throws Exception {
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.parse(archivoXml("viajes.xml"));
        documento.getDocumentElement().normalize();
        viajes = new Viaje[MAX_DESTINOS][MAX_HORARIOS];
        NodeList listaViajes = documento.getElementsByTagName("viaje");
        for (int i = 0; i < listaViajes.getLength(); i++) {
            Element viajeEl = (Element) listaViajes.item(i);
            int fila = Integer.parseInt(viajeEl.getAttribute("fila"));
            int columna = Integer.parseInt(viajeEl.getAttribute("columna"));
            // Validar que la posición está dentro de los límites
            if (fila < 0 || fila >= MAX_DESTINOS || columna < 0 || columna >= MAX_HORARIOS) {
                System.out.println("⚠️ El viaje id=" + viajeEl.getAttribute("id") + 
                    " tiene una posición fuera del tamaño de la matriz y fue omitido.");
                continue;
            }
            Viaje viaje = new Viaje();
            viaje.setIdViaje(Integer.parseInt(viajeEl.getAttribute("id")));
            viaje.setFecha(viajeEl.getElementsByTagName("fecha").item(0).getTextContent());
            viaje.setHora(viajeEl.getElementsByTagName("hora").item(0).getTextContent());
            int idRuta = Integer.parseInt(
                viajeEl.getElementsByTagName("idRuta").item(0).getTextContent());
            viaje.setRuta(buscarRutaPorId(idRuta));
            Element busEl = (Element) viajeEl.getElementsByTagName("bus").item(0);
            if (busEl != null) {
                viaje.setBus(parseBus(busEl));
            }
            viajes[fila][columna] = viaje;
        }
    }

    /*
    Construye un objeto Bus a partir de un elemento XML.
    Incluye la reconstrucción del mapa de asientos desde el XML.
     */
    private Bus parseBus(Element busEl) {
        int idBus = Integer.parseInt(busEl.getElementsByTagName("id").item(0).getTextContent());
        String placa = busEl.getElementsByTagName("placa").item(0).getTextContent();
        int capacidad = Integer.parseInt(busEl.getElementsByTagName("capacidad").item(0).getTextContent());
        Bus bus = new Bus(idBus, placa, capacidad);
        Element asientosEl = (Element) busEl.getElementsByTagName("asientos").item(0);
        if (asientosEl != null) {
            // Inicializar mapa vacío (todos libres)
            char[][] mapa = new char[Bus.FILAS][Bus.COLUMNAS];
            for (char[] filaArr : mapa) {
                Arrays.fill(filaArr, 'O');
            }
            // Cargar estado de asientos desde XML
            NodeList filasXml = asientosEl.getElementsByTagName("fila");
            for (int i = 0; i < filasXml.getLength(); i++) {
                Element filaEl = (Element) filasXml.item(i);
                int index = Integer.parseInt(filaEl.getAttribute("index"));
                String valores = filaEl.getTextContent().trim();

                if (index >= 0 && index < Bus.FILAS) {
                    for (int col = 0; col < valores.length() && col < Bus.COLUMNAS; col++) {
                        mapa[index][col] = valores.charAt(col);
                    }
                }
            }
            bus.setAsientos(mapa);
        }
        return bus;
    }

    public Viaje[][] getViajes() {
        return viajes;
    }

    /*
     Busca todos los viajes programados hacia un destino específico.
     Recorre la matriz de viajes buscando la fila que corresponde al destino
     y devuelve todos los viajes (frecuencias horarias) en esa fila.
     */
    public Viaje[] buscarViajesPorDestino(String destino) {
        Viaje[] resultado = new Viaje[MAX_HORARIOS];
        int encontrados = 0;

        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            String destinoDeLaFila = obtenerDestinoDeFila(fila);
            if (destinoDeLaFila != null && destinoDeLaFila.equalsIgnoreCase(destino)) {
                for (int columna = 0; columna < MAX_HORARIOS; columna++) {
                    if (viajes[fila][columna] != null) {
                        resultado[encontrados] = viajes[fila][columna];
                        encontrados++;
                    }
                }
                break;
            }
        }
        return Arrays.copyOf(resultado, encontrados);
    }

    /*
     Obtiene el nombre del destino asociado a una fila de la matriz.
     Lo toma del primer viaje no nulo que encuentre en esa fila.
     */
    private String obtenerDestinoDeFila(int fila) {
        for (int columna = 0; columna < MAX_HORARIOS; columna++) {
            if (viajes[fila][columna] != null && viajes[fila][columna].getRuta() != null) {
                return viajes[fila][columna].getRuta().getDestino();
            }
        }
        return null;
    }

    /*
    Obtiene o crea la fila de la matriz para un destino específico.
     Si el destino ya tiene viajes registrados, retorna su fila.
     Si es nuevo, asigna la primera fila disponible.
     Este método es fundamental para "Agregar Horario":
     - Un destino nuevo = una fila nueva
     - Un horario nuevo para un destino existente = una columna nueva en esa fila
     */
    public int obtenerOCrearFilaParaDestino(String destino) {
        // Buscar fila existente para este destino
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            String destinoDeLaFila = obtenerDestinoDeFila(fila);
            if (destinoDeLaFila != null && destinoDeLaFila.equalsIgnoreCase(destino)) {
                return fila;
            }
        }
        
        // No existe, buscar primera fila disponible
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            if (obtenerDestinoDeFila(fila) == null) {
                return fila;
            }
        }
        
        return -1; // No hay filas disponibles
    }

    // Busca la primera columna (franja horaria) libre en una fila específica.
    public int primeraColumnaLibre(int fila) {
        if (fila < 0 || fila >= MAX_DESTINOS) {
            return -1;
        }
        for (int columna = 0; columna < MAX_HORARIOS; columna++) {
            if (viajes[fila][columna] == null) {
                return columna;
            }
        }
        return -1;
    }

    /**
     * Calcula el siguiente ID disponible para un nuevo viaje.
     * Recorre toda la matriz buscando el ID más alto.
     * 
     * @return próximo ID disponible
     */
    public int siguienteIdViaje() {
        int maxId = 0;
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            for (int columna = 0; columna < MAX_HORARIOS; columna++) {
                if (viajes[fila][columna] != null && 
                    viajes[fila][columna].getIdViaje() > maxId) {
                    maxId = viajes[fila][columna].getIdViaje();
                }
            }
        }
        return maxId + 1;
    }

    /**
     * Inserta un viaje en una posición específica de la matriz.
     * Valida que la posición exista y esté libre.
     * 
     * @param fila índice de fila
     * @param columna índice de columna
     * @param viaje Viaje a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    public boolean agregarViaje(int fila, int columna, Viaje viaje) {
        if (fila < 0 || fila >= MAX_DESTINOS || columna < 0 || columna >= MAX_HORARIOS) {
            return false;
        }
        if (viajes[fila][columna] != null) {
            return false;
        }
        viajes[fila][columna] = viaje;
        return true;
    }

    /**
     * Obtiene todos los viajes de la matriz en un solo arreglo.
     * Usado para reportes de ocupación.
     * 
     * @return arreglo de Viaje sin espacios vacíos
     */
    public Viaje[] getTodosLosViajes() {
        Viaje[] resultado = new Viaje[MAX_DESTINOS * MAX_HORARIOS];
        int encontrados = 0;
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            for (int columna = 0; columna < MAX_HORARIOS; columna++) {
                if (viajes[fila][columna] != null) {
                    resultado[encontrados] = viajes[fila][columna];
                    encontrados++;
                }
            }
        }
        return Arrays.copyOf(resultado, encontrados);
    }

    public static int getMaxDestinos() {
        return MAX_DESTINOS;
    }

    public static int getMaxHorarios() {
        return MAX_HORARIOS;
    }

    /**
     * Busca un viaje dentro de la matriz por su ID.
     * Usado para reconstruir la relación entre Venta y Viaje al cargar ventas.
     * 
     * @param idViaje ID del viaje a buscar
     * @return el Viaje encontrado, o null si no existe
     */
    public Viaje buscarViajePorId(int idViaje) {
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            for (int columna = 0; columna < MAX_HORARIOS; columna++) {
                if (viajes[fila][columna] != null && 
                    viajes[fila][columna].getIdViaje() == idViaje) {
                    return viajes[fila][columna];
                }
            }
        }
        return null;
    }

    /**
     * Guarda la matriz completa de viajes en viajes.xml.
     * Incluye el mapa actualizado de asientos de cada bus.
     * Los asientos ocupados durante la sesión se persisten.
     * 
     * @throws Exception si hay error de E/S
     */
    public void guardarViajes() throws Exception {
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.newDocument();
        Element root = documento.createElement("root");
        documento.appendChild(root);
        Element viajesEl = documento.createElement("viajes");
        root.appendChild(viajesEl);

        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            for (int columna = 0; columna < MAX_HORARIOS; columna++) {
                Viaje viaje = viajes[fila][columna];
                if (viaje == null) {
                    continue;
                }

                Element viajeEl = documento.createElement("viaje");
                viajeEl.setAttribute("id", String.valueOf(viaje.getIdViaje()));
                viajeEl.setAttribute("fila", String.valueOf(fila));
                viajeEl.setAttribute("columna", String.valueOf(columna));
                
                Element fecha = documento.createElement("fecha");
                fecha.setTextContent(viaje.getFecha());
                Element hora = documento.createElement("hora");
                hora.setTextContent(viaje.getHora());
                Element idRuta = documento.createElement("idRuta");
                idRuta.setTextContent(String.valueOf(
                    viaje.getRuta() != null ? viaje.getRuta().getIdRuta() : -1));
                
                viajeEl.appendChild(fecha);
                viajeEl.appendChild(hora);
                viajeEl.appendChild(idRuta);

                if (viaje.getBus() != null) {
                    viajeEl.appendChild(crearElementoBus(documento, viaje.getBus()));
                }
                viajesEl.appendChild(viajeEl);
            }
        }
        escribirDocumento(documento, CARPETA_XML + "viajes.xml");
    }

    /**
     * Construye un elemento XML Bus para guardarlo dentro de un viaje.
     * Incluye el mapa de asientos actual.
     * 
     * @param documento Document XML
     * @param bus Bus a serializar
     * @return elemento XML con los datos del bus
     */
    private Element crearElementoBus(Document documento, Bus bus) {
        Element busEl = documento.createElement("bus");
        
        Element id = documento.createElement("id");
        id.setTextContent(String.valueOf(bus.getIdBus()));
        Element placa = documento.createElement("placa");
        placa.setTextContent(bus.getPlaca());
        Element capacidad = documento.createElement("capacidad");
        capacidad.setTextContent(String.valueOf(bus.getCapacidad()));
        
        busEl.appendChild(id);
        busEl.appendChild(placa);
        busEl.appendChild(capacidad);
        
        Element asientosEl = documento.createElement("asientos");
        char[][] mapa = bus.getAsientos();
        for (int i = 0; i < mapa.length; i++) {
            Element filaEl = documento.createElement("fila");
            filaEl.setAttribute("index", String.valueOf(i));
            filaEl.setTextContent(new String(mapa[i]));
            asientosEl.appendChild(filaEl);
        }
        busEl.appendChild(asientosEl);
        
        return busEl;
    }

    // ==================== VENTAS ====================
    
    /**
     * Carga el historial de ventas desde ventas.xml.
     * Reconstruye las relaciones con sus Viajes y Pasajeros.
     * 
     * Prerequisito: cargarViajes() debe ejecutarse ANTES para poder enlazar viajes.
     * 
     * @throws Exception si el archivo no puede leerse
     */
    public void cargarVentas() throws Exception {
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.parse(archivoXml("ventas.xml"));
        documento.getDocumentElement().normalize();
        
        NodeList listaVentas = documento.getElementsByTagName("venta");
        cantidadVentas = 0;
        
        for (int i = 0; i < listaVentas.getLength() && cantidadVentas < MAX_VENTAS; i++) {
            Element ventaEl = (Element) listaVentas.item(i);
            
            Venta venta = new Venta();
            venta.setIdVenta(Integer.parseInt(ventaEl.getAttribute("id")));
            venta.setFecha(ventaEl.getElementsByTagName("fecha").item(0).getTextContent());
            venta.setPrecioFinal(Double.parseDouble(
                ventaEl.getElementsByTagName("precioFinal").item(0).getTextContent()));

            // Enlazar con el viaje
            int idViaje = Integer.parseInt(
                ventaEl.getElementsByTagName("idViaje").item(0).getTextContent());
            venta.setViaje(buscarViajePorId(idViaje));
            
            // Reconstruir pasajero
            Element pasajeroEl = (Element) ventaEl.getElementsByTagName("pasajero").item(0);
            if (pasajeroEl != null) {
                String dni = pasajeroEl.getElementsByTagName("dni").item(0).getTextContent();
                String nombre = pasajeroEl.getElementsByTagName("nombre").item(0).getTextContent();
                int edad = Integer.parseInt(pasajeroEl.getElementsByTagName("edad").item(0).getTextContent());
                venta.setPasajero(new Pasajero(dni, nombre, edad));
            }
            
            ventas[cantidadVentas] = venta;
            cantidadVentas++;
        }
    }

    /**
     * Registra una nueva venta en memoria.
     * Se llama cada vez que el Cajero completa una venta exitosa.
     * 
     * @param venta Venta a registrar
     * @return true si se registró correctamente, false si se alcanzó el límite
     */
    public boolean registrarVenta(Venta venta) {
        if (venta == null || cantidadVentas >= MAX_VENTAS) {
            return false;
        }
        ventas[cantidadVentas] = venta;
        cantidadVentas++;
        return true;
    }

    public Venta[] getVentas() {
        return ventas;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    /**
     * Obtiene solo las ventas registradas (sin las posiciones vacías del arreglo).
     * 
     * @return arreglo de Venta sin espacios vacíos
     */
    public Venta[] getVentasRegistradas() {
        return Arrays.copyOf(ventas, cantidadVentas);
    }

    /**
     * Guarda el historial completo de ventas en ventas.xml.
     * Incluye las ventas cargadas al inicio más las nuevas de esta sesión.
     * 
     * @throws Exception si hay error de E/S
     */
    public void guardarVentas() throws Exception {
        DocumentBuilder builder = crearParserXml();
        Document documento = builder.newDocument();
        Element root = documento.createElement("root");
        documento.appendChild(root);
        Element ventasEl = documento.createElement("ventas");
        root.appendChild(ventasEl);

        for (int i = 0; i < cantidadVentas; i++) {
            Venta v = ventas[i];
            Element ventaEl = documento.createElement("venta");
            ventaEl.setAttribute("id", String.valueOf(v.getIdVenta()));
            
            Element fecha = documento.createElement("fecha");
            fecha.setTextContent(v.getFecha());
            Element precioFinal = documento.createElement("precioFinal");
            precioFinal.setTextContent(String.valueOf(v.getPrecioFinal()));
            Element idViaje = documento.createElement("idViaje");
            idViaje.setTextContent(String.valueOf(
                v.getViaje() != null ? v.getViaje().getIdViaje() : -1));
            
            ventaEl.appendChild(fecha);
            ventaEl.appendChild(precioFinal);
            ventaEl.appendChild(idViaje);

            if (v.getPasajero() != null) {
                Element pasajeroEl = documento.createElement("pasajero");
                Element dni = documento.createElement("dni");
                dni.setTextContent(v.getPasajero().getDni());
                Element nombre = documento.createElement("nombre");
                nombre.setTextContent(v.getPasajero().getNombre());
                Element edad = documento.createElement("edad");
                edad.setTextContent(String.valueOf(v.getPasajero().getEdad()));
                pasajeroEl.appendChild(dni);
                pasajeroEl.appendChild(nombre);
                pasajeroEl.appendChild(edad);
                ventaEl.appendChild(pasajeroEl);
            }
            ventasEl.appendChild(ventaEl);
        }
        escribirDocumento(documento, CARPETA_XML + "ventas.xml");
    }

    // ==================== GUARDADO GENERAL ====================
    
    /**
     * Punto único de guardado al cerrar el programa.
     * Persiste los cambios realizados durante la sesión:
     * - viajes.xml: asientos actualizados (ocupados durante la sesión)
     * - ventas.xml: nuevo historial de ventas
     * 
     * @throws Exception si hay error de E/S
     */
    public void guardarDatos() throws Exception {
        guardarViajes();
        guardarVentas();
    }
}
