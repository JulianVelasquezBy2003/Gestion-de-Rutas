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

/* Encargada de leer y escribir la informacion del sistema en los archivos XML 
de src/main/resources/xml (persistencia de datos), y de reconstruir los arreglos 
en memoria (Usuario[], Ruta[], Viaje[][]) que usa el resto  de la aplicacion.
 @author Julian,Angela
 */
public class Persistencia {

    private static final String CARPETA_XML = "src/main/resources/xml/";

    /*Abre el archivo XML indicado siempre desde la carpeta fuente real del proyecto, 
    para que lo que se lee sea exactamente lo mismo que se escribio la ultima vez.
     */
    private File archivoXml(String nombreArchivo) {
        return new File(CARPETA_XML + nombreArchivo);
    }

    //  Usuarios 
    private static final int MAX_USUARIOS = 100;
    private Usuario[] usuarios = new Usuario[MAX_USUARIOS];
    private int cantidadUsuarios = 0;

    //  Rutas 
    private static final int MAX_RUTAS = 50;
    private Ruta[] rutas = new Ruta[MAX_RUTAS];
    private int cantidadRutas = 0;

    //  Matriz de horarios: filas = destinos, columnas = franjas horarias 
    private static final int MAX_DESTINOS = 10;
    private static final int MAX_HORARIOS = 5;
    private Viaje[][] viajes = new Viaje[MAX_DESTINOS][MAX_HORARIOS];

    //  Ventas (historial de la sesion + lo cargado del XML) 
    private static final int MAX_VENTAS = 500;
    private Venta[] ventas = new Venta[MAX_VENTAS];
    private int cantidadVentas = 0;

    //  Metodo para cargar USUARIOS XML
    public void cargarXML() throws Exception {
        //Crea la fábrica que construirá el parser XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //Obtiene un parser (lector) de XML
        DocumentBuilder builder = factory.newDocumentBuilder();
        //Lee el XML y lo convierte en un árbol (Document)
        Document documento = builder.parse(archivoXml("usuarios.xml"));
        //Une nodos de texto que estén separados
        documento.getDocumentElement().normalize();
        //Obtiene todos los elementos <usuario>
        NodeList listaUsuarios = documento.getElementsByTagName("usuario");
        cantidadUsuarios = 0;
        //Recorre cada usuario
        for (int i = 0; i < listaUsuarios.getLength() && cantidadUsuarios < MAX_USUARIOS; i++) {
            Element usuario = (Element) listaUsuarios.item(i);
            int id = Integer.parseInt(usuario.getAttribute("id"));
            String nombre = usuario.getElementsByTagName("nombre").item(0).getTextContent();
            String contrasena = usuario.getElementsByTagName("contrasena").item(0).getTextContent();
            String rol = usuario.getAttribute("rol");
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

    /* Carga todas las rutas desde rutas.xml hacia el arreglo unidimensional de Ruta en memoria.
     */
    //  Metodo para cargar RUTAS XML
    public void cargarRutas() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
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

    /* Busca una ruta por su ID dentro del arreglo ya cargado en memoria.
     @param id identificador de la ruta
     @return la Ruta encontrada, o null si no existe
     */
    public Ruta buscarRutaPorId(int id) {
        for (int i = 0; i < cantidadRutas; i++) {
            if (rutas[i].getIdRuta() == id) {
                return rutas[i];
            }
        }
        return null;
    }

    //  MATRIZ DE HORARIOS (VIAJES) 
    /*Carga la matriz de viajes (Destinos x Horarios) desde viajes.xml. Cada viaje trae su posicion en la 
    matriz mediante los atributos fila/columna, ademas de su bus (con su mapa de asientos). 
     */
    public void cargarViajes() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.parse(archivoXml("viajes.xml"));
        documento.getDocumentElement().normalize();

        viajes = new Viaje[MAX_DESTINOS][MAX_HORARIOS];

        NodeList listaViajes = documento.getElementsByTagName("viaje");
        for (int i = 0; i < listaViajes.getLength(); i++) {
            Element viajeEl = (Element) listaViajes.item(i);
            int fila = Integer.parseInt(viajeEl.getAttribute("fila"));
            int columna = Integer.parseInt(viajeEl.getAttribute("columna"));
            if (fila < 0 || fila >= MAX_DESTINOS || columna < 0 || columna >= MAX_HORARIOS) {
                System.out.println("⚠️ El viaje id=" + viajeEl.getAttribute("id") + " tiene una posicion fuera del tamaño de la matriz y fue omitido.");
                continue;
            }

            Viaje viaje = new Viaje();
            viaje.setIdViaje(Integer.parseInt(viajeEl.getAttribute("id")));
            viaje.setFecha(viajeEl.getElementsByTagName("fecha").item(0).getTextContent());
            viaje.setHora(viajeEl.getElementsByTagName("hora").item(0).getTextContent());

            int idRuta = Integer.parseInt(viajeEl.getElementsByTagName("idRuta").item(0).getTextContent());
            viaje.setRuta(buscarRutaPorId(idRuta));

            Element busEl = (Element) viajeEl.getElementsByTagName("bus").item(0);
            if (busEl != null) {
                viaje.setBus(parseBus(busEl));
            }

            viajes[fila][columna] = viaje;
        }
    }

    /* Construye un objeto Bus (incluyendo su mapa de asientos) a partir del elemento bus, EN viajes.xml
     */
    private Bus parseBus(Element busEl) {
        int idBus = Integer.parseInt(busEl.getElementsByTagName("id").item(0).getTextContent());
        String placa = busEl.getElementsByTagName("placa").item(0).getTextContent();
        int capacidad = Integer.parseInt(busEl.getElementsByTagName("capacidad").item(0).getTextContent());

        Bus bus = new Bus(idBus, placa, capacidad);

        Element asientosEl = (Element) busEl.getElementsByTagName("asientos").item(0);
        if (asientosEl != null) {
            char[][] mapa = new char[Bus.FILAS][Bus.COLUMNAS];
            for (char[] filaArr : mapa) {
                Arrays.fill(filaArr, 'O');
            }

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

    /* Recorre la matriz de horarios buscando la fila que corresponde al destino indicado y devuelve todos 
    los viajes programados (frecuencias) disponibles para ese destino.
     @param destino nombre del destino a buscar (no distingue mayusculas/minusculas)
     @return arreglo con los viajes encontrados (tamaño 0 si no hay ninguno)
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
                break; // ya se encontro la fila del destino, no hace falta seguir
            }
        }
        return Arrays.copyOf(resultado, encontrados);
    }

    /**
     * Obtiene el nombre del destino asociado a una fila de la matriz, tomandolo del primer viaje no nulo que encuentre en esa fila.
     */
    private String obtenerDestinoDeFila(int fila) {
        for (int columna = 0; columna < MAX_HORARIOS; columna++) {
            if (viajes[fila][columna] != null && viajes[fila][columna].getRuta() != null) {
                return viajes[fila][columna].getRuta().getDestino();
            }
        }
        return null;
    }

    /*  Busca la fila de la matriz que ya esta siendo usada para el destino indicado. Si el destino todavia no tiene ninguna fila asignada, 
    reserva la primera fila libre para el. Es la base de "Agregar Horario": cada destino nuevo ocupa una fila, y cada frecuencia horaria 
    de ese destino ocupa una columna dentro de esa fila
    @param destino nombre del destino
    @return el indice de fila a usar, o -1 si la matriz ya no tiene filas libres para un destino nuevo
     */
    public int obtenerOCrearFilaParaDestino(String destino) {
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            String destinoDeLaFila = obtenerDestinoDeFila(fila);
            if (destinoDeLaFila != null && destinoDeLaFila.equalsIgnoreCase(destino)) {
                return fila;
            }
        }
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            if (obtenerDestinoDeFila(fila) == null) {
                return fila;
            }
        }
        return -1;
    }

    /* Busca la primera columna (franja horaria) libre dentro de una fila.
     @param fila indice de fila dentro de la matriz
     @return el indice de columna libre, o -1 si la fila ya esta completa
     */
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

    /* Calcula el siguiente ID disponible para un nuevo viaje, revisando el mayor ID ya usado en toda la matriz
     */
    public int siguienteIdViaje() {
        int maxId = 0;
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            for (int columna = 0; columna < MAX_HORARIOS; columna++) {
                if (viajes[fila][columna] != null && viajes[fila][columna].getIdViaje() > maxId) {
                    maxId = viajes[fila][columna].getIdViaje();
                }
            }
        }
        return maxId + 1;
    }

    /* Inserta un viaje en la posicion indicada de la matriz, validando que la posicion exista y este libre.
     @return true si se pudo insertar, false si la posicion es invalida o ya estaba ocupada
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

    public static int getMaxDestinos() {
        return MAX_DESTINOS;
    }

    public static int getMaxHorarios() {
        return MAX_HORARIOS;
    }

    /* Busca un viaje dentro de la matriz por su ID, recorriendo todas las posiciones. Se usa para reconstruir 
    el enlace Venta y Viaje al cargar ventas.xml
     */
    public Viaje buscarViajePorId(int idViaje) {
        for (int fila = 0; fila < MAX_DESTINOS; fila++) {
            for (int columna = 0; columna < MAX_HORARIOS; columna++) {
                if (viajes[fila][columna] != null && viajes[fila][columna].getIdViaje() == idViaje) {
                    return viajes[fila][columna];
                }
            }
        }
        return null;
    }

    //  VENTAS
    /* Agrega una venta al historial en memoria (arreglo de tamaño fijo, sin colecciones dinamicas). 
    Debe llamarse cada vez que Cajero complete una venta exitosa
    @param venta la venta a registrar
    @return true si se pudo agregar, false si ya no hay espacio disponible
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

    /* Carga el historial de ventas desde ventas.xml (si existen registros previos 
    guardados en una ejecucion anterior). Debe ejecutarse despues de cargarRutas() 
    y cargarViajes(), ya que cada venta se enlaza con su Viaje original mediante idViaje.
     */
    public void cargarVentas() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.parse(archivoXml("ventas.xml"));
        documento.getDocumentElement().normalize();

        NodeList listaVentas = documento.getElementsByTagName("venta");
        cantidadVentas = 0;
        for (int i = 0; i < listaVentas.getLength() && cantidadVentas < MAX_VENTAS; i++) {
            Element ventaEl = (Element) listaVentas.item(i);

            Venta venta = new Venta();
            venta.setIdVenta(Integer.parseInt(ventaEl.getAttribute("id")));
            venta.setFecha(ventaEl.getElementsByTagName("fecha").item(0).getTextContent());
            venta.setPrecioFinal(Double.parseDouble(ventaEl.getElementsByTagName("precioFinal").item(0).getTextContent()));

            int idViaje = Integer.parseInt(ventaEl.getElementsByTagName("idViaje").item(0).getTextContent());
            venta.setViaje(buscarViajePorId(idViaje));

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

    /* Reescribe por completo ventas.xml con todas las ventas actualmente en memoria 
    (las cargadas al inicio mas las nuevas de esta sesion).
     */
    public void guardarVentas() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
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
            idViaje.setTextContent(String.valueOf(v.getViaje() != null ? v.getViaje().getIdViaje() : -1));

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

    /* Reescribe por completo viajes.xml a partir de la matriz actual en memoria,
    incluyendo el mapa de asientos ya actualizado de cada bus (asientos vendidos 
    durante la sesion no se pierden al reiniciar)
     */
    public void guardarViajes() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
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
                idRuta.setTextContent(String.valueOf(viaje.getRuta() != null ? viaje.getRuta().getIdRuta() : -1));

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

    /* Construye el elemento bus (con su mapa de asientos actual) para guardarlo dentro de un viaje en viajes.xml
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

    /* Elimina recursivamente los nodos de texto que solo contienen espacios en blanco o saltos de linea 
    (los que deja el indentado del archivo original al leerlo). Se debe llamar antes de reescribir un 
    documento ya existente, para que el Transformer no duplique la indentacion
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

    /* Transforma un Document XML en memoria y lo escribe a disco.
     */
    private void escribirDocumento(Document documento, String rutaArchivo) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(documento);
        StreamResult result = new StreamResult(new File(rutaArchivo));
        transformer.transform(source, result);
    }

    /* Punto unico de guardado al cerrar el programa: vuelca a los XML tanto 
    los asientos actualizados (viajes.xml) como el historial de ventas (ventas.xml), 
    tal como exige la consigna al seleccionar "Salir"
     */
    public void guardarDatos() throws Exception {
        guardarViajes();
        guardarVentas();
    }

    //  GUARDAR RUTA 
    public void guardarRuta(Ruta ruta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.parse(archivoXml("rutas.xml"));
        documento.getDocumentElement().normalize();
        // Elimina los saltos de linea/espacios que ya trae el archivo leido,
        // para que al reescribirlo con indentado automatico no se dupliquen
        // (si no se hace esto, cada ruta nueva agrega una linea en blanco mas).
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
    }
}
