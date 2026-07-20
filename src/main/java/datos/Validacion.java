package datos;

import transporte.Bus;

/*
Clase de utilidad con métodos estáticos de validación reutilizables. Diseñada para simplificar 
el código en clases como Cajero, Reporte, Administrador.
Todos los métodos lanzan IllegalArgumentException o IllegalStateException para los casos de validación fallida.
@author Julian
 */
public class Validacion {

    // Constructor privado para evitar instanciación
    private Validacion() {
        throw new AssertionError("No se puede instanciar Validacion.");
    }

    // DNI 
    public static boolean validarDni(String dni) {
        if (dni == null) {
            return false;
        }
        String limpio = dni.trim();
        return limpio.length() == 8 && limpio.chars().allMatch(Character::isDigit);
    }

    public static void validarDniOExcepcion(String dni) {
        if (!validarDni(dni)) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }
    }

    // TELÉFONO
    public static boolean validarTelefono(String telefono) {
        if (telefono == null) {
            return false;
        }
        String limpio = telefono.trim();
        return limpio.length() == 9 && limpio.chars().allMatch(Character::isDigit);
    }

    public static void validarTelefonoOExcepcion(String telefono) {
        if (!validarTelefono(telefono)) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 dígitos numéricos.");
        }
    }

    // EMAIL
    public static boolean validarEmail(String email) {
        if (email == null) {
            return false;
        }
        String limpio = email.trim();
        return limpio.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static void validarEmailOExcepcion(String email) {
        if (!validarEmail(email)) {
            throw new IllegalArgumentException("El email no tiene un formato válido.");
        }
    }

    // TEXTO
    public static boolean textoNoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static void validarTextoNoVacioOExcepcion(String texto, String nombreCampo) {
        if (!textoNoVacio(texto)) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede estar vacío.");
        }
    }

    // ENTEROS 
    public static int validarYParsearEntero(String texto, String nombreCampo, int min, int max) {
        validarTextoNoVacioOExcepcion(texto, nombreCampo);
        try {
            int val = Integer.parseInt(texto.trim());
            if (val < min || val > max) {
                throw new IllegalArgumentException(
                        String.format("%s debe estar entre %d y %d (ingresaste: %d).",
                                nombreCampo, min, max, val)
                );
            }
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("%s debe ser un número entero válido. Ingresaste: '%s'.",
                            nombreCampo, texto)
            );
        }
    }

    // DECIMALES
    public static double validarYParsearDecimal(String texto, String nombreCampo, double min, double max) {
        validarTextoNoVacioOExcepcion(texto, nombreCampo);
        try {
            double val = Double.parseDouble(texto.trim());
            if (val < min || val > max) {
                throw new IllegalArgumentException(
                        String.format("%s debe estar entre %.2f y %.2f (ingresaste: %.2f).",
                                nombreCampo, min, max, val)
                );
            }
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("%s debe ser un número decimal válido. Ingresaste: '%s'.",
                            nombreCampo, texto)
            );
        }
    }

    // EDAD
    public static int validarYParsearEdad(String textoEdad) {
        return validarYParsearEntero(textoEdad, "Edad", 0, 120);
    }

    // CAPACIDAD
    public static int validarCapacidad(String texto, String nombreCampo) {
        return validarYParsearEntero(texto, nombreCampo, 1, Integer.MAX_VALUE);
    }

    // HORA
    public static boolean validarHora(String hora) {
        if (hora == null) {
            return false;
        }
        return hora.trim().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    public static void validarHoraOExcepcion(String hora) {
        if (!validarHora(hora)) {
            throw new IllegalArgumentException("La hora debe estar en formato HH:MM (ej. 14:30).");
        }
    }

    // FECHA
    public static boolean validarFecha(String fecha) {
        if (fecha == null) {
            return false;
        }
        return fecha.trim().matches("^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/\\d{4}$");
    }

    public static void validarFechaOExcepcion(String fecha) {
        if (!validarFecha(fecha)) {
            throw new IllegalArgumentException("La fecha debe estar en formato DD/MM/YYYY.");
        }
    }

    // ASIENTOS
    public static int validarYParsearFila(String textoFila, int maxRows) {
        if (textoFila == null || textoFila.trim().isEmpty()) {
            throw new IllegalArgumentException("La fila no puede estar vacía.");
        }
        String t = textoFila.trim().toUpperCase();
        if (t.length() != 1 || !Character.isLetter(t.charAt(0))) {
            throw new IllegalArgumentException("La fila debe ser una sola letra (ej. A, B, C...).");
        }
        int fila = t.charAt(0) - 'A';
        if (fila < 0 || fila >= maxRows) {
            throw new IllegalArgumentException("La fila está fuera del rango válido (máximo: "
                    + ((char) ('A' + maxRows - 1)) + ").");
        }
        return fila;
    }

    public static int validarYParsearColumna(String textoColumna, int maxCols) {
        int col1based = validarYParsearEntero(textoColumna, "Número de asiento", 1, maxCols);
        return col1based - 1;
    }

    public static void validarAsientoConBus(Bus bus, int fila, int columna) {
        if (bus == null) {
            throw new IllegalArgumentException("Bus no asignado para el viaje.");
        }
        if (!bus.posicionValida(fila, columna)) {
            throw new IllegalArgumentException("Esa posición no existe en el mapa de asientos de este bus.");
        }
        if (!bus.asientoLibre(fila, columna)) {
            throw new IllegalStateException("Ese asiento ya está ocupado. Elija otro.");
        }
    }
}
