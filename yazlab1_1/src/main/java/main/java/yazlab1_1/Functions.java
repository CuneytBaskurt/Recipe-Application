
package main.java.yazlab1_1;

import java.util.List;


public interface Functions {
    
    Recipe addRecipe(String recipeName, String category, String time, String instructions);
    
    void deleteRecipe(int Id);
    
    List<Recipe> getRecipeWithName(String RecipeName);
    
    List<Recipe> sortByTimeAscending();
    
    List<Recipe> sortByTimeDescending();
    
    List<Recipe> getRecipesWithIngredient(String ingredient);
    
    List<Recipe> sortByIncreasingCost();
    
    List<Recipe> sortByDecreasingCost();
    
    List<Recipe> sortByNumberOfIngredients();
       
    void updateRecipe(String recipeName, String category, String time, String instructions);
    
}
