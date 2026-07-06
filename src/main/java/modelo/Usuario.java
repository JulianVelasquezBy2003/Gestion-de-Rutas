package modelo;

public class Usuario {

    // Atributos de la clase usuario
    private int id;
    private String nombre;
    private String contrasena;
    private String rol;

    // Constructores
    public Usuario() {
    }

    public Usuario(int id, String nombre, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
    }

    public Usuario(int id, String nombre, String contrasena, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    //Getters & Setters
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

    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contraseña) {
        this.contrasena = contraseña;
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
        this.contrasena = nuevaContrasena;
    }

    @Override
    public String toString(){
        return nombre + "(" + rol +")";
    } 
}
