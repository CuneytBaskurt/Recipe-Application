
package main.java.yazlab1_1;

import java.util.List;


public class Recipe {
    
    int RecipeId;
    String RecipeName;
    String Category;
    int Time;
    String Instructions;
    int Price;
    

    public Recipe(String RecipeName, String Category, int Time, String Instructions) {
        this.RecipeName = RecipeName;
        this.Category = Category;
        this.Time = Time;
        this.Instructions = Instructions;
    }
    
    public Recipe(String RecipeName, String Category, int Time, String Instructions, int Price) {
        this.RecipeName = RecipeName;
        this.Category = Category;
        this.Time = Time;
        this.Instructions = Instructions;
        this.Price = Price;
    }
    
    

    public int getPrice() {
        return Price;
    }

    public void setPrice(int Price) {
        this.Price = Price;
    }

    public int getRecipeId() {
        return RecipeId;
    }

    public void setRecipeId(int RecipeId) {
        this.RecipeId = RecipeId;
    }

    public String getRecipeName() {
        return RecipeName;
    }

    public void setRecipeName(String RecipeName) {
        this.RecipeName = RecipeName;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int Time) {
        this.Time = Time;
    }

    public String getInstructions() {
        return Instructions;
    }

    public void setInstructions(String Instructions) {
        this.Instructions = Instructions;
    }
    
    
    
}
