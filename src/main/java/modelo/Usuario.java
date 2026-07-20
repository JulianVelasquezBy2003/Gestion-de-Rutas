package modelo;

/**
 * Representa un usuario del sistema con autenticación y rol.
 *
 * @author Julian
 */
public class Usuario {

    private int id;
    private String nombre;
    private String contrasena;
    private String rol;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String contrasena) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío.");
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Contraseña no puede estar vacía.");
        }
        if (contrasena.length() < 6) {
            throw new IllegalArgumentException("Contraseña debe tener al menos 6 caracteres.");
        }

        this.id = id;
        this.nombre = nombre.trim();
        this.contrasena = contrasena.trim();
    }

    public Usuario(int id, String nombre, String contrasena, String rol) {
        this(id, nombre, contrasena);
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException("Rol no puede estar vacío.");
        }
        this.rol = rol.trim();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Contraseña no puede estar vacía.");
        }
        if (contrasena.length() < 6) {
            throw new IllegalArgumentException("Contraseña debe tener al menos 6 caracteres.");
        }
        this.contrasena = contrasena.trim();
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException("Rol no puede estar vacío.");
        }
        this.rol = rol.trim();
    }

    /**
     * Cambia la contraseña del usuario con validaciones de seguridad.
     *
     * @param contrasenaActual la contraseña actual para verificación
     * @param nuevaContrasena la nueva contraseña a establecer
     * @throws IllegalArgumentException si la contraseña actual es incorrecta o
     * la nueva no cumple requisitos
     */
    public void cambiarContrasena(String contrasenaActual, String nuevaContrasena) {
        if (contrasenaActual == null || !contrasenaActual.equals(this.contrasena)) {
            throw new IllegalArgumentException("Contraseña actual incorrecta.");
        }
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Nueva contraseña no puede estar vacía.");
        }
        if (nuevaContrasena.length() < 6) {
            throw new IllegalArgumentException("Nueva contraseña debe tener al menos 6 caracteres.");
        }
        if (nuevaContrasena.equals(this.contrasena)) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la actual.");
        }
        this.contrasena = nuevaContrasena.trim();
    }

    /**
     * Verifica si la contraseña proporcionada es correcta.
     *
     * @param contrasena contraseña a verificar
     * @return true si la contraseña es correcta
     */
    public boolean verificarContrasena(String contrasena) {
        return contrasena != null && contrasena.equals(this.contrasena);
    }

    /**
     * Valida si el rol es válido en el sistema.
     *
     * @param rol rol a validar
     * @return true si el rol es válido
     */
    public static boolean esRolValido(String rol) {
        return rol != null && (rol.equalsIgnoreCase("administrador")
                || rol.equalsIgnoreCase("vendedor")
                || rol.equalsIgnoreCase("gerente"));
    }

    @Override
    public String toString() {
        return String.format("Usuario #%d | %s | Rol: %s",
                id,
                nombre != null ? nombre : "N/D",
                rol != null ? rol : "N/D");
    }
}
