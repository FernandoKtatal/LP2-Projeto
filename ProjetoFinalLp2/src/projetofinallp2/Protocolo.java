/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetofinallp2;

import java.util.ArrayList;

/**
 *
 * @author Kevin e Fernando
 */
public class Protocolo {
    
    public ArrayList pesquisar(String texto){
        
        ArrayList<String> lista = new ArrayList();
        
        for(int i = 0; i < ServidorArquivo.arquivosDisponiveis.size(); i++ ){
            
            if( ServidorArquivo.arquivosDisponiveis.get(i).toUpperCase().contains(texto.toUpperCase()) ){
               lista.add(ServidorArquivo.arquivosDisponiveis.get(i));
            }
        }
       
       return lista;
    }
    
    public ArrayList listar()
    {        
        ArrayList<String> lista = new ArrayList();
        
        for(int i = 0 ; i < ServidorArquivo.arquivosDisponiveis.size();i++) {  
            
            lista.add(ServidorArquivo.arquivosDisponiveis.get(i));

        }
        
        return lista;

    }
    
}
