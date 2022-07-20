package PortableStorage.forms;

import PortableStorage.InventoryItem.PouchInventoryItem;
import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.presets.containerComponent.item.ItemInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import packets.PacketPouchInventoryNameUpdate;

import java.util.Optional;

public class PouchItemInventoryContainerForm<T extends PouchInventoryContainer> extends ItemInventoryContainerForm<T> {
    public Form inventoryForm;
    public FormLabelEdit label;
    public FormContentIconButton edit;
    public LocalMessage renameTip;

    public static TypeParser<?>[] getParsers(FontOptions fontOptions) {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions)};
    }
    protected PouchItemInventoryContainerForm(Client client, T container, int height) {
        super(client, container, height);
        PouchInventory pouchInventory = container.getPouchInventory();
        FontOptions labelOptions = new FontOptions(20);
        this.label = this.inventoryForm.addComponent(new FormLabelEdit("", labelOptions, Settings.UI.activeTextColor, 4, 4, 4, 50), -1000);
        this.label.onMouseChangedTyping((e) -> this.runEditUpdate());
        this.label.onSubmit((e) -> this.runEditUpdate());
        this.label.allowCaretSetTyping = pouchInventory.canSetInventoryName();
        this.label.allowItemAppend = true;
        this.label.setParsers(getParsers(labelOptions));
        this.label.setText(pouchInventory.getInventoryName(Optional.empty()).translate());
        FormFlow iconFlow = new FormFlow(this.inventoryForm.getWidth() - 4);
        this.renameTip = new LocalMessage("ui", "renamebutton");
        if (pouchInventory.canSetInventoryName()) {
            this.edit = this.inventoryForm.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.container_rename), this.renameTip));
            this.edit.onClicked((e) -> {
                this.label.setTyping(!this.label.isTyping());
                this.runEditUpdate();
            });
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

