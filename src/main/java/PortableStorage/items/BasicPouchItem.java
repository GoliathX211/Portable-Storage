package PortableStorage.items;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.inventory.InventoryAddConsumer;
import necesse.level.maps.Level;

public class BasicPouchItem extends PouchItem {
    protected final int size;
    protected final boolean enable_pickup;
    public BasicPouchItem(int size, Rarity rarity){
        this(size, rarity, false);
    }

    public BasicPouchItem(int size, Rarity rarity, boolean pickup){
        this.rarity = rarity;
        this.size = size;
        this.enable_pickup = pickup;
        drawStoredItems = false;
    }
    /*
    ITEM_INVENTORY_CONTAINER = registerContainer((client, uniqueSeed, packet) -> {
            return new ItemInventoryContainerForm(client, new ItemInventoryContainer(client.getClient(), uniqueSeed, packet));
        }, (client, uniqueSeed, packet, serverObject) -> {
            return new ItemInventoryContainer(client, uniqueSeed, packet);
    */

    /*
    this.renameTip = new LocalMessage("ui", "renamebutton");
        if (oeInventory.canSetInventoryName()) {
            this.edit = (FormContentIconButton)this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.field_161.container_rename), new GameMessage[]{this.renameTip}));
            this.edit.onClicked((e) -> {
                this.label.setTyping(!this.label.isTyping());
                this.runEditUpdate();
            });
        }
    */
    /*
    @Override
    protected void openContainer(ServerClient client, int slotIndex) {
        PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.ITEM_INVENTORY_CONTAINER, ItemInventoryContainer.getContainerContent(this, slotIndex));
        ContainerRegistry.openAndSendContainer(client, p);
    }
    */

    @Override
    public boolean inventoryAddItem(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem input, String purpose, boolean isValid, int stackLimit, boolean combineIsValid, InventoryAddConsumer addConsumer) {
      if (isValidAddItem(input))
        if (isValidPurpose(this.insertPurposes, this.isInsertPurposesBlacklist, purpose)) {
          Inventory internalInventory = getInternalInventory(me);
          boolean success = (!this.enable_pickup && purpose.equals("itempickup")) ? internalInventory.addItemOnlyCombine(level, player, input, 0, internalInventory.getSize()-1, false, purpose, false, false, addConsumer) : internalInventory.addItem(level, player, input, purpose, addConsumer);
          if (success) {
            saveInternalInventory(me, internalInventory);
          } 
          return success;
        }  
      return false;
    }

    @Override
    public boolean onCombine(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem other, int maxStackSize, int amount, boolean combineIsNew, String purpose, InventoryAddConsumer addConsumer) {
      boolean valid = false;
      if (isValidPurpose(this.combinePurposes, this.isCombinePurposesBlacklist, purpose))
        if (purpose.equals("lootall")) {
          valid = isValidAddItem(other);
        } else {
          valid = isValidPouchItem(other);
        }  
      if (valid) {
        Inventory internalInventory = getInternalInventory(me);
        if (purpose.equals("restockfrom")) {
          if (internalInventory.restockFrom(level, player, other, 0, internalInventory.getSize(), purpose, false, addConsumer)) {
            saveInternalInventory(me, internalInventory);
            return true;
          } 
          return false;
        } 
        int startAmount = Math.min(amount, other.getAmount());
        InventoryItem copy = other.copy(startAmount);
        if (!this.enable_pickup && purpose.equals("lootall"))
          internalInventory.addItemOnlyCombine(level, player, copy, 0, internalInventory.getSize()-1, false, "pouchinsert", false, false, addConsumer);
        else
          internalInventory.addItem(level, player, copy, "pouchinsert", addConsumer);

        if (copy.getAmount() != startAmount) {
          int diff = startAmount - copy.getAmount();
          other.setAmount(other.getAmount() - diff);
          saveInternalInventory(me, internalInventory);
          return true;
        } 
      } 
      return false;
    }

    @Override
    public Inventory getInternalInventory(InventoryItem item) {
        GNDItem gndItem = item.getGndData().getItem("inventory");
        Inventory inventory = new Inventory(this.getInternalInventorySize()) {
            @Override
            public boolean canLockItem(int slot) {
                return true;
            }
        };

        if (gndItem instanceof GNDItemInventory) {
            GNDItemInventory gndInventory = (GNDItemInventory)gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
            }

            inventory.override(gndInventory.inventory);
        }
        item.getGndData().setItem("inventory", new GNDItemInventory(inventory));
        return inventory;

    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "bagSize", "size", this.size));
        tooltips.add(Localization.translate("itemtooltip", "storedItems", "items", this.getStoredItemAmounts(item)));
        return tooltips;
    }


    @Override
    public boolean isValidPouchItem(InventoryItem var1) {
        return !(var1.item instanceof PouchItem);
    }

    @Override
    public boolean isValidRequestItem(Item var1) {
        return !(var1 instanceof PouchItem);
    }

    /*
    @Override
    public boolean isValidRequestItem(Item var1) {
        if (!(var1 instanceof PouchItem) && )
    }
    */
    @Override
    public boolean isValidRequestType(Type var1) {
        return true;
    }

    @Override
    public int getInternalInventorySize() {
        return size;
    }
}
