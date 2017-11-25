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
    public static Object MyLock1;
    public static Object MyLock2;
    public static Object MyLock3;
    
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
    
    private void enviaMensagem(String mensagem){
        try {
            out.writeUTF(mensagem); //envia a mensagem pros clientes
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public String getNome(){
        return this.nome;
    }
    private String recebeMensagem()
    {
        String retorno = null;
        
        try {
            retorno = in.readUTF(); //recebe a recebe a mensagem dos clientes
        } catch (IOException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            return retorno;
        }
        
    }
    
    private synchronized void carregaArquivo(){
        
        File arq = new File(path+"data.txt");
        
        try (InputStream in = new FileInputStream(arq)){
            
            Scanner scan = new Scanner(in);
            String n = null;
            
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
        
        String nome = recebeMensagem(); //recebe o nome do arquivo
        System.out.println("Nome do Arquivo: " + nome);
        
        File file = new File(path+nome); // carrega o arquivo local
        enviaMensagem(String.valueOf(file.length())); //diz ao servidor o tamanho do arquivo que vai ser upado
        enviaArquivo(path+nome); //faz o upload
        
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
            
            System.out.println("Servidor Enviou o Arquivo");
            fileIn.close();
           
            
            }catch(SocketException e){
                System.err.println("Cliente Cancelou Download!!");
            }catch (FileNotFoundException ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }catch (IOException ex) {
                Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    private void recebeArquivo(){
        int tamanho = Integer.parseInt(recebeMensagem()); //Eh passado o tamanho do arquivo
        String nomeArq = recebeMensagem();
        
        try{
            fileOut = new FileOutputStream(path+nomeArq);
            objIn = new ObjectInputStream(ns.getInputStream());
            
            byte[] buffer = new byte[4096];
            int len = 0;
            int total = 0;
            
            while (total < len) {
                len = objIn.read(buffer);
                total += len;
                fileOut.write(buffer, 0, len);
            }
            
            System.out.println("Arquivo Recebido");
            fileOut.close(); //Libera arquivo para ser utilizado
            
            synchronized(MyLock1){ // Para multiplas threads adicionarem arquivos
                if(!arquivosDisponiveis.contains(nomeArq)){
                    arquivosDisponiveis.add(nomeArq);
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
        
        while(true){
            
            ns = s.accept();
            novo = new ServidorArquivo(ns);
            exe.execute(novo);
            threadsAtivos.add(novo);
            System.out.println("Numero de Clientes Ativos: " + Thread.activeCount());
            
        }
        
        
    }

    @Override
    public void run() {
        
        try {
            
            in = new DataInputStream(ns.getInputStream());
            out = new DataOutputStream(ns.getOutputStream());
            nome = in.readUTF().toString();

            
            Protocolo protocolo = new Protocolo();
        
            String operacao; 
            
            while(true){

                operacao = in.readUTF();  
                System.out.println(operacao);

                if(operacao.equals("pesquisar")){
                    
                    out.writeUTF(protocolo.pesquisar(in.readUTF()));
                
                } else if(operacao.equals("listar")){
                
                    out.writeUTF(protocolo.listar());
                
                } else if(operacao.equals("fazerupload")){
                
                    recebeArquivo();
                
                } else if(operacao.equals("fazerdownload")){
                
                    fazUpload();
                
                }

            }
        }catch (Exception e) {}
    }
    
}
