package PortableStorage.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.PacketReader;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = FishingRodItem.class, name = "onPlace", arguments = {Level.class, int.class, int.class, PlayerMob.class, InventoryItem.class, PacketReader.class})
public class FishingRodItemOnPlacePatch {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean onEntry(@Advice.This FishingRodItem fishingRodItem,
                                  @Advice.Argument(0) Level level,
                                  @Advice.Argument(1) int x,
                                  @Advice.Argument(2) int y,
                                  @Advice.Argument(3) PlayerMob player,
                                  @Advice.Argument(4) InventoryItem inventoryItem,
                                  @Advice.Argument(5) PacketReader packetReader) {
        return true;
    }
    @Advice.OnMethodExit()
    public static void onExit(@Advice.This FishingRodItem fishingRodItem,
                              @Advice.Argument(0) Level level,
                              @Advice.Argument(1) int x,
                              @Advice.Argument(2) int y,
                              @Advice.Argument(3) PlayerMob player,
                              @Advice.Argument(4) InventoryItem inventoryItem,
                              @Advice.Argument(5) PacketReader packetReader,
                              @Advice.Return(readOnly = false) InventoryItem ret) {
            if (level.isServerLevel()) {
                Item got = player.getInv().main.getFirstItem(level, player, Item.Type.BAIT, "fishingbait");
                BaitItem bait = (BaitItem) got;
                int removed = player.getInv().main.removeItems(level, player, got, 1, "fishingbait");
                if (removed >= 1 || bait == null) {
                    FishingEvent event = new FishingEvent(player, x, y, fishingRodItem, bait);
                    level.entityManager.addLevelEvent(event);
                }
            }
            ret = inventoryItem;
    }
}
