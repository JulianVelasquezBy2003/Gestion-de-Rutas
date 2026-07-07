package datos;

public class Validacion {

    // Valida que el DNI tenga exactamente 8 digitos numericos (formato Peru).
    public boolean validarDni(String dni) {
        if (dni == null) {
            return false;
        }
        String limpio = dni.trim();
        return limpio.length() == 8 && limpio.chars().allMatch(Character::isDigit);
    }

    /*
    public int leerEntero (){
    }
*/
}