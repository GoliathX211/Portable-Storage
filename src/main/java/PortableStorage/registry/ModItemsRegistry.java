package PortableStorage.registry;

import PortableStorage.items.BasicPouchItem;
import PortableStorage.items.DeepPouchItem;
import PortableStorage.items.TackleboxItem;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;

public class ModItemsRegistry {
    public static void RegisterAll() {
        /* Register everything here! */
        ItemRegistry.registerItem(
                "SmallBag",
                new BasicPouchItem(10, Item.Rarity.COMMON),
                50.0f,
                true
        );
        ItemRegistry.registerItem(
                "MediumBag",
                new BasicPouchItem(20, Item.Rarity.UNCOMMON),
                100.0f,
                true
        );
        ItemRegistry.registerItem(
                "LargeBag",
                new BasicPouchItem(40, Item.Rarity.RARE),
                200.0f,
                true
        );
        ItemRegistry.registerItem(
                "ExtraLargeBag",
                new BasicPouchItem(60, Item.Rarity.EPIC),
                500.0f,
                true
        );
        ItemRegistry.registerItem(
                "DeepBag",
                new DeepPouchItem(10, 50, Item.Rarity.UNIQUE),
                250.0f,
                true
        );
        ItemRegistry.registerItem(
                "Tacklebox",
                new TackleboxItem(Item.Rarity.COMMON),
                50.0f,
                true
        );
    }
}
