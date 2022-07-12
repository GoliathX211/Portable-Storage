package PortableStorage.patches;

import necesse.engine.GameLog;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.AnglerHumanMob;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import net.bytebuddy.asm.Advice;

import java.util.ArrayList;

// public ArrayList<ShopItem> getShopItems(VillageShopsData data, ServerClient client) {
@ModMethodPatch(
        target = AnglerHumanMob.class,
        name = "getShopItems",
        arguments = {VillageShopsData.class, ServerClient.class}
)
public class HumanShopAddItemPatch {
    @Advice.OnMethodExit
    public void onExit(
            @Advice.This AnglerHumanMob shopkeeper,
            @Advice.Argument(0) VillageShopsData data,
            @Advice.Argument(1) ServerClient client,
            @Advice.Return(readOnly=false) ArrayList<ShopItem> ret
    ) {
        GameRandom random = GameRandom.globalRandom;

        ShopItem.addStockedItem(ret, data, "Tacklebox", shopkeeper.getRandomHappinessPrice(random, 1500, 2000, 1750));
        GameLog.debug.println("post edit");
    }
}


