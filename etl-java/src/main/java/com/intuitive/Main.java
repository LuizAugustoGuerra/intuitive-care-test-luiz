package com.intuitive;

import lombok.extern.java.Log;
import java.util.logging.Level;

@Log 
public class Main {
    public static void main(String[] args) {
       
        System.out.println("Olá, Intuitive Care! Ambiente configurado.");
        
        log.info("Lombok está funcionando corretamente! Bora codar.");
        
        log.log(Level.INFO, "Iniciando Pipeline de Dados...");
    }
}