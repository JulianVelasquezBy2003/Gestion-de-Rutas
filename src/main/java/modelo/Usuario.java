package modelo;

public class Usuario {
    
    // Atributos de la clase usuario
    private int id;
    private String nombre;
    private String contraseña;
    private String rol;

    // Constructores
    public Usuario(int id, String nombre, String contraseña, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    // Getters & Setters    
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
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}
