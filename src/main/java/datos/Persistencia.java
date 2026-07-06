package datos;

import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import modelo.Administrador;
import modelo.Cajero;
import modelo.Usuario;
import transporte.Ruta;
import transporte.Viaje;
import transporte.Venta;

public class Persistencia {

    private static final int MAX_USUARIOS = 100;
    private Usuario[] usuarios = new Usuario[MAX_USUARIOS];
    private int cantidadUsuarios = 0;

    public void cargarXML() throws Exception {
        // 1. Crea la fábrica que construirá el parser XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 2. Obtiene un parser (lector) de XML
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 3. Busca el archivo usuarios.xml dentro de src/main/resources
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/usuarios.xml");
        // 4. Lee el XML y lo convierte en un árbol (Document)
        Document documento = builder.parse(is);
        // 5. Une nodos de texto que estén separados
        documento.getDocumentElement().normalize();
        // 6. Obtiene todos los elementos <usuario>
        NodeList listaUsuarios = documento.getElementsByTagName("usuario");
        cantidadUsuarios = 0;
        // 7. Recorre cada usuario
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

    public void guardarRuta(Ruta ruta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = getClass().getClassLoader().getResourceAsStream("xml/rutas.xml");
        Document documento = builder.parse(is);
        documento.getDocumentElement().normalize();
        Element rutas = (Element) documento.getElementsByTagName("rutas").item(0);
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
        rutas.appendChild(nuevaRuta);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(documento);
        StreamResult result = new StreamResult(new File("src/main/resources/xml/rutas.xml"));
        transformer.transform(source, result);
    }

}


/*
    public void guardarUsuarios(Usuario[] usuarios) {
    }

    public void guardarRutas(Ruta[] rutas) {
    }

    public Viaje[][] cargarViajes() {
    }

    public void guardarViajes(Viaje[][] viajes) {
    }

    public Venta[] cargarVentas() {
    }

    public void guardarVentas(Venta[] ventas) {
    } */
