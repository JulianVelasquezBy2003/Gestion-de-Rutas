package datos;

import modelo.Usuario;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivo {

    // Carga usuarios desde el XML usando JDOM
    public List<Usuario> cargarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            // Obtener el archivo desde resources
            InputStream is = getClass().getResourceAsStream("/xml/usuarios.xml");
            if (is == null) {
                System.err.println("ERROR: No se encontró /xml/usuarios.xml");
                return usuarios;
            }

            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(is);
            Element root = doc.getRootElement(); // <Usuarios>

            List<Element> elementosUsuario = root.getChildren("Usuario");
            for (Element e : elementosUsuario) {
                int id = Integer.parseInt(e.getChildText("id"));
                String nombre = e.getChildText("nombre");
                String contraseña = e.getChildText("contraseña");
                String rol = e.getChildText("rol");
                usuarios.add(new Usuario(id, nombre, contraseña, rol));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    // Autenticación (igual que antes)
    public Usuario autenticar(String nombre, String contraseña) {
        List<Usuario> usuarios = cargarUsuarios();
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre) && u.getContraseña().equals(contraseña)) {
                return u;
            }
        }
        return null;
    }

    // Guardar usuarios en XML (sobrescribe el archivo en el directorio de trabajo)
    public void guardarUsuarios(List<Usuario> usuarios) {
        try {
            Element root = new Element("Usuarios");
            Document doc = new Document(root);

            for (Usuario u : usuarios) {
                Element e = new Element("Usuario");
                e.addContent(new Element("id").setText(String.valueOf(u.getId())));
                e.addContent(new Element("nombre").setText(u.getNombre()));
                e.addContent(new Element("contraseña").setText(u.getContraseña()));
                e.addContent(new Element("rol").setText(u.getRol()));
                root.addContent(e);
            }

            // Guardar en archivo (en el directorio de trabajo)
            XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
            File file = new File("usuarios.xml");
            xmlOutput.output(doc, new FileWriter(file));
            System.out.println("✅ Usuarios guardados en " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Métodos
    public void cargarRuta() {
    }

    public void cargarVenta() {
    }

    public void guardarUsuario() {
    }

    public void guardarRuta() {
    }

    public void guardarVenta() {
    }
}
