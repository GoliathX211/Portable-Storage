package PortableStorage.registry;

import PortableStorage.items.BasicPouchItem;
import PortableStorage.items.DeepPouchItem;
import PortableStorage.items.TackleboxItem;
import PortableStorage.items.TacoboxItem;
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
                new DeepPouchItem(
                        10,
                        10,
                        Item.Rarity.LEGENDARY,
                        new String[] {
                                "items/DeepBag/deepbag1",
                                "items/DeepBag/deepbag2",
                                "items/DeepBag/deepbag3",
                                "items/DeepBag/deepbag4",
                                "items/DeepBag/deepbag5",
                                "items/DeepBag/deepbag6",
                                "items/DeepBag/deepbag7",
                                "items/DeepBag/deepbag8"
                        }
                ),

                250.0f,
                true
        );
        ItemRegistry.registerItem(
                "DeeperBag",
                new DeepPouchItem(
                        15,
                        50,
                        Item.Rarity.UNIQUE,
                        new String[] {
                                "items/DeeperBag/deeperbag1",
                                "items/DeeperBag/deeperbag2",
                                "items/DeeperBag/deeperbag3",
                                "items/DeeperBag/deeperbag4",
                                "items/DeeperBag/deeperbag5",
                                "items/DeeperBag/deeperbag6",
                                "items/DeeperBag/deeperbag7",
                                "items/DeeperBag/deeperbag8"
                        }
                        ),
                500.0f,
                true
        );
        ItemRegistry.registerItem(
                "Tacklebox",
                new TackleboxItem(10, Item.Rarity.COMMON),
                50.0f,
                true
        );
        ItemRegistry.registerItem(
                "TacoBox",
                new TacoboxItem(1, Item.Rarity.UNIQUE),
                69.0f,
                true
        );
    }
}
