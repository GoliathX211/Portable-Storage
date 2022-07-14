package PortableStorage.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.AnglerHumanMob;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import net.bytebuddy.asm.Advice;

import java.util.ArrayList;

@ModMethodPatch(target = AnglerHumanMob.class, name = "getShopItems", arguments = {VillageShopsData.class, ServerClient.class})
public class getShopItemsPatch {
    @Advice.OnMethodExit()
    static ArrayList<ShopItem> onExit(
            @Advice.This AnglerHumanMob angler,
            @Advice.Return(readOnly = false) ArrayList<ShopItem> list
    ) {
        GameRandom random = GameRandom.globalRandom;
        if (angler.isTravelingHuman()) {
            return null;
        }
        else {
            list.add(ShopItem.item(
                    "Tacklebox",
                    angler.getRandomHappinessPrice(
                            random,
                            1500,
                            2000,
                            1750)
            ));
            return list;
        }
    }
}


// Kodi022 Implementation
// @ModMethodPatch(target = TravelingMerchantMob.class, name = "getShopItems", arguments = {VillageShopsData.class, ServerClient.class})
// public class SilverTrade {
//     @Advice.OnMethodExit()
//     static void onExit(@Advice.This TravelingMerchantMob merch,
//                         @Advice.Return(readOnly = false) ArrayList<ShopItem> list) {
//         list.add(ShopItem.item("silveroreitem", 18));
//     }
// }