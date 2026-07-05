package modelo;

public class Usuario {

    // Atributos de la clase usuario
    private int id;
    private String nombre;
    private String contraseña;
    private String rol;

    // Constructores
    public Usuario() {
    }

    public Usuario(String nombre, String contraseña) {
    }

    public Usuario(int id, String nombre, String contraseña, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    // Getters & Setters
    //Modificadores del Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //Modificadores del Nombre

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    //Modificadores de la Contrasena

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    //Modificadores del Rol
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    //Metodo para cambiar Contrasena
    public void cambiarContrasena(String nuevaContrasena) {
    }

    /* @Override
    public String toString(){
        return 
    } */
}
