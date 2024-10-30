
package main.java.yazlab1_1;


import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;



public class MainPage extends javax.swing.JFrame  implements Functions {
    
    private static final String URL = "jdbc:mysql://localhost:3306/yazlab1_1"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "BENbenveben1."; 
    private JPanel recipePanel; 
    
     
    public MainPage() {
         initComponents();
         createRecipePanel(); 
         connectToDatabase(); 
          
        SearchRecipe.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchRecipeDynamically();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchRecipeDynamically();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchRecipeDynamically();
            }
        });
    }
    private void searchRecipeDynamically() {
        SwingUtilities.invokeLater(() -> {
            String recipeName = SearchRecipe.getText();
            getRecipeWithName(recipeName);  
        });
    }
    
     private void createRecipePanel() {
        recipePanel = new JPanel(); 
        recipePanel.setLayout(new javax.swing.BoxLayout(recipePanel, javax.swing.BoxLayout.Y_AXIS)); 
        RecipePanel.setViewportView(recipePanel); 
    }
    
     
    private void connectToDatabase() {
    Connection connection = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connected to database");

        
        fetchRecipes(connection);
        getIngredients(connection);
        
    } catch (ClassNotFoundException e) {
        System.out.println("MySQL JDBC sürücüsü bulunamadı: " + e.getMessage());
    } catch (SQLException e) {
        System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
    } 
}
    
    

private void fetchRecipes(Connection connection) {
    String getRecipeName = "SELECT * FROM recipe"; 
    
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(getRecipeName)) {

        System.out.println("Recipe Details:");
        recipePanel.removeAll(); 

        while (rs.next()) {
            String recipeName = rs.getString("RecipeName");
            String category = rs.getString("Category"); 
            int recipeId = rs.getInt("RecipeId"); 
            int recipeTime = rs.getInt("Time"); 
            int price = rs.getInt("Price");

            String getIngredientsIds = "SELECT IngredientId FROM ingredients_recipe WHERE RecipeId = " + recipeId;
            boolean hasLowStock = false; 

            try (Statement ingredientStmt = connection.createStatement();
                 ResultSet ingredientRs = ingredientStmt.executeQuery(getIngredientsIds)) {

                StringBuilder ingredientsList = new StringBuilder();
                
                while (ingredientRs.next()) {
                    int ingredientId = ingredientRs.getInt("IngredientId");

                    String getIngredientDetails = "SELECT IngredientsName, TotalAmount FROM ingredients WHERE IngredientsId = " + ingredientId;
                    try (Statement nameStmt = connection.createStatement();
                         ResultSet nameRs = nameStmt.executeQuery(getIngredientDetails)) {
                         
                         if (nameRs.next()) {
                             String ingredientName = nameRs.getString("IngredientsName");
                             int totalAmount = nameRs.getInt("TotalAmount"); 

                            
                             if (totalAmount <= 0) {
                                 hasLowStock = true; 
                             }
                             ingredientsList.append(ingredientName).append("<br>"); 
                         }
                    }
                }

                
                Color recipeColor = hasLowStock ? Color.RED : Color.GREEN; 

                JLabel recipeLabel = new JLabel("<html><b>Recipe Name</b><br> " + recipeName + 
                                                 "<br><b>Category</b><br> " + category + 
                                                 "<br><b>Ingredients:</b><br>" + ingredientsList.toString() + 
                                                 "<br><b>Time</b><br>" + recipeTime + " mins" + 
                                                 "<br><b>Price</b><br>" + price + " TL");
                recipeLabel.setOpaque(true); 
                recipeLabel.setBackground(recipeColor); 
                recipeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
                recipePanel.add(recipeLabel); 
                
                JButton moreButton = new JButton("More...");
                recipePanel.add(moreButton); 
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                moreButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        InstructionPage instructionPage = new InstructionPage(recipeId, connection); 
                        instructionPage.setVisible(true);
                    }
                });
                
                JButton updateButton = new JButton("Update Recipe");
                recipePanel.add(updateButton);
                recipePanel.add(Box.createVerticalStrut(10));
                
                
                
                updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt){
                    
                     UpdateRecipe updateRecipe = new UpdateRecipe();
                     updateRecipe.id(recipeId);
                     MainPage mainPage = new MainPage();
                     mainPage.setVisible(false);
                     updateRecipe.setVisible(true);
                    
                    
                    }
                });
                
               
                
                JButton deleteButton = new JButton("Delete Recipe");
                recipePanel.add(deleteButton);
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                deleteButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        deleteRecipe(recipeId);
                        recipePanel.removeAll();
                        fetchRecipes(connection);
                        recipePanel.revalidate();
                        recipePanel.repaint();
                    }
                });
                
                
            }
        }

        recipePanel.revalidate();
        recipePanel.repaint(); 

    } catch (SQLException e) {
        System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
    }
}

private void getIngredients(Connection connection){
    String getIngredientsName = "SELECT IngredientsName FROM ingredients";
    
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(getIngredientsName)) {
             
        while(rs.next()){
            String ingredients = rs.getNString("IngredientsName");
            IngredientComboBox.addItem(ingredients);
        }
    } catch (SQLException e) {
        System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
    }
}




public void refreshRecipePanel() {
    recipePanel.removeAll();  
    connectToDatabase();  
    recipePanel.revalidate();  
    recipePanel.repaint();     
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SearchRecipe = new javax.swing.JTextField();
        AddRecipeButton = new javax.swing.JButton();
        FilterButton = new javax.swing.JButton();
        RecipePanel = new javax.swing.JScrollPane();
        Filters = new javax.swing.JComboBox<>();
        IngredientComboBox = new javax.swing.JComboBox<>();
        FİndWithIngredient = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        SearchRecipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchRecipeActionPerformed(evt);
            }
        });

        AddRecipeButton.setText("Add Recipe");
        AddRecipeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddRecipeButtonActionPerformed(evt);
            }
        });

        FilterButton.setText("Filter");
        FilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilterButtonActionPerformed(evt);
            }
        });

        Filters.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "sort by time (ascending)", "sort by time (descending)", "sort by cost (ascending)", "sort by cost (descending)", "sort by number of ingredients" }));
        Filters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FiltersActionPerformed(evt);
            }
        });

        FİndWithIngredient.setText("Find");
        FİndWithIngredient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FİndWithIngredientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RecipePanel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(AddRecipeButton)
                                .addGap(27, 27, 27)
                                .addComponent(Filters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FilterButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(SearchRecipe, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(111, 111, 111)
                                .addComponent(IngredientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FİndWithIngredient)))
                        .addGap(0, 399, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchRecipe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(IngredientComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FİndWithIngredient))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddRecipeButton)
                    .addComponent(FilterButton)
                    .addComponent(Filters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RecipePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AddRecipeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddRecipeButtonActionPerformed
        
         this.setVisible(false); 
         new AddRecipePage().setVisible(true); 
        
    }//GEN-LAST:event_AddRecipeButtonActionPerformed

    private void FilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterButtonActionPerformed
     
        String choice = Filters.getSelectedItem().toString();
        if(choice.equals("sort by time (ascending)")){
            sortByTimeAscending();
        }
        else if(choice.equals("sort by time (descending)")){
            sortByTimeDescending();
        }
        else if(choice.equals("sort by cost (ascending)")){
            sortByIncreasingCost();
        }
        else if(choice.equals("sort by cost (descending)")){
            sortByDecreasingCost();
        }
        else if(choice.equals("sort by number of ingredients")){
            sortByNumberOfIngredients();
        }
        else if(choice.equals("sort by category")){
        }
        
    }//GEN-LAST:event_FilterButtonActionPerformed
     
    
    @Override
public List<Recipe> sortByTimeAscending() {
    List<Recipe> recipes = new ArrayList<>();

    String query = "SELECT * FROM recipe ORDER BY Time ASC"; 

    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        recipePanel.removeAll();  

        while (rs.next()) {
            String recipeName = rs.getString("RecipeName");
            String category = rs.getString("Category");
            int recipeId = rs.getInt("RecipeId");
            int recipeTime = rs.getInt("Time");
            double recipePrice = rs.getDouble("Price"); 
            
            
            String getIngredientsIds = "SELECT IngredientId FROM ingredients_recipe WHERE RecipeId = " + recipeId;
            boolean hasLowStock = false; 
            
            try (Statement ingredientStmt = connection.createStatement();
                 ResultSet ingredientRs = ingredientStmt.executeQuery(getIngredientsIds)) {

                StringBuilder ingredientsList = new StringBuilder();
                
                while (ingredientRs.next()) {
                    int ingredientId = ingredientRs.getInt("IngredientId");

                    String getIngredientName = "SELECT IngredientsName, TotalAmount FROM ingredients WHERE IngredientsId = " + ingredientId;
                    try (Statement nameStmt = connection.createStatement();
                         ResultSet nameRs = nameStmt.executeQuery(getIngredientName)) {
                         
                         if (nameRs.next()) {
                             String ingredientName = nameRs.getString("IngredientsName");
                             int totalAmount = nameRs.getInt("TotalAmount"); 
                             
                             
                             if (totalAmount <= 0) {
                                 hasLowStock = true; 
                             }
                             
                             ingredientsList.append(ingredientName).append("<br>"); 
                         }
                    }
                }

               
                Color recipeColor = hasLowStock ? Color.RED : Color.GREEN; 

                
                JLabel recipeLabel = new JLabel("<html><b>Recipe Name</b>: " + recipeName + 
                                                 "<br><b>Category</b>: " + category +
                                                 "<br><b>Ingredients:</b><br>" + ingredientsList.toString() +
                                                 "<b>Time</b>: " + recipeTime + "m" + 
                                                 "<br><b>Price</b>: " + recipePrice + "₺</html>");
                recipeLabel.setOpaque(true); 
                recipeLabel.setBackground(recipeColor); 
                recipeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
                recipePanel.add(recipeLabel); 
                
                
                JButton moreButton = new JButton("More...");
                recipePanel.add(moreButton); 
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                moreButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        
                        try (Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            InstructionPage instructionPage = new InstructionPage(recipeId, newConnection); 
                            instructionPage.setVisible(true);
                        } catch (SQLException e) {
                            System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
                        }
                    }
                });
                
                 
                JButton deleteButton = new JButton("Delete Recipe");
                recipePanel.add(deleteButton);
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                deleteButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                         
                        try {
                            String deleteQuery = "DELETE FROM recipe WHERE RecipeId = " + recipeId;
                            try (Statement deleteStmt = connection.createStatement()) {
                                deleteStmt.executeUpdate(deleteQuery);
                                recipePanel.remove(recipeLabel);   
                                recipePanel.remove(deleteButton);   
                                recipePanel.revalidate();
                                recipePanel.repaint();
                            }
                        } catch (SQLException e) {
                            System.out.println("Tarif silme hatası: " + e.getMessage());
                        }
                    }
                });
            }

            recipePanel.add(Box.createVerticalStrut(10));   
        } 

        recipePanel.revalidate();
        recipePanel.repaint();

    } catch (SQLException e) {
        System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
    }

    return recipes;
}

@Override
public List<Recipe> sortByTimeDescending() {
        List<Recipe> recipes = new ArrayList<>();

    String query = "SELECT * FROM recipe ORDER BY Time DESC";  
    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        recipePanel.removeAll();   

        while (rs.next()) {
            String recipeName = rs.getString("RecipeName");
            String category = rs.getString("Category");
            int recipeId = rs.getInt("RecipeId");
            int recipeTime = rs.getInt("Time");
            double recipePrice = rs.getDouble("Price");  
            
             
            String getIngredientsIds = "SELECT IngredientId FROM ingredients_recipe WHERE RecipeId = " + recipeId;
            boolean hasLowStock = false;  
            
            try (Statement ingredientStmt = connection.createStatement();
                 ResultSet ingredientRs = ingredientStmt.executeQuery(getIngredientsIds)) {

                StringBuilder ingredientsList = new StringBuilder();
                
                while (ingredientRs.next()) {
                    int ingredientId = ingredientRs.getInt("IngredientId");

                    String getIngredientName = "SELECT IngredientsName, TotalAmount FROM ingredients WHERE IngredientsId = " + ingredientId;
                    try (Statement nameStmt = connection.createStatement();
                         ResultSet nameRs = nameStmt.executeQuery(getIngredientName)) {
                         
                         if (nameRs.next()) {
                             String ingredientName = nameRs.getString("IngredientsName");
                             int totalAmount = nameRs.getInt("TotalAmount");  
                             
                              
                             if (totalAmount <= 0) {
                                 hasLowStock = true;  
                             }
                             
                             ingredientsList.append(ingredientName).append("<br>"); 
                         }
                    }
                }

                 
                Color recipeColor = hasLowStock ? Color.RED : Color.GREEN;  

                 
                JLabel recipeLabel = new JLabel("<html><b>Recipe Name</b>: " + recipeName + 
                                                 "<br><b>Category</b>: " + category +
                                                 "<br><b>Ingredients:</b><br>" + ingredientsList.toString() +
                                                 "<b>Time</b>: " + recipeTime + "m" + 
                                                 "<br><b>Price</b>: " + recipePrice + "₺</html>");
                recipeLabel.setOpaque(true);  
                recipeLabel.setBackground(recipeColor);  
                recipeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
                recipePanel.add(recipeLabel); 
                
                 
                JButton moreButton = new JButton("More...");
                recipePanel.add(moreButton); 
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                moreButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                         
                        try (Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            InstructionPage instructionPage = new InstructionPage(recipeId, newConnection); 
                            instructionPage.setVisible(true);
                        } catch (SQLException e) {
                            System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
                        }
                    }
                });
                
                
                JButton deleteButton = new JButton("Delete Recipe");
                recipePanel.add(deleteButton);
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                deleteButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                         
                        try {
                            String deleteQuery = "DELETE FROM recipe WHERE RecipeId = " + recipeId;
                            try (Statement deleteStmt = connection.createStatement()) {
                                deleteStmt.executeUpdate(deleteQuery);
                                recipePanel.remove(recipeLabel);  
                                recipePanel.remove(deleteButton);   
                                recipePanel.revalidate();
                                recipePanel.repaint();
                            }
                        } catch (SQLException e) {
                            System.out.println("Tarif silme hatası: " + e.getMessage());
                        }
                    }
                });
            }

            recipePanel.add(Box.createVerticalStrut(10));   
        } 
        recipePanel.revalidate();
        recipePanel.repaint();

    } catch (SQLException e) {
        System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
    }

    return recipes;
    }



    private void FiltersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FiltersActionPerformed
        
    }//GEN-LAST:event_FiltersActionPerformed

    private void SearchRecipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchRecipeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SearchRecipeActionPerformed

    private void FİndWithIngredientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FİndWithIngredientActionPerformed
          
    String ingredient = (String) IngredientComboBox.getSelectedItem();
    
     
    List<Recipe> recipes = getRecipesWithIngredient(ingredient);
    
    
    recipePanel.removeAll();
    
    for (Recipe recipe : recipes) {
        JLabel recipeLabel = new JLabel("<html><b>Recipe Name:</b> " + recipe.getRecipeName() +
                                        "<br><b>Category:</b> " + recipe.getCategory() +
                                        "<br><b>Time:</b> " + recipe.getTime() + " mins" +
                                        "<br><b>Instructions:</b> " + recipe.getInstructions() +
                                        "<br><b>Price:</b> " + recipe.getPrice() + 
                                        "<br><br></html>");
        recipePanel.add(recipeLabel);
    }
    
    recipePanel.revalidate();
    recipePanel.repaint();
    }//GEN-LAST:event_FİndWithIngredientActionPerformed

    
    
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
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainPage().setVisible(true);
            }
        });
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddRecipeButton;
    private javax.swing.JButton FilterButton;
    private javax.swing.JComboBox<String> Filters;
    private javax.swing.JButton FİndWithIngredient;
    private javax.swing.JComboBox<String> IngredientComboBox;
    private javax.swing.JScrollPane RecipePanel;
    private javax.swing.JTextField SearchRecipe;
    // End of variables declaration//GEN-END:variables

    @Override
    public Recipe addRecipe(String recipeName, String category, String time, String instructions) {
         Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
        
        int timeInt = Integer.parseInt(time);

        
        Recipe recipe = new Recipe(recipeName, category, timeInt, instructions);

        
        String url = "jdbc:mysql://localhost:3306/yazlab1_1"; 
        String username = "root"; 
        String password = "BENbenveben1."; 

        
        connection = DriverManager.getConnection(url, username, password);

        
        String sql = "INSERT INTO recipe (RecipeName, Category, Time, Instructions) VALUES (?, ?, ?, ?)";

        
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, recipeName);
        preparedStatement.setString(2, category);
        preparedStatement.setInt(3, timeInt);
        preparedStatement.setString(4, instructions);

        
        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " satır eklendi.");

        return recipe; 

    } catch (NumberFormatException e) {
        System.out.println("Hata: Time parametresi bir sayı olmalıdır.");
        return null; 
    } catch (SQLException e) {
        System.out.println("Veritabanı hatası: " + e.getMessage());
        return null; 
    } finally {
        
        try {
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
        }
    }
    }

    @Override
    public void deleteRecipe(int Id) {
          Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
         
        connection = DriverManager.getConnection(URL, USER, PASSWORD);

         
        String sql = "DELETE FROM recipe WHERE RecipeId = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, Id);  

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " tarif silindi.");

    } catch (SQLException e) {
        System.out.println("Veritabanı silme hatası: " + e.getMessage());
    } finally {
         
        try {
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
        }
    }
    
  
    }
    
    

    
public List<Recipe> getRecipeWithName(String recipeName) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement ingredientStatement = null;
    ResultSet resultSet = null;
    ResultSet ingredientResultSet = null;
    List<Recipe> recipes = new ArrayList<>();

    try {
        recipePanel.removeAll();   

         
        connection = DriverManager.getConnection(URL, USER, PASSWORD);

         
        String sql = "SELECT * FROM recipe WHERE RecipeName LIKE ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "%" + recipeName + "%");

        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int recipeId = resultSet.getInt("RecipeId");
            String name = resultSet.getString("RecipeName");
            String category = resultSet.getString("Category");
            int time = resultSet.getInt("Time");
            String instructions = resultSet.getString("Instructions");
            int price = resultSet.getInt("Price");

            Recipe recipe = new Recipe(name, category, time, instructions, price);
            recipes.add(recipe);

             
            String ingredientSql = "SELECT IngredientsName, TotalAmount FROM ingredients " +
                    "WHERE IngredientsId IN (SELECT IngredientId FROM ingredients_recipe WHERE RecipeId = ?)";
            ingredientStatement = connection.prepareStatement(ingredientSql);
            ingredientStatement.setInt(1, recipeId);
            ingredientResultSet = ingredientStatement.executeQuery();

            StringBuilder ingredientsList = new StringBuilder();
            boolean hasLowStock = false;

            while (ingredientResultSet.next()) {
                String ingredientName = ingredientResultSet.getString("IngredientsName");
                int totalAmount = ingredientResultSet.getInt("TotalAmount");

                ingredientsList.append(ingredientName).append("<br>");

                if (totalAmount <= 0) {
                    hasLowStock = true;
                }
            }

             
            Color recipeColor = hasLowStock ? Color.RED : Color.GREEN;

             
            JLabel recipeLabel = new JLabel("<html><b>Recipe Name:</b> " + recipe.getRecipeName() +
                    "<br><b>Category:</b> " + recipe.getCategory() +
                    "<br><b>Time:</b> " + recipe.getTime() + " mins" +
                    "<br><b>Price:</b> " + recipe.getPrice() + "₺" +
                    "<br><b>Ingredients:</b><br>" + ingredientsList.toString() + "</html>");
            recipeLabel.setOpaque(true);   
            recipeLabel.setBackground(recipeColor);   
            recipeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
            recipePanel.add(recipeLabel); 

             
            JButton moreButton = new JButton("More...");
            recipePanel.add(moreButton); 
            recipePanel.add(Box.createVerticalStrut(10)); 

            moreButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try (Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        InstructionPage instructionPage = new InstructionPage(recipeId, newConnection);
                        instructionPage.setVisible(true);
                    } catch (SQLException e) {
                        System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
                    }
                }
            });

             
            JButton deleteButton = new JButton("Delete Recipe");
            recipePanel.add(deleteButton);
            recipePanel.add(Box.createVerticalStrut(10)); 

            deleteButton.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        try (Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {  
            String deleteQuery = "DELETE FROM recipe WHERE RecipeId = " + recipeId;
            try (Statement deleteStmt = newConnection.createStatement()) {  
                deleteStmt.executeUpdate(deleteQuery);
                recipePanel.remove(recipeLabel);   
                recipePanel.remove(deleteButton);   
                recipePanel.revalidate();
                recipePanel.repaint();
            }
        } catch (SQLException e) {
            System.out.println("Tarif silme hatası: " + e.getMessage());
        }
    }
});

            recipePanel.add(Box.createVerticalStrut(10));   
        }

        recipePanel.revalidate();
        recipePanel.repaint();

        if (recipes.isEmpty()) {
            System.out.println("Tarif bulunamadı.");
        }

    } catch (SQLException e) {
        System.out.println("Veritabanı hatası: " + e.getMessage());
    } finally {
        try {
            if (ingredientResultSet != null) ingredientResultSet.close();
            if (resultSet != null) resultSet.close();
            if (ingredientStatement != null) ingredientStatement.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
        }
    }

    return recipes;
}



   public List<Recipe> getRecipesWithIngredient(String ingredient) {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    PreparedStatement ingredientStatement = null;
    ResultSet resultSet = null;
    ResultSet ingredientResultSet = null;
    List<Recipe> recipes = new ArrayList<>();

    try {
         
        connection = DriverManager.getConnection(URL, USER, PASSWORD);

         
        String findIngredientIdSql = "SELECT IngredientsId FROM ingredients WHERE IngredientsName = ?";
        preparedStatement = connection.prepareStatement(findIngredientIdSql);
        preparedStatement.setString(1, ingredient);
        resultSet = preparedStatement.executeQuery();

         
        int ingredientId = -1;
        if (resultSet.next()) {
            ingredientId = resultSet.getInt("IngredientsId");
        }

        
        String findRecipesSql = "SELECT * FROM recipe WHERE RecipeId IN " +
                                "(SELECT RecipeId FROM ingredients_recipe WHERE IngredientId = ?)";
        preparedStatement = connection.prepareStatement(findRecipesSql);
        preparedStatement.setInt(1, ingredientId);
        resultSet = preparedStatement.executeQuery();

         
        while (resultSet.next()) {
            int recipeId = resultSet.getInt("RecipeId");
            String name = resultSet.getString("RecipeName");
            String category = resultSet.getString("Category");
            int time = resultSet.getInt("Time");
            String instructions = resultSet.getString("Instructions");

             
            instructions = wrapText(instructions, 145);  

            int price = resultSet.getInt("Price");

             
            Recipe recipe = new Recipe(name, category, time, instructions, price);
            recipes.add(recipe);

            String ingredientSql = "SELECT IngredientsName FROM ingredients " +
                                   "WHERE IngredientsId IN (SELECT IngredientId FROM ingredients_recipe WHERE RecipeId = ?)";
            ingredientStatement = connection.prepareStatement(ingredientSql);
            ingredientStatement.setInt(1, recipeId);
            ingredientResultSet = ingredientStatement.executeQuery();

            StringBuilder ingredientList = new StringBuilder();
            while (ingredientResultSet.next()) {
                String ingredientName = ingredientResultSet.getString("IngredientsName");
                ingredientList.append(ingredientName).append(", ");
            }

            if (ingredientList.length() > 0) {
                ingredientList.setLength(ingredientList.length() - 2);  
            }

            // Tarifin bilgilerini ekrana yazdır (tarif ve malzemeler)
            JLabel recipeLabel = new JLabel("<html><b>Recipe Name:</b> " + recipe.getRecipeName() + "<br>" +   
                                            "<b>Category:</b> " + recipe.getCategory() + "<br>" +  
                                            "<b>Time:</b> " + recipe.getTime() + " mins<br>" +  
                                            "<b>Instructions:</b> " + instructions.replace("\n", "<br>") + "<br>" +  
                                            "<b>Price:</b> " + recipe.getPrice() + "<br>" +   
                                            "<b>Ingredients:</b> " + ingredientList + 
                                            "<br><br></html>");
             
            recipePanel.add(recipeLabel);
        }

         
        recipePanel.revalidate();
        recipePanel.repaint();

    } catch (SQLException e) {
        System.out.println("Veritabanı hatası: " + e.getMessage());
    } finally {
         
        try {
            if (ingredientResultSet != null) ingredientResultSet.close();
            if (resultSet != null) resultSet.close();
            if (ingredientStatement != null) ingredientStatement.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Bağlantı kapatma hatası: " + e.getMessage());
        }
    }

    return recipes;
}

 
public String wrapText(String text, int maxLineLength) {
    StringBuilder wrappedText = new StringBuilder();
    int lineLength = 0;

    for (String word : text.split(" ")) {
        if (lineLength + word.length() > maxLineLength) {
            wrappedText.append("<br>");   
            lineLength = 0;   
        }
        wrappedText.append(word).append(" ");
        lineLength += word.length() + 1;  
    }

    return wrappedText.toString().trim();
}



    public List<Recipe> sortByIncreasingCost() {
    List<Recipe> recipes = new ArrayList<>();

    String query = "SELECT * FROM recipe ORDER BY Price ASC"; 

    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        recipePanel.removeAll();  

        while (rs.next()) {
            String recipeName = rs.getString("RecipeName");
            String category = rs.getString("Category");
            int recipeId = rs.getInt("RecipeId");
            int recipeTime = rs.getInt("Time");
            double recipePrice = rs.getDouble("Price");  
            
             
            String getIngredientsIds = "SELECT IngredientId FROM ingredients_recipe WHERE RecipeId = " + recipeId;
            boolean hasLowStock = false;  
            
            try (Statement ingredientStmt = connection.createStatement();
                 ResultSet ingredientRs = ingredientStmt.executeQuery(getIngredientsIds)) {

                StringBuilder ingredientsList = new StringBuilder();
                
                while (ingredientRs.next()) {
                    int ingredientId = ingredientRs.getInt("IngredientId");

                    String getIngredientName = "SELECT IngredientsName, TotalAmount FROM ingredients WHERE IngredientsId = " + ingredientId;
                    try (Statement nameStmt = connection.createStatement();
                         ResultSet nameRs = nameStmt.executeQuery(getIngredientName)) {
                         
                         if (nameRs.next()) {
                             String ingredientName = nameRs.getString("IngredientsName");
                             int totalAmount = nameRs.getInt("TotalAmount");  
                             
                              
                             if (totalAmount <= 0) {
                                 hasLowStock = true;  
                             }
                             
                             ingredientsList.append(ingredientName).append("<br>"); 
                         }
                    }
                }

                 
                Color recipeColor = hasLowStock ? Color.RED : Color.GREEN;  

                
                JLabel recipeLabel = new JLabel("<html><b>Recipe Name</b>: " + recipeName + 
                                                 "<br><b>Category</b>: " + category +
                                                 "<br><b>Ingredients:</b><br>" + ingredientsList.toString() +
                                                 "<b>Time</b>: " + recipeTime + "m" + 
                                                 "<br><b>Price</b>: " + recipePrice + "₺</html>");
                recipeLabel.setOpaque(true); 
                recipeLabel.setBackground(recipeColor); 
                recipeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
                recipePanel.add(recipeLabel); 
                
                
                JButton moreButton = new JButton("More...");
                recipePanel.add(moreButton); 
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                moreButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                         
                        try (Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            InstructionPage instructionPage = new InstructionPage(recipeId, newConnection);  
                            instructionPage.setVisible(true);
                        } catch (SQLException e) {
                            System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
                        }
                    }
                });
                
                 
                JButton deleteButton = new JButton("Delete Recipe");
                recipePanel.add(deleteButton);
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                deleteButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        
                        try {
                            String deleteQuery = "DELETE FROM recipe WHERE RecipeId = " + recipeId;
                            try (Statement deleteStmt = connection.createStatement()) {
                                deleteStmt.executeUpdate(deleteQuery);
                                recipePanel.remove(recipeLabel);   
                                recipePanel.remove(deleteButton);   
                                recipePanel.revalidate();
                                recipePanel.repaint();
                            }
                        } catch (SQLException e) {
                            System.out.println("Tarif silme hatası: " + e.getMessage());
                        }
                    }
                });
            }

            recipePanel.add(Box.createVerticalStrut(10));   
        } 

        recipePanel.revalidate();
        recipePanel.repaint();

    } catch (SQLException e) {
        System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
    }

    return recipes;
}


  @Override
public List<Recipe> sortByDecreasingCost() {
    List<Recipe> recipes = new ArrayList<>();

    String query = "SELECT * FROM recipe ORDER BY Price DESC"; 

    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        recipePanel.removeAll();   

        while (rs.next()) {
            String recipeName = rs.getString("RecipeName");
            String category = rs.getString("Category");
            int recipeId = rs.getInt("RecipeId");
            int recipeTime = rs.getInt("Time");
            double recipePrice = rs.getDouble("Price");  
            
             
            String getIngredientsIds = "SELECT IngredientId FROM ingredients_recipe WHERE RecipeId = " + recipeId;
            boolean hasLowStock = false;  
            
            try (Statement ingredientStmt = connection.createStatement();
                 ResultSet ingredientRs = ingredientStmt.executeQuery(getIngredientsIds)) {

                StringBuilder ingredientsList = new StringBuilder();
                
                while (ingredientRs.next()) {
                    int ingredientId = ingredientRs.getInt("IngredientId");

                    String getIngredientName = "SELECT IngredientsName, TotalAmount FROM ingredients WHERE IngredientsId = " + ingredientId;
                    try (Statement nameStmt = connection.createStatement();
                         ResultSet nameRs = nameStmt.executeQuery(getIngredientName)) {
                         
                         if (nameRs.next()) {
                             String ingredientName = nameRs.getString("IngredientsName");
                             int totalAmount = nameRs.getInt("TotalAmount");  
                             
                              
                             if (totalAmount <= 0) {
                                 hasLowStock = true;  
                             }
                             
                             ingredientsList.append(ingredientName).append("<br>"); 
                         }
                    }
                }

                 
                Color recipeColor = hasLowStock ? Color.RED : Color.GREEN;  

                 
                JLabel recipeLabel = new JLabel("<html><b>Recipe Name</b>: " + recipeName + 
                                                 "<br><b>Category</b>: " + category +
                                                 "<br><b>Ingredients:</b><br>" + ingredientsList.toString() +
                                                 "<b>Time</b>: " + recipeTime + "m" + 
                                                 "<br><b>Price</b>: " + recipePrice + "₺</html>");
                recipeLabel.setOpaque(true);  
                recipeLabel.setBackground(recipeColor);  
                recipeLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
                recipePanel.add(recipeLabel); 
                
                
                JButton moreButton = new JButton("More...");
                recipePanel.add(moreButton); 
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                moreButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                         
                        try (Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                            InstructionPage instructionPage = new InstructionPage(recipeId, newConnection);  
                            instructionPage.setVisible(true);
                        } catch (SQLException e) {
                            System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
                        }
                    }
                });
                
                 
                JButton deleteButton = new JButton("Delete Recipe");
                recipePanel.add(deleteButton);
                recipePanel.add(Box.createVerticalStrut(10)); 
                
                deleteButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        // Tarif silme işlemi
                        try {
                            String deleteQuery = "DELETE FROM recipe WHERE RecipeId = " + recipeId;
                            try (Statement deleteStmt = connection.createStatement()) {
                                deleteStmt.executeUpdate(deleteQuery);
                                recipePanel.remove(recipeLabel);   
                                recipePanel.remove(deleteButton);  
                                recipePanel.revalidate();
                                recipePanel.repaint();
                            }
                        } catch (SQLException e) {
                            System.out.println("Tarif silme hatası: " + e.getMessage());
                        }
                    }
                });
            }

            recipePanel.add(Box.createVerticalStrut(10));   
        } 

        recipePanel.revalidate();
        recipePanel.repaint();

    } catch (SQLException e) {
        System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
    }

    return recipes;
}


    public List<Recipe> sortByNumberOfIngredients() {
    List<Recipe> recipes = new ArrayList<>();

    String query = "SELECT RecipeId, COUNT(IngredientId) AS ingredientCount " +
                   "FROM ingredients_recipe " +
                   "GROUP BY RecipeId " +
                   "ORDER BY ingredientCount ASC";   

    try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        recipePanel.removeAll();   

        while (rs.next()) {
            int recipeId = rs.getInt("RecipeId");
            int ingredientCount = rs.getInt("ingredientCount");

             
            String recipeQuery = "SELECT * FROM recipe WHERE RecipeId = " + recipeId;
            try (Statement recipeStmt = connection.createStatement();
                 ResultSet recipeRs = recipeStmt.executeQuery(recipeQuery)) {

                if (recipeRs.next()) {
                    String recipeName = recipeRs.getString("RecipeName");
                    String category = recipeRs.getString("Category");
                    int recipeTime = recipeRs.getInt("Time");

                   
                    String getIngredientsIds = "SELECT IngredientId FROM ingredients_recipe WHERE RecipeId = " + recipeId;
                    boolean hasLowStock = false;  

                    try (Statement ingredientStmt = connection.createStatement();
                         ResultSet ingredientRs = ingredientStmt.executeQuery(getIngredientsIds)) {

                        while (ingredientRs.next()) {
                            int ingredientId = ingredientRs.getInt("IngredientId");
                            String getIngredientDetails = "SELECT TotalAmount FROM ingredients WHERE IngredientsId = " + ingredientId;
                            try (Statement nameStmt = connection.createStatement();
                                 ResultSet nameRs = nameStmt.executeQuery(getIngredientDetails)) {

                                if (nameRs.next()) {
                                    int totalAmount = nameRs.getInt("TotalAmount");  

                                    
                                    if (totalAmount <= 0) {
                                        hasLowStock = true;  
                                    }
                                }
                            }
                        }
                    }

                   
                    Color recipeColor = hasLowStock ? Color.RED : Color.GREEN;  

                    JLabel recipeLabel = new JLabel("<html><b>Recipe Name</b>: " + recipeName + 
                                                     "<br><b>Category</b>: " + category +
                                                     "<br><b>Ingredients Count</b>: " + ingredientCount + 
                                                     "<br><b>Time</b>: " + recipeTime + " mins</html>");
                    recipeLabel.setOpaque(true);  
                    recipeLabel.setBackground(recipeColor);  
                    recipePanel.add(recipeLabel);

                    JButton moreButton = new JButton("More...");
                    recipePanel.add(moreButton);
                    recipePanel.add(Box.createVerticalStrut(10));  

                    
                    moreButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            try (Connection newConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                                InstructionPage instructionPage = new InstructionPage(recipeId, newConnection); 
                                instructionPage.setVisible(true);
                            } catch (SQLException e) {
                                System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        }

        recipePanel.revalidate();
        recipePanel.repaint();

    } catch (SQLException e) {
        System.out.println("Veritabanı sorgu hatası: " + e.getMessage());
    }

    return recipes;
}

    @Override
public void updateRecipe(String recipeName, String category, String time, String instructions) {
    
   
}


}
