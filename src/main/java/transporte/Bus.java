package transporte;

// @author Julian
 
public class Bus {

    // Dimensiones estandar del bus segun la consigna: 4 filas x 10 columnas
    public static final int FILAS = 4;
    public static final int COLUMNAS = 10;

    private static final char LIBRE = 'O';
    private static final char OCUPADO = 'X';

    //Atributos
    private int idBus;
    private String placa;
    private int capacidad;
    private char[][] asientos;

    //Constructor
    public Bus() {
        this.asientos = crearMapaVacio();
    }

    // Crea un bus inicializando su mapa de asientos con FILAS x COLUMNAS posiciones libres.
    public Bus(int idBus, String placa, int capacidad) {
        this.idBus = idBus;
        this.placa = placa;
        this.capacidad = capacidad;
        this.asientos = crearMapaVacio();
    }

    /* 
    Construye un arreglo 2D de FILAS x COLUMNAS con todas las posiciones marcadas como 
    libres ('O'). Se usa en el constructor y como respaldo si el mapa cargado desde el XML 
    llega nulo o con tamano invalido.
     */
    private char[][] crearMapaVacio() {
        char[][] mapa = new char[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                mapa[i][j] = LIBRE;
            }
        }
        return mapa;
    }

    //Metodos
    public int getIdBus() {
        return idBus;
    }
    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }

    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getCapacidad() {
        return capacidad;
    }
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public char[][] getAsientos() {
        return asientos;
    }

    /*
    Reemplaza el mapa de asientos (por ejemplo, al cargarlo desde el XML). 
    Si el arreglo recibido es nulo, se conserva un mapa vacio
     */
    public void setAsientos(char[][] asientos) {
        this.asientos = (asientos != null) ? asientos : crearMapaVacio();
    }


    /* 
    Muestra en consola la distribucion de asientos del bus. Las filas se identifican con letras (A, B, C...) 
    y las columnas con numeros (1, 2, 3...), similar a un bus real.  [O] = libre, [X] = ocupado/vendido.
     */
    public void mostrarAsientos() {
        if (asientos == null) {
            System.out.println("⚠️ Este bus no tiene un mapa de asientos cargado.");
            return;
        }

        System.out.println("\n   --- Mapa de asientos | Bus: " + placa + " ---");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        // Encabezado con el numero de columna
        System.out.print("     ");
        for (int col = 0; col < asientos[0].length; col++) {
            System.out.printf("%-4d", (col + 1));
        }
        System.out.println();

        // Filas identificadas con letras (A, B, C, D...)
        for (int fila = 0; fila < asientos.length; fila++) {
            char letraFila = (char) ('A' + fila);
            System.out.print(" " + letraFila + "  ");
            for (int col = 0; col < asientos[fila].length; col++) {
                System.out.print(" [" + asientos[fila][col] + "]");
            }
            System.out.println();
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Leyenda: [O] Libre   [X] Ocupado/Vendido\n");
    }

    // Verifica si la posicion indicada existe dentro del mapa de asientos.
    public boolean posicionValida(int fila, int columna) {
        return asientos != null && fila >= 0 && fila < asientos.length && columna >= 0 && columna < asientos[fila].length;
    }

    // Indica si el asiento en la posicion dada esta libre. Se asume que posicionValida(fila, columna) ya fue verificado antes
    public boolean asientoLibre(int fila, int columna) {
        return posicionValida(fila, columna) && asientos[fila][columna] == LIBRE;
    }

    /* Marca un asiento como ocupado (vendido/reservado). Valida que la posicion exista y 
    que el asiento no este ya ocupado antes de modificar la matriz, evitando ArrayIndexOutOfBoundsException 
    y ventas duplicadas sobre el mismo asiento.
     */
    public boolean ocuparAsiento(int fila, int columna) {
        if (!posicionValida(fila, columna)) {
            System.out.println("❌ Esa posicion no existe en el mapa de asientos.");
            return false;
        }
        if (asientos[fila][columna] == OCUPADO) {
            System.out.println("❌ Ese asiento ya esta ocupado. Elija otro.");
            return false;
        }
        asientos[fila][columna] = OCUPADO;
        return true;
    }

    // Libera un asiento previamente ocupado (por ejemplo, si se anula una venta).
    public boolean liberarAsiento(int fila, int columna) {
        if (!posicionValida(fila, columna)) {
            System.out.println("❌ Esa posicion no existe en el mapa de asientos.");
            return false;
        }
        asientos[fila][columna] = LIBRE;
        return true;
    }

    // Calcula cuantos asientos estan ocupados actualmente. Util para el reporte de ocupacion del administrador.
    public int contarAsientosOcupados() {
        int contador = 0;
        if (asientos == null) {
            return 0;
        }
        for (char[] fila : asientos) {
            for (char estado : fila) {
                if (estado == OCUPADO) {
                    contador++;
                }
            }
        }
        return contador;
    }
}