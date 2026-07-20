package modelo;

/*
Representa un usuario del sistema.
Clase base para Administrador y Cajero.
@author Julian
 */
public class Usuario {

    public static final String ROL_ADMIN = "admin";
    public static final String ROL_CAJERO = "cajero";

    private int id;
    private String nombre;
    private String contrasena;
    private String rol;

    public Usuario() { }

    public Usuario(int id, String nombre, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
    }

    public Usuario(int id, String nombre, String contrasena, String rol) {
        this(id, nombre, contrasena);
        setRol(rol);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un número positivo.");
        }
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().length() < 2) {
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres.");
        }
        this.nombre = nombre.trim();
    }

    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.contrasena = contrasena.trim();
    }

    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        if (!esRolValido(rol)) {
            throw new IllegalArgumentException("Rol inválido. Debe ser 'admin' o 'cajero'.");
        }
        this.rol = rol.toLowerCase().trim();
    }

    // --------------------------- MÉTODOS ---------------------------
    // Verifica si la contraseña ingresada coincide con la del usuario
    public boolean verificarContrasena(String contrasena) {
        return this.contrasena.equals(contrasena);
    }
    // Cambia la contraseña verificando la actual
    public void cambiarContrasena(String actual, String nueva) {
        if (!verificarContrasena(actual)) {
            throw new IllegalArgumentException("Contraseña actual incorrecta.");
        }
        setContrasena(nueva);
    }

    // Devuelve true si el usuario es Administrador.
    public boolean esAdministrador() {
        return ROL_ADMIN.equals(rol);
    }

    // Devuelve true si el usuario es Cajero.
    public boolean esCajero() {
        return ROL_CAJERO.equals(rol);
    }

    // Valida si un rol es válido (admin o cajero).
    public static boolean esRolValido(String rol) {
        if (rol == null) return false;
        String r = rol.toLowerCase().trim();
        return r.equals(ROL_ADMIN) || r.equals(ROL_CAJERO);
    }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nombre='%s', rol='%s'}", id, nombre, rol);
    }
}