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
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Ktatal
 */
public class TelaCliente extends javax.swing.JFrame {

    private DataInputStream in;
    private DataOutputStream out;
    private Socket s;
    private String pesquisa; //armazenar a palavra a ser pesquisada
    private ObjectOutputStream objOut;
    private ObjectInput objIn;
    private FileInputStream fileIn;
    private FileOutputStream fileOut;
    private String path = System.getProperty("user.dir") + "/ArquivosCliente/";
    private Upload upload;
    private Download download;
    private String nome; // precisa ??
    private Thread t;
    private TimerTask time;;
    private long tamanho; //tamanho do arquivo
    /**
     * Creates new form TelaCliente
     */
    public TelaCliente() throws IOException {
        try{
            s = new Socket("localhost", 4444);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            nome = "Cliente";
            out.writeUTF(nome);
            
        }catch(ConnectException e){
            JOptionPane.showMessageDialog(null, "Servidor está offline, tente novamente mais tarde", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            System.exit(1);
        }
        try {
            initComponents();
            jFileChooser1.setEnabled(false);
            jFileChooser1.setVisible(false);
            DefaultListModel model2 = new DefaultListModel();
            this.s = s;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro", "Erro inesperado, tente novamente", JOptionPane.ERROR_MESSAGE);
            s.close();
            System.exit(1);
        }
    }

    
    public String getNome(){
        return this.nome;
    }
    
    private String informacoes(){
        String escolhido;
        escolhido = String.valueOf(jList1.getSelectedValuesList());
        escolhido = escolhido.replace("[", "");
        escolhido = escolhido.replace("]", "");
        
        return escolhido;
    }
    
    public void MetodoAttLista(){
        
        try {
            String arquivoEncontrado;
            
            out.writeUTF("listar"); //Envia protocolo de listagem
            
            DefaultListModel model = new DefaultListModel();
            
            jList1.setModel(model);
            System.out.println("mostrando lista");
            
            while( ! (arquivoEncontrado = in.readUTF()).equals("") ){
                model.addElement(arquivoEncontrado);
            }
        } catch (IOException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void enviaArq(String caminho){
        float enviado = 0;
        try{
            fileIn = new FileInputStream(caminho);
            objOut = new ObjectOutputStream(s.getOutputStream());
            byte[] buffer = new byte[4096];
            int len = 0;
            
            while(true){
                len = fileIn.read(buffer);
                
                if(len == -1){
                    break; //sai do laço depois que o arquivo eh enviado
                }
                objOut.write(buffer, 0, len);
                objOut.flush();
            }
            
            System.out.println("Cliente: Enviado");
            fileIn.close(); //fecha o envio da arquivo
            
            
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Arquivo não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Desculpe, serviço temporariamente offline", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            System.exit(1);        }
    }
    
    private void recebeArq(String caminho){
        try {
            int tamanho = Integer.parseInt(in.readUTF());
            fileOut = new FileOutputStream(caminho);
            objIn = new ObjectInputStream(s.getInputStream());
            
            byte[] buffer = new byte[4096];
            int len = 0;
            int total = 0;
            
            while(total < tamanho){
                len = objIn.read(buffer);
                total += len;
                fileOut.write(buffer, 0, len);
            }
            fileOut.close(); //libera o arquivo para ser usado
           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jBDown = new javax.swing.JButton();
        jBUp = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        JBSearch = new javax.swing.JButton();
        jFileChooser1 = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jBDown.setText("Download");
        jBDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDownActionPerformed(evt);
            }
        });

        jBUp.setText("Upload");
        jBUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBUpActionPerformed(evt);
            }
        });

        JBSearch.setText("Search");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 644, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 644, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBDown, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jBUp, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JBSearch)))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBDown)
                    .addComponent(jBUp)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JBSearch))
                .addGap(50, 50, 50)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jFileChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBUpActionPerformed
        jFileChooser1.setEnabled(true);
        jFileChooser1.setVisible(true);
        jFileChooser1.showOpenDialog(this);
        //Armazena Arquivo
        try{
            Socket socket;
            socket = new Socket("localhost", 4444);
            String caminhoArq = "";
            String nomeArq = "";
            
            caminhoArq = jFileChooser1.getSelectedFile().getAbsolutePath();
            
            if(!caminhoArq.equals(null)){
                tamanho = jFileChooser1.getSelectedFile().length();
                nomeArq = jFileChooser1.getSelectedFile().getName();
                upload = new Upload(socket, caminhoArq, tamanho, nomeArq, jTextArea1);
                upload.start();
                
            }
            MetodoAttLista();
        } catch (IOException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jBUpActionPerformed

    private void jBDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDownActionPerformed
        try {
            String escolhido;
            escolhido = informacoes();
            
            Socket socket;
            socket = new Socket("localhost", 4444);
            if(!escolhido.equals("")){
                download = new Download(socket, path+escolhido, escolhido, jTextArea1);
                download.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jBDownActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TelaCliente().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JBSearch;
    private javax.swing.JButton jBDown;
    private javax.swing.JButton jBUp;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
