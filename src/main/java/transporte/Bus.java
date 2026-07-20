package transporte;

/*
Representa un bus con su mapa de asientos
@author Julian
 */
public class Bus {
    // Número de filas del mapa de asientos.
    public static final int FILAS = 4;
    // Número de columnas del mapa de asientos
    public static final int COLUMNAS = 10;

    // Carácter que representa un asiento libre
    private static final char LIBRE = 'O';
    // Carácter que representa un asiento ocupado
    private static final char OCUPADO = 'X';

    // ATRIBUTOS 

    private int idBus;
    private String placa;
    private int capacidad;
    // Matriz de asientos: filas (A-D) x columnas (1-10)
    private char[][] asientos;

    // ==================== CONSTRUCTORES ====================

    /**
     * Constructor por defecto: crea un bus sin ID, placa ni capacidad,
     * pero con el mapa de asientos vacío (todos libres).
     */
    public Bus() {
        this.asientos = crearMapaVacio();
    }

    /**
     * Constructor principal.
     * 
     * @param idBus     identificador único del bus
     * @param placa     placa del bus (no puede ser null)
     * @param capacidad número total de asientos (debe ser >= 0)
     */
    public Bus(int idBus, String placa, int capacidad) {
        this.idBus = idBus;
        setPlaca(placa);
        setCapacidad(capacidad);
        this.asientos = crearMapaVacio();
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Crea un mapa de asientos vacío (todos los asientos libres).
     * 
     * @return matriz de FILAS x COLUMNAS con todas las celdas en 'O'
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

    // ==================== GETTERS Y SETTERS ====================

    public int getIdBus() { return idBus; }
    public void setIdBus(int idBus) { this.idBus = idBus; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) {
        this.placa = (placa != null) ? placa.trim() : "";
    }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa.");
        }
        this.capacidad = capacidad;
    }

    /**
     * Devuelve una copia del mapa de asientos para evitar modificaciones externas.
     * 
     * @return copia de la matriz de asientos, o null si no hay mapa
     */
    public char[][] getAsientos() {
        if (asientos == null) return null;
        char[][] copia = new char[asientos.length][];
        for (int i = 0; i < asientos.length; i++) {
            copia[i] = asientos[i].clone();
        }
        return copia;
    }

    /**
     * Asigna un nuevo mapa de asientos (copia el contenido).
     * Si el parámetro es null, se reemplaza por un mapa vacío.
     * 
     * @param asientos nueva matriz de asientos
     */
    public void setAsientos(char[][] asientos) {
        if (asientos == null) {
            this.asientos = crearMapaVacio();
            return;
        }
        this.asientos = new char[asientos.length][];
        for (int i = 0; i < asientos.length; i++) {
            this.asientos[i] = (asientos[i] != null) ? asientos[i].clone() : new char[COLUMNAS];
        }
    }

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Muestra en consola el mapa de asientos del bus.
     * Las filas se identifican con letras (A, B, C...) y las columnas con números.
     */
    public void mostrarAsientos() {
        if (asientos == null || asientos.length == 0) {
            System.out.println("⚠️ Mapa de asientos no disponible.");
            return;
        }
        System.out.println("\n   --- Mapa de asientos | Bus: " + placa + " ---");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.print("     ");
        for (int col = 0; col < asientos[0].length; col++) {
            System.out.printf("%-4d", (col + 1));
        }
        System.out.println();
        for (int fila = 0; fila < asientos.length; fila++) {
            char letra = (char) ('A' + fila);
            System.out.print(" " + letra + "  ");
            for (int col = 0; col < asientos[fila].length; col++) {
                System.out.print(" [" + asientos[fila][col] + "]");
            }
            System.out.println();
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Leyenda: [O] Libre   [X] Ocupado\n");
    }

    /**
     * Verifica si una posición (fila, columna) es válida dentro del mapa.
     * 
     * @param fila    índice de fila (0-based)
     * @param columna índice de columna (0-based)
     * @return true si la posición existe
     */
    public boolean posicionValida(int fila, int columna) {
        return asientos != null && fila >= 0 && fila < asientos.length
                && columna >= 0 && columna < asientos[fila].length;
    }

    /**
     * Verifica si un asiento está libre.
     * 
     * @param fila    índice de fila
     * @param columna índice de columna
     * @return true si el asiento está libre y la posición es válida
     */
    public boolean asientoLibre(int fila, int columna) {
        return posicionValida(fila, columna) && asientos[fila][columna] == LIBRE;
    }

    /**
     * Marca un asiento como ocupado.
     * 
     * @param fila    índice de fila
     * @param columna índice de columna
     * @return true si se ocupó correctamente, false si ya estaba ocupado o la posición es inválida
     */
    public boolean ocuparAsiento(int fila, int columna) {
        if (!asientoLibre(fila, columna)) return false;
        asientos[fila][columna] = OCUPADO;
        return true;
    }

    /**
     * Libera un asiento (lo marca como libre).
     * 
     * @param fila    índice de fila
     * @param columna índice de columna
     * @return true si se liberó correctamente, false si la posición es inválida
     */
    public boolean liberarAsiento(int fila, int columna) {
        if (!posicionValida(fila, columna)) return false;
        asientos[fila][columna] = LIBRE;
        return true;
    }

    /**
     * Cuenta cuántos asientos están ocupados actualmente.
     * 
     * @return número de asientos ocupados
     */
    public int contarAsientosOcupados() {
        int cont = 0;
        if (asientos == null) return 0;
        for (char[] fila : asientos) {
            for (char c : fila) {
                if (c == OCUPADO) cont++;
            }
        }
        return cont;
    }

    // ==================== SOBREESCRITURA ====================

    @Override
    public String toString() {
        return "Bus{id=" + idBus + ", placa='" + placa + "', capacidad=" + capacidad + "}";
    }
}