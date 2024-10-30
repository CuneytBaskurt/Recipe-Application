
package main.java.yazlab1_1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTextArea;


public class InstructionPage extends javax.swing.JFrame {
    
     private final int recipeId;
    
    private final JTextArea instructionsArea;
    
    
    public InstructionPage(int recipeId, Connection connection) {
        
        this.recipeId = recipeId;
        System.out.println("Received recipeId: " + recipeId);
        initComponents();  
        instructionsArea = new JTextArea(); 
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        Instructions.setViewportView(instructionsArea); 
        loadInstructions(connection); 
    }
    
    
    
   private void loadInstructions(Connection connection) {
        String getInstructionsQuery = "SELECT Instructions FROM recipe WHERE RecipeId = " + recipeId;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getInstructionsQuery)) {
            
            if (rs.next()) {
                String instructions = rs.getString("Instructions");
                setInstructions(instructions); 
                System.out.println(instructions);                                
                
            } else {
                setInstructions("No instructions found for this recipe."); 
            }
        } catch (SQLException e) {
            System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
            setInstructions("Error fetching instructions.");
        }
    }
    
    public void setInstructions(String instructions) {
         instructionsArea.setText(instructions);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        close = new javax.swing.JButton();
        Instructions = new javax.swing.JScrollPane();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        close.setText("CLOSE");
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(close, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
            .addComponent(Instructions)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Instructions, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(close)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_closeActionPerformed

    /**
     * @param args the command line arguments
     */
     public static void main(String args[]) {
        // Nimbus look and feel ayarları...

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Connection connection = null; // Bağlantınızı oluşturun
                new InstructionPage(1, connection).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane Instructions;
    private javax.swing.JButton close;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}