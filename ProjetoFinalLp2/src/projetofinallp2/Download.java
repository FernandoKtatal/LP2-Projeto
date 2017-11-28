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
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Ktatal
 */
public class Download extends Thread {
    
    private DataInputStream in;
    private DataOutputStream out;
    
    private ObjectInputStream objIn;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private Socket s;
    private String mensagem;
    private String caminho;
    private String escolhido;
    private int tamanho;
    private Carregando porcentagem;
    private JTextArea j;
    
    public Download(Socket s, String caminho, String escolhido, JTextArea j){
       
        this.s = s;
        this.caminho = caminho;
        this.escolhido = escolhido;
        this.j = j;
        porcentagem = new Carregando(this);
        porcentagem.setVisible(true);
              
        
        try {
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            
            out.writeUTF("fazerdownload");
            
        } catch (IOException ex) {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void run(){
        try {
            
            float porcento;
            int len;
            float total = 0;
            
            out.writeUTF("fazerdownload");
            out.writeUTF(escolhido);
            tamanho = Integer.valueOf(in.readUTF());
            
            fileOut = new FileOutputStream(caminho);
            objIn = new ObjectInputStream(s.getInputStream());
            
            byte[] buffer = new byte[4096];
            
            while(total < tamanho){
                
                porcento = ((total/tamanho) * 100);
                len = objIn.read(buffer);
                total += len;
                porcentagem.setPorcentagem((int)total);
                fileOut.write(buffer, 0, len);
                
            }
            porcentagem.setVisible(false);
            fileOut.close(); //libera o arquivo
            
            Date d = new Date();
            
            j.insert("Arquivo: "+ escolhido+" Baixado as: "+ d.getHours()+":"+d.getMinutes() + "\n",JFrame.WIDTH);
            
            
        }catch (FileNotFoundException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }catch (SocketException ex) {
            JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            System.exit(1);
            
        } catch (IOException ex) {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
