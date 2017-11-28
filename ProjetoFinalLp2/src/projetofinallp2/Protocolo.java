/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetofinallp2;

/**
 *
 * @author Kevin e Fernando
 */
public class Protocolo {
    
    public String pesquisar(String texto){
            
       for(int i = 0; i < ServidorArquivo.arquivosDisponiveis.size(); i++ ){
            if( ServidorArquivo.arquivosDisponiveis.get(i).startsWith(texto) ){
               return ServidorArquivo.arquivosDisponiveis.get(i);
             }
         }
         
//       for(int i=0; i < ServidorArquivo.threadsAtivos.size(); i++){
//           System.out.println(ServidorArquivo.threadsAtivos.get(i).toString());           
//       }
       
       return "Nenhum arquivo encontrado."; //Nao foram achados arquivos
    }
    
    public String listar(int id)
    {
        String lista = "";
        
        for(int i = id ; i < ServidorArquivo.arquivosDisponiveis.size();i++) {
            lista += ServidorArquivo.arquivosDisponiveis.get(i);
        }

        return lista;
    }
    
}
