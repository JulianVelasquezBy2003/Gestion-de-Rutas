package com.mycompany.gestionderutas;

import java.util.Scanner;
import modelo.Usuario;
import datos.GestorArchivo;

public class MenuPrincipal {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        GestorArchivo gestor = new GestorArchivo();
        boolean acceso = false;
        Usuario usuarioLogin;

        while (!acceso) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━ Login ━━━━━━━━━━━━━━━━━━━━━━");
            System.out.print("Ingresar Nombre de Usuario: ");
            String usuario = input.next();
            System.out.print("Contraseña: ");
            String contraseña = input.next();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            usuarioLogin = gestor.autenticar(usuario, contraseña);

            if (usuarioLogin != null) {
                acceso = true;
                System.out.println("✅ Login exitoso ");
            } else {
                System.out.println("❌ Usuario o contraseña incorrectos\n");
            }
        }

    }
}
