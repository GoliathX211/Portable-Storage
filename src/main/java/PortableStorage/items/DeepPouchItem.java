package PortableStorage.items;

import PortableStorage.inventory.DeepPouchInventory;
import PortableStorage.registry.ModTextureRegistry;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDDeepItemInventory;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureAnim;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

import java.util.Arrays;

public class DeepPouchItem extends BasicPouchItem {
    public final int multiplicity;
    private final int speed;
    private GameTexture[] textures;

    public DeepPouchItem(int size, int multiplicity, Rarity rarity) {
        super(size, rarity);
        this.multiplicity = multiplicity;
        this.speed = GameRandom.globalRandom.getOneOf(1511, 1523, 1531, 1543, 1549, 1553, 1559, 1567, 1571, 1579, 1583, 1597, 1601, 1607, 1609, 1613, 1619, 1621, 1627, 1637, 1657, 1663, 1667, 1669, 1693, 1697, 1699, 1709, 1721, 1723, 1733, 1741, 1747, 1753, 1759, 1777, 1783, 1787, 1789, 1801, 1811, 1823, 1831, 1847, 1861, 1867, 1871, 1873, 1877, 1879, 1889, 1901, 1907, 1913, 1931, 1933, 1949, 1951, 1973, 1979, 1987, 1993, 1997, 1999, 2003, 2011, 2017, 2027, 2029, 2039, 2053, 2063, 2069, 2081, 2083, 2087, 2089, 2099, 2111, 2113, 2129, 2131, 2137, 2141, 2143, 2153, 2161, 2179);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "deepBagExplanation", "multiplier", multiplicity));
        return tooltips;
    }
    @Override
    public void loadTextures() {
        super.loadTextures();
        GameTexture DeepBag1 = GameTexture.fromFile("items/deepbag1");
        GameTexture DeepBag2 = GameTexture.fromFile("items/deepbag2");
        GameTexture DeepBag3 = GameTexture.fromFile("items/deepbag3");
        GameTexture DeepBag4 = GameTexture.fromFile("items/deepbag4");
        GameTexture DeepBag5 = GameTexture.fromFile("items/deepbag5");
        GameTexture DeepBag6 = GameTexture.fromFile("items/deepbag6");
        GameTexture DeepBag7 = GameTexture.fromFile("items/deepbag7");
        GameTexture DeepBag8 = GameTexture.fromFile("items/deepbag8");
        textures = new GameTexture[] {
                DeepBag1,
                DeepBag2,
                DeepBag3,
                DeepBag4,
                DeepBag5,
                DeepBag6,
                DeepBag7,
                DeepBag8,
        };
    }
    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        float proprotion = ((float) (System.currentTimeMillis() % speed)) / speed;
        int index = (int)(proprotion * (float)this.textures.length) % this.textures.length;
        GameTexture toDraw = this.textures[index];
        return new GameSprite(toDraw, 32);
    }

    @Override
    public DeepPouchInventory getInternalInventory(InventoryItem item) {
        GNDItem gndItem = item.getGndData().getItem("DeepInventory");
        if (gndItem instanceof GNDDeepItemInventory) {
            GNDDeepItemInventory gndInventory = (GNDDeepItemInventory) gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
            }
            return gndInventory.inventory;

        } else {
            DeepPouchInventory inventory = new DeepPouchInventory(this.getInternalInventorySize(), this.multiplicity);
            item.getGndData().setItem("DeepInventory", new GNDItemInventory(inventory));
            return inventory;
        }
    }

    @Override
    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        GNDItem gndItem = item.getGndData().getItem("DeepInventory");
        if (gndItem instanceof GNDDeepItemInventory) {
            GNDDeepItemInventory gndInventory = (GNDDeepItemInventory)gndItem;
            gndInventory.inventory.override((DeepPouchInventory) inventory, true, true);
        } else {
            item.getGndData().setItem("DeepInventory", new GNDDeepItemInventory((DeepPouchInventory) inventory));
        }
    }
}
