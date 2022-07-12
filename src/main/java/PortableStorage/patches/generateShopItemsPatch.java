package PortableStorage.patches;

import necesse.engine.GameLog;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.villageShops.ShopItemStock;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import net.bytebuddy.asm.Advice;

import java.util.HashMap;

@ModMethodPatch(target = VillageShopsData.class, name = "generateShopItems", arguments = {Level.class})
public class generateShopItemsPatch {
    @Advice.OnMethodEnter
    public static boolean onEnter(
            @Advice.FieldValue("generatedShopItems") boolean generatedShopItems

    ) {
        GameLog.debug.println("Obtained boolean generatedShopItems: " + generatedShopItems);
        return generatedShopItems;
    }
    @Advice.OnMethodExit
    public void onExit(
            @Advice.This VillageShopsData data,
            @Advice.Argument(0) Level level,
            @Advice.FieldValue("shopItems") HashMap<String, ShopItemStock> shopItems,
            @Advice.Enter() boolean generatedShopItems
    ) {
        GameLog.debug.println("On method exit called, \ndata: " + data + "\nlevel: " + level + "\nshopItems: " + shopItems + "\nBoolean: " + generatedShopItems);
        if (!generatedShopItems) {
            GameLog.debug.println("Inside of the if statement.");
            shopItems.put("Tacklebox", new ShopItemStock("TackleAbox", 25));
        }
    }
}
