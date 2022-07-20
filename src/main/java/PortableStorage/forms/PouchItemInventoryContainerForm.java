package PortableStorage.forms;

import PortableStorage.InventoryItem.PouchInventoryItem;
import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.forms.presets.containerComponent.item.ItemInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import packets.PacketPouchInventoryNameUpdate;

import java.util.Optional;

public class PouchItemInventoryContainerForm<T extends PouchInventoryContainer> extends ContainerForm<T> {
    public FormLabelEdit label;
    public FormContentIconButton edit;
    public LocalMessage renameTip;
    protected FormContainerSlot[] slots;

    public static TypeParser<?>[] getParsers(FontOptions fontOptions) {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions)};
    }
    protected PouchItemInventoryContainerForm(Client client, T container, int height) {
        super(client,408,  height, container);
        FormFlow iconFlow = new FormFlow(this.getWidth() - 4);
        InternalInventoryItemInterface item = container.inventoryitem;
        FormContentIconButton lootAllButton;
        FormContentIconButton sortButton;
        if (item.canQuickStackInventory()) {
            lootAllButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.inventory_quickstack_out), new GameMessage[0]) {
                public GameTooltips getTooltips() {
                    StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "inventoryquickstack"));
                    if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
                        tooltips.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
                    } else {
                        tooltips.add(Localization.translate("ui", "inventoryquickstackinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), (GameColor)GameColor.LIGHT_GRAY, 400);
                    }

                    return tooltips;
                }
            });
            lootAllButton.onClicked((e) -> {
                container.quickStackButton.runAndSend();
            });
            lootAllButton.setCooldown(500);
            sortButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, (new GameSprite(Settings.UI.container_loot_all)).mirrorY(), new GameMessage[0]) {
                public GameTooltips getTooltips() {
                    StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "inventorytransferall"));
                    if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
                        tooltips.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
                    } else {
                        tooltips.add(Localization.translate("ui", "inventorytransferallinfo", "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), (GameColor)GameColor.LIGHT_GRAY, 400);
                    }

                    return tooltips;
                }
            });
            sortButton.onClicked((e) -> {
                container.transferAll.runAndSend();
            });
            sortButton.setCooldown(500);
        }

        if (item.canRestockInventory()) {
            lootAllButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.inventory_quickstack_in), new GameMessage[]{new LocalMessage("ui", "inventoryrestock")}));
            lootAllButton.onClicked((e) -> {
                container.restockButton.runAndSend();
            });
            lootAllButton.setCooldown(500);
        }

        lootAllButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.container_loot_all), new GameMessage[]{new LocalMessage("ui", "inventorylootall")}));
        lootAllButton.onClicked((e) -> {
            container.lootButton.runAndSend();
        });
        lootAllButton.setCooldown(500);
        if (item.canSortInventory()) {
            sortButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.inventory_sort), new GameMessage[]{new LocalMessage("ui", "inventorysort")}));
            sortButton.onClicked((e) -> {
                container.sortButton.runAndSend();
            });
            sortButton.setCooldown(500);
        }
        this.addSlots();
        PouchInventory pouchInventory = container.getPouchInventory();
        FontOptions labelOptions = new FontOptions(20);
        this.label = this.addComponent(new FormLabelEdit("", labelOptions, Settings.UI.activeTextColor, 4, 4, 4, 50), -1000);
        this.label.onMouseChangedTyping((e) -> this.runEditUpdate());
        this.label.onSubmit((e) -> this.runEditUpdate());
        this.label.allowCaretSetTyping = pouchInventory.canSetInventoryName();
        this.label.allowItemAppend = true;
        this.label.setParsers(getParsers(labelOptions));
        this.label.setText(pouchInventory.getInventoryName(Optional.empty()).toString());
        this.renameTip = new LocalMessage("ui", "renamebutton");
        if (pouchInventory.canSetInventoryName()) {
            this.edit = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.container_rename), this.renameTip));
            this.edit.onClicked((e) -> {
                this.label.setTyping(!this.label.isTyping());
                this.runEditUpdate();
            });
        }





    }


    protected static int getContainerHeight(int inventorySize, int columns) {
        return (inventorySize + columns - 1) / columns * 40 + 38;
    }

    protected void addSlots() {
        this.slots = new FormContainerSlot[((ItemInventoryContainer)this.container).INVENTORY_END - ((ItemInventoryContainer)this.container).INVENTORY_START + 1];

        for(int i = 0; i < this.slots.length; ++i) {
            int slotIndex = i + ((ItemInventoryContainer)this.container).INVENTORY_START;
            int x = i % 10;
            int y = i / 10;
            this.slots[i] = (FormContainerSlot)this.addComponent(new FormContainerSlot(this.client, slotIndex, 4 + x * 40, 4 + y * 40 + 30));
        }

    }
    public PouchItemInventoryContainerForm(Client client, T container) {
        this(client, container, getContainerHeight(container.inventory.getSize(), 10));
    }

    private void runEditUpdate() {
        PouchInventoryItem pouchInventoryItem;
        PouchInventory pouchInventory = this.container.getPouchInventory();
        InventoryItem inventoryItem = this.container.inventoryItemSlot.getItem();
        int slot = this.container.inventoryItemSlot.getInventorySlot();


        if (inventoryItem instanceof PouchInventoryItem) {
            pouchInventoryItem = (PouchInventoryItem) inventoryItem;
        }
        else {
            throw new RuntimeException("Oh shit");
        }

        if (pouchInventory.canSetInventoryName()) {
            if (this.label.isTyping()) {
                this.edit.setIconSprite(new GameSprite(Settings.UI.container_rename_save));
                this.renameTip = new LocalMessage("ui", "savebutton");
            } else {
                if (!this.label.getText().equals(pouchInventory.getInventoryName(Optional.of(pouchInventoryItem)).toString())) {
                    pouchInventory.setInventoryName(this.label.getText(), pouchInventoryItem);
                    this.client.network.sendPacket(new PacketPouchInventoryNameUpdate(pouchInventory, this.label.getText(), slot));
                }

                this.edit.setIconSprite(new GameSprite(Settings.UI.container_rename));
                this.renameTip = new LocalMessage("ui", "renamebutton");
                this.label.setText(pouchInventory.getInventoryName(Optional.of(pouchInventoryItem)).translate());
            }

            this.edit.setTooltips(this.renameTip);
        }
    }

}

