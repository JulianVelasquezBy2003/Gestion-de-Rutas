package gestionderutas;

import modelo.Usuario;
import transporte.Ruta;
import transporte.Viaje;
import transporte.Venta;
import datos.Persistencia;

public class GestorArchivo {

    private final Persistencia persistencia;

    public GestorArchivo() {
        this.persistencia = new Persistencia();
    }

    // Carga los datos usando Persistencia y propaga excepciones si las hay.
    public void cargarDatos() throws Exception {
        persistencia.cargarXML();      // usuarios
        persistencia.cargarRutas();    // rutas
        persistencia.cargarViajes();   // viajes
        persistencia.cargarVentas();   // ventas
    }

    // Guarda los datos a persistencia (asientos/ventas/etc).
    public void guardarDatos() throws Exception {
        persistencia.guardarDatos();
    }

    // Búsquedas delegadas (ejemplo)
    public Usuario buscarUsuarioPorNombre(String nombre) {
        Usuario[] usuarios = persistencia.getUsuarios();
        if (usuarios == null) return null;
        for (Usuario u : usuarios) {
            if (u != null && nombre.equals(u.getNombre())) return u;
        }
        return null;
    }

    public Ruta buscarRutaPorId(int id) {
        Ruta[] rutas = persistencia.getRutas(); // suponiendo que Persistencia devuelve Ruta[]
        if (rutas == null) return null;
        for (Ruta r : rutas) {
            if (r != null && r.getIdRuta() == id) return r;
        }
        return null;
    }

    public Viaje buscarViajePorId(int id) {
        Viaje[] viajes = persistencia.getTodosLosViajes(); // o getViajes()
        if (viajes == null) return null;
        for (Viaje v : viajes) {
            if (v != null && v.getIdViaje() == id) return v;
        }
        return null;
    }

    // Registrar venta delegando en Persistencia (si existe ese método)
    public boolean registrarVenta(Venta venta) {
        return persistencia.registrarVenta(venta);
    }

    // Getters de conveniencia (devuelven directamente lo de Persistencia)
    public Usuario[] getUsuarios() { return persistencia.getUsuarios(); }
    public Ruta[] getRutas() { return persistencia.getRutas(); }
    public Viaje[] getViajes() { return persistencia.getTodosLosViajes(); }
    public Venta[] getVentas() { return persistencia.getVentasRegistradas(); }
}

