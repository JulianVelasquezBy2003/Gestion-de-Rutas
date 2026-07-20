package datos;

import transporte.Bus;

/*
Utilidades de validación y parseo para entrada de usuario.
Todos los métodos lanzan IllegalArgumentException si el dato no es válido.
@author Julian
 */
public final class Validacion {

    private Validacion() {
        throw new AssertionError("No instanciar");
    }

    // --------------------------- VALIDACIONES BÁSICAS ---------------------------------
    // Valida que un texto no sea nulo ni vacío (tras recortar espacios)
    public static void validarTextoNoVacio(String texto, String nombreCampo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede estar vacío.");
        }
    }
    // Devuelve true si el texto no es nulo ni vacío
    public static boolean esTextoNoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    // --------------------------- NÚMEROS ---------------------------
    // Convierte un texto a entero y valida que esté en [min, max].
    public static int parsearEntero(String texto, String nombreCampo, int min, int max) {
        validarTextoNoVacio(texto, nombreCampo);
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor < min || valor > max) {
                throw new IllegalArgumentException(
                        String.format("%s debe estar entre %d y %d (ingresaste: %d).",
                                nombreCampo, min, max, valor)
                );
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("%s debe ser un número entero. Ingresaste: '%s'.",
                            nombreCampo, texto)
            );
        }
    }

    // Convierte un texto a double y valida que esté en [min, max].
    public static double parsearDecimal(String texto, String nombreCampo, double min, double max) {
        validarTextoNoVacio(texto, nombreCampo);
        try {
            double valor = Double.parseDouble(texto.trim());
            if (valor < min || valor > max) {
                throw new IllegalArgumentException(
                        String.format("%s debe estar entre %.2f y %.2f (ingresaste: %.2f).", nombreCampo, min, max, valor)
                );
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("%s debe ser un número decimal. Ingresaste: '%s'.", nombreCampo, texto)
            );
        }
    }

    // --------------------------- VALIDACIONES ESPECÍFICAS ---------------------------
    // DNI: exactamente 8 dígitos numéricos
    public static void validarDni(String dni) {
        if (dni == null || !dni.trim().matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }
    }

    // Teléfono: exactamente 9 dígitos numéricos
    public static void validarTelefono(String telefono) {
        if (telefono == null || !telefono.trim().matches("\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 dígitos numéricos.");
        }
    }

    // Email: formato básico (usuario@dominio)
    public static void validarEmail(String email) {
        if (email == null || !email.trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El email no tiene un formato válido.");
        }
    }

    // Contraseña: al menos 6 caracteres no vacíos
    public static void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (contrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
    }

    // Edad: entero entre 0 y 120.
    public static int parsearEdad(String textoEdad) {
        return parsearEntero(textoEdad, "Edad", 0, 120);
    }

    // Capacidad: entero positivo (mínimo 1)
    public static int parsearCapacidad(String texto, String nombreCampo) {
        return parsearEntero(texto, nombreCampo, 1, Integer.MAX_VALUE);
    }

    // Hora en formato HH:MM (24h)
    public static void validarHora(String hora) {
        if (hora == null || !hora.trim().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("La hora debe estar en formato HH:MM (ej. 14:30).");
        }
    }

    // Fecha en formato DD/MM/YYYY
    public static void validarFecha(String fecha) {
        if (fecha == null || !fecha.trim().matches("^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/\\d{4}$")) {
            throw new IllegalArgumentException("La fecha debe estar en formato DD/MM/YYYY.");
        }
    }

    // --------------------------- ASIENTOS (para bus) ---------------------------
    //  Convierte una letra (A, B, C...) a índice de fila (0, 1, 2...).
    public static int parsearFila(String textoFila, int maxRows) {
        validarTextoNoVacio(textoFila, "Fila");
        String letra = textoFila.trim().toUpperCase();
        if (letra.length() != 1 || !Character.isLetter(letra.charAt(0))) {
            throw new IllegalArgumentException("La fila debe ser una sola letra (ej. A, B, C...).");
        }
        int fila = letra.charAt(0) - 'A';
        if (fila < 0 || fila >= maxRows) {
            char maxLetra = (char) ('A' + maxRows - 1);
            throw new IllegalArgumentException("La fila está fuera del rango (máximo: " + maxLetra + ").");
        }
        return fila;
    }

    // Convierte un número de asiento (1-based) a índice de columna (0-based).
    public static int parsearColumna(String textoColumna, int maxCols) {
        int columna1 = parsearEntero(textoColumna, "Número de asiento", 1, maxCols);
        return columna1 - 1;
    }

    //  Valida que la posición (fila, columna) exista y esté libre en el bus.
    public static void validarAsientoEnBus(Bus bus, int fila, int columna) {
        if (bus == null) {
            throw new IllegalArgumentException("El bus no está asignado al viaje.");
        }
        if (!bus.posicionValida(fila, columna)) {
            throw new IllegalArgumentException("Esa posición no existe en el mapa de asientos.");
        }
        if (!bus.asientoLibre(fila, columna)) {
            throw new IllegalStateException("Ese asiento ya está ocupado. Elija otro.");
        }
    }
}