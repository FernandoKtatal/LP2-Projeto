/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetofinallp2;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fernando e Kevin
 */
public class Automatico extends Thread{
    TelaCliente tela;
    int segundos = 7;
    public Automatico(TelaCliente tela){
        this.tela = tela;
    }
    
    public synchronized void att(){
        start();
        notifyAll();
    }
    
    @Override
    public void run(){
        while (true) {            
            tela.MetodoAttLista();
            try {
                sleep(1000* segundos);
            } catch (InterruptedException ex) {
                Logger.getLogger(Automatico.class.getName()).log(Level.SEVERE, null, ex);
            }
            tela.MetodoAttLista();
            
        }
    }
}
