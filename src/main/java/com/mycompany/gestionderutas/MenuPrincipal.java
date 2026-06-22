package com.mycompany.gestionderutas;

import java.util.Scanner;
import modelo.Usuario;

public class MenuPrincipal {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        Usuario adm1 = new Usuario(1, "ADMIN", "123@", "Administrador");
        Usuario caje1 = new Usuario(2, "Angel", "12345", "Cajero");
        
        boolean acceso = false;

        while (!acceso) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━ Login ━━━━━━━━━━━━━━━━━━━━━━");
            System.out.print("Ingresar Nombre de Usuario: ");
            String usuario = input.next();
            System.out.print("Contraseña: ");
            String contraseña= input.next();

            if (usuario.equals(adm1.getNombre()) && contraseña.equals(adm1.getContraseña())) {
                acceso = true;
                System.out.println("\nLogin exitoso");
            } else {
                System.out.println("\nUsuario o contraseña incorrectos\n");
            }

            if (adm1.getRol().equals("Administrador")) {

                System.out.println("━━━━━━━━━━━━━━ Menu Administrativo ━━━━━━━━━━━━━━━");

            } else {

                System.out.println("━━━━━━━━━━━━━━━━━━ Menu Caja ━━━━━━━━━━━━━━━━━━━");
            }
        }

    }
}
