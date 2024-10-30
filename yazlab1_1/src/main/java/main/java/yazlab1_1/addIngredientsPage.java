 
package main.java.yazlab1_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

 
public class addIngredientsPage extends javax.swing.JFrame {
    
     private static final String URL = "jdbc:mysql://localhost:3306/yazlab1_1";  
    private static final String USER = "root";  
    private static final String PASSWORD = "BENbenveben1.";  

    
     private void connectToDatabase() {
    Connection connection = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connected to database");


        getIngredients(connection);
        
    } catch (ClassNotFoundException e) {
        System.out.println("MySQL JDBC sürücüsü bulunamadı: " + e.getMessage());
    } catch (SQLException e) {
        System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
    } finally {
        
       
    }
}
     
     private void getIngredients(Connection connection){
    String getIngredientsName = "SELECT IngredientsName FROM ingredients";
    
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(getIngredientsName)) {
             
        while(rs.next()){
            String ingredients = rs.getNString("IngredientsName");
            IngredientsComboBox.addItem(ingredients);
        }
    } catch (SQLException e) {
        System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
    }
}
    
    public addIngredientsPage() {
        initComponents();
        connectToDatabase();
    }

     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        IngredientsComboBox = new javax.swing.JComboBox<>();
        addIngredient = new javax.swing.JButton();
        goToMain = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Ingredients - Amount");

        addIngredient.setText("Add");
        addIngredient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIngredientActionPerformed(evt);
            }
        });

        goToMain.setText("Finished");
        goToMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToMainActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(goToMain)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addIngredient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(IngredientsComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1))
                .addContainerGap(144, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(IngredientsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(addIngredient)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
                .addComponent(goToMain)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private int getId() {
    
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    int lastId = -1;  

    try {
         
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASSWORD);

         
        String query = "SELECT MAX(RecipeId) AS last_id FROM recipe";
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

         
        if (resultSet.next()) {
            lastId = resultSet.getInt("last_id");
            System.out.println("Son eklenen kaydın id değeri: " + lastId);
        }
    } catch (ClassNotFoundException | SQLException e) {
        System.out.println("Hata: " + e.getMessage());
    } finally {
         
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Kaynak kapatma hatası: " + e.getMessage());
        }
    }

    return lastId;  
}

    
    private void addIngredientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIngredientActionPerformed
        
          
    String selectedIngredient = (String) IngredientsComboBox.getSelectedItem();
    int ingredientId = -1;
    int recipeId = getId();  
    String amount = jTextField1.getText();  

    
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    try {
       
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASSWORD);

        
        String query = "SELECT IngredientsId, UnitPrice FROM ingredients WHERE IngredientsName = '" + selectedIngredient + "'";
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

        if (resultSet.next()) {
            ingredientId = resultSet.getInt("IngredientsId");
            double unitPrice = resultSet.getDouble("UnitPrice"); 

           
            double amountValue = Double.parseDouble(amount); 

         
            double totalPrice = unitPrice * amountValue;

           
            if (ingredientId != -1 && recipeId != -1 && amount != null && !amount.isEmpty()) {
                String insertQuery = "INSERT INTO ingredients_recipe (RecipeId, IngredientId, ingredientsAmount) VALUES (" 
                    + recipeId + ", " + ingredientId + ", '" + amount + "')";
                statement.executeUpdate(insertQuery);
                System.out.println("Ingredient eklendi: RecipeId=" + recipeId + ", IngredientId=" + ingredientId + ", Amount=" + amount);

                
                String updateQuery = "UPDATE recipe SET Price = " + totalPrice + " WHERE RecipeId = " + recipeId;
                statement.executeUpdate(updateQuery);
                System.out.println("Price güncellendi: RecipeId=" + recipeId + ", TotalPrice=" + totalPrice);
            } else {
                System.out.println("Eksik bilgi veya geçersiz değer");
            }
        }
    } catch (ClassNotFoundException | SQLException e) {
        System.out.println("Hata: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.out.println("Miktar geçersiz: " + e.getMessage());
    } finally {
        
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Kaynak kapatma hatası: " + e.getMessage());
        }
    }
        
    }//GEN-LAST:event_addIngredientActionPerformed

    private void goToMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToMainActionPerformed
        this.setVisible(false);
        MainPage mainPage = new MainPage();
        mainPage.refreshRecipePanel();
        mainPage.setVisible(true);
    }//GEN-LAST:event_goToMainActionPerformed

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
            java.util.logging.Logger.getLogger(addIngredientsPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(addIngredientsPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(addIngredientsPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(addIngredientsPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new addIngredientsPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> IngredientsComboBox;
    private javax.swing.JButton addIngredient;
    private javax.swing.JButton goToMain;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
