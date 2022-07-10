package PortableStorage.registry;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;

public class ModRecipeRegistry {
    public static void RegisterAll() {
        Recipes.registerModRecipe(new Recipe(
            "SmallBag",
            1,
            RecipeTechRegistry.WORKSTATION,
            new Ingredient[]{
                    new Ingredient("goldbar", 5),
                    new Ingredient("leather", 10),
                    new Ingredient("wool", 15)
            }
        ));

        Recipes.registerModRecipe(new Recipe(
            "MediumBag",
            1,
            RecipeTechRegistry.DEMONIC,
            new Ingredient[]{
                    new Ingredient("SmallBag", 2),
                    new Ingredient("demonicbar", 15),
                    new Ingredient("wool", 10)
            }
        ).showAfter("SmallBag"));

        Recipes.registerModRecipe(new Recipe(
            "LargeBag",
            1,
            RecipeTechRegistry.DEMONIC,
            new Ingredient[]{
                    new Ingredient("MediumBag", 2),
                    new Ingredient("glacialbar", 10),
                    new Ingredient("ivybar", 10)
            }
        ).showAfter("MediumBag"));

        Recipes.registerModRecipe(new Recipe(
                "ExtraLargeBag",
                1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("LargeBag", 2),
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("ancientfossilbar", 10)
                }
        ).showAfter("LargeBag"));

        Recipes.registerModRecipe(new Recipe(
                "TacoBox",
                1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("fishtaco", 25),
                        new Ingredient("palmdinnertable", 1),
                        new Ingredient("wool", 25)
                },
                true

        ));

        Recipes.registerModRecipe(new Recipe(
                "DeepBag",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[] {
                        new Ingredient("SmallBag", 4),
                        new Ingredient("voidshard", 20),
                        new Ingredient("demonicbar", 15),
                        new Ingredient("voidpouch", 2),
                        new Ingredient("darkgem", 1)
                }
        ).showAfter("SmallBag"));

        Recipes.registerModRecipe(new Recipe(
                "DeeperBag",
                1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[] {
                        new Ingredient("DeepBag", 2),
                        new Ingredient("ExtraLargeBag", 1),
                        new Ingredient("tungstenbar", 20)
                }
        ).showAfter("DeepBag"));
    }
}
