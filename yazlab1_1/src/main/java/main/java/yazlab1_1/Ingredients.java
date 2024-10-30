
package main.java.yazlab1_1;

public class Ingredients {
    
    int IngredientsId;
    String IngredientsName;
    String TotalAmount;
    String IngredientUnit;
    int UnitPrice;

    public Ingredients(int IngredientsId, String IngredientsName, String TotalAmount, String IngredientUnit, int UnitPrice) {
        this.IngredientsId = IngredientsId;
        this.IngredientsName = IngredientsName;
        this.TotalAmount = TotalAmount;
        this.IngredientUnit = IngredientUnit;
        this.UnitPrice = UnitPrice;
    }

    public int getIngredientsId() {
        return IngredientsId;
    }

    public void setIngredientsId(int IngredientsId) {
        this.IngredientsId = IngredientsId;
    }

    public String getIngredientsName() {
        return IngredientsName;
    }

    public void setIngredientsName(String IngredientsName) {
        this.IngredientsName = IngredientsName;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String TotalAmount) {
        this.TotalAmount = TotalAmount;
    }

    public String getIngredientUnit() {
        return IngredientUnit;
    }

    public void setIngredientUnit(String IngredientUnit) {
        this.IngredientUnit = IngredientUnit;
    }

    public int getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(int UnitPrice) {
        this.UnitPrice = UnitPrice;
    }
    
    
    
    
    
}
