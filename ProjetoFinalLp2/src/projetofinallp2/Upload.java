/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetofinallp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Fernando e Kevin
 */
public class Upload extends Thread{
    
    private ObjectOutputStream objOut;
    private FileInputStream fileIn;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket s;
    private String mensagem;
    private String caminho;
    private String nomeArq;
    private long tamanho;
    private JTextArea areaTexto;
    private Carregando tela;
    private TelaCliente principal;
    
    public Upload(Socket s, String caminho, long tamanho, String nomeArq, JTextArea areaTexto, TelaCliente principal) throws IOException{
        this.s = s;
        this.caminho = caminho;
        this.tamanho = tamanho;
        this.nomeArq = nomeArq;
        this.areaTexto = areaTexto;
        this.principal = principal;
        tela = new Carregando(this);
        tela.setVisible(true);
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
        
        out.writeUTF("fazerupload"); //diz pro servidor que quer enviar um arquivo

    }
    
    public void run(){
        float enviado = 0;
        int porcento = 0;
        
        try {
            out.writeUTF("fazerupload");
            out.writeUTF(String.valueOf(tamanho)); //diz o tamanho do arquivo a ser enviado
            out.writeUTF(nomeArq);
            
            fileIn = new FileInputStream(caminho);
            objOut = new ObjectOutputStream(s.getOutputStream());
            
            byte[] buffer = new byte[4096]; //pacotes a serem enviados
            int len = 0;
            
            while(true){
                len = fileIn.read(buffer);
                porcento = (int) ( ((enviado +=4096)/tamanho) * 100);
                tela.setPorcentagem(porcento);
                
                if(len == -1){
                    break;
                }
                objOut.write(buffer, 0, len);
                objOut.flush();   
            }
            
            Date date = new Date();
            areaTexto.insert("Arquivo: "+nomeArq+"\tAção: Upload\t\tHorario: "+date.getHours()+":"+date.getMinutes()+"\tTamanho: "+tamanho+"\n", JFrame.WIDTH);
            tela.setVisible(false);
            fileIn.close();
            Thread.sleep(150);
            principal.MetodoAttLista();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            System.exit(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(Upload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
