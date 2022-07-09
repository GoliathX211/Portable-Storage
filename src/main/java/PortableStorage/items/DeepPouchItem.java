package PortableStorage.items;

import PortableStorage.inventory.DeepPouchInventory;
import PortableStorage.registry.ModTextureRegistry;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDDeepItemInventory;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureAnim;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.HashMap;

public class DeepPouchItem extends BasicPouchItem {
    public static final long SPEED = 10 * 1000;
    public final int multiplicity;
    private GameTexture[] textures;

    public DeepPouchItem(int size, int multiplicity, Rarity rarity) {
        super(size, rarity);
        this.multiplicity = multiplicity;
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
        float proprotion = ((float) (System.currentTimeMillis() % SPEED)) / SPEED;
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
