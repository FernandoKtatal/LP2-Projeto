/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package projetofinallp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fernando e Kevin
 */

public class ServidorArquivo implements Runnable{
    
    private String nome = null;
    public static  Object MyLock1;
   
    public static LinkedList<String> arquivosDisponiveis; // Lista dos aquivos disponiveis
    public static LinkedList<ServidorArquivo> threadsAtivos; // Lista dos clientes ativos
    String path = System.getProperty("user.dir") + "/ArquivosServidor/"; // Local dos arquivos do servidor
    public static File arquivo; // arquivo que vai ser enviado/baixado
    
    Socket ns;
    DataInputStream in;
    DataOutputStream out;
    ObjectInputStream objIn;
    ObjectOutputStream objOut;
    FileInputStream fileIn;
    FileOutputStream fileOut;
    FileOutputStream file; //genrenciador de arquivos
    
    
    public ServidorArquivo(Socket ns) throws IOException{
        carregaArquivo();
        this.ns = ns;
    }

    public String getNome(){
        return this.nome;
    }
    
    
    private synchronized void carregaArquivo(){
        
        File arq = new File(path+"data.txt");
        
        try (InputStream s = new FileInputStream(arq)){
            
            Scanner scan = new Scanner(s);
            String n;
            
            while(scan.hasNext()){
                n = scan.nextLine();
                if(!arquivosDisponiveis.contains(n)) //se nao esta na lista ele adiciona
                    arquivosDisponiveis.add(n);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ServidorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void fazUpload(){
        
        try {
            
            String nome = in.readUTF(); //recebe o nome do arquivo
            
            File file = new File(path+nome); // carrega o arquivo local
            out.writeUTF(String.valueOf(file.length())); //diz ao servidor o tamanho do arquivo que vai ser upado
            enviaArquivo(path+nome); //faz o upload
        
        } catch (FileNotFoundException ex){
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServidorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void enviaArquivo(String caminho){
        try{
            this.fileIn = new FileInputStream(caminho);
            this.objOut = new ObjectOutputStream(ns.getOutputStream());
            
            byte[] buffer = new byte[4096];
            int len;
            
            while(true){
                
                len = fileIn.read(buffer);
                if(len == -1) //se ja terminou de enviar o arquivo
                    break;
                
                objOut.write(buffer, 0, len); //envia arquivo
                objOut.flush();
            }
            
            fileIn.close(); // libera o arquivo para ser usado
            
            
        }catch(SocketException e){
            System.err.println("Cliente Cancelou Download!!");
        }catch (FileNotFoundException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void recebeArquivo(){
        try{
            
            int tamanho = Integer.parseInt(in.readUTF()); //Eh passado o tamanho do arquivo
            String nomeArquivo = in.readUTF();
            
            fileOut = new FileOutputStream(path+nomeArquivo);
            objIn = new ObjectInputStream(ns.getInputStream());
            
            byte[] buffer = new byte[4096];
            int len = 0;
            int total = 0;
            
            while (total < tamanho) {
                
                len = objIn.read(buffer);
                total += len;
                fileOut.write(buffer, 0, len);
            }
            
            fileOut.close(); //Libera arquivo para ser utilizado
            
            synchronized(MyLock1){ // Para multiplas threads adicionarem arquivos
                
                if(!arquivosDisponiveis.contains(nomeArquivo)){
                    arquivosDisponiveis.add(nomeArquivo);
                    salvaArquivo();
                }
                
            }
            
        }catch(SocketException e){
            System.err.println("Cliente Cancelou Upload!!");
        }catch (FileNotFoundException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }catch (Exception ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void salvaArquivo(){
        
        File arquivo2 = new File(path + "data.txt");
        
        try(PrintWriter pw = new PrintWriter(arquivo2)){
            
            for(int i = 0; i < arquivosDisponiveis.size(); i++){
                pw.println(arquivosDisponiveis.get(i));
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServidorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) throws IOException {
        
        Socket ns;
        ServerSocket s = new ServerSocket(4444); //porta para conexao
        ExecutorService exe = Executors.newCachedThreadPool(); //pool de threads dinamico
        arquivosDisponiveis = new LinkedList<>(); //criando lista de arquivos
        threadsAtivos = new LinkedList<>(); //criando lista de clientes
        ServidorArquivo novo; //criando servidor
        
        MyLock1 = new Object();
        
        while(true){
            
            ns = s.accept();
            novo = new ServidorArquivo(ns);
            exe.execute(novo);
            threadsAtivos.add(novo);
            
        }
        
        
    }
    
    @Override
    public void run() {
        
        try {
            
            in = new DataInputStream(ns.getInputStream());
            out = new DataOutputStream(ns.getOutputStream());
            nome = in.readUTF();
            
            
            Protocolo protocolo = new Protocolo();
            
            String operacao;
            
            while(true){
                
                operacao = in.readUTF();
                
                switch (operacao) {
                    case "pesquisar":
                        {
                            ArrayList<String> lista = protocolo.pesquisar(in.readUTF());
                            for (String item : lista) {
                                out.writeUTF(item);
                            }       out.writeUTF("FIM DE LISTAGEM");
                            break;
                        }
                    case "listar":
                        {
                            ArrayList<String> lista = protocolo.listar();
                            for (String item : lista) {
                                out.writeUTF(item);
                            }       out.writeUTF("FIM DE LISTAGEM");
                            break;
                        }
                    case "fazerupload":
                        recebeArquivo();
                        break;
                    case "fazerdownload":
                        fazUpload();
                        break;
                    default:
                        break;
                }
                
            }
        }catch (Exception e) {}
    }
    
}
