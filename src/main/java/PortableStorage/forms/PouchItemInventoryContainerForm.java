package PortableStorage.forms;

import PortableStorage.container.PouchInventoryContainer;
import PortableStorage.events.PouchOpenStorageConfigEvent;
import PortableStorage.events.actions.*;
import PortableStorage.inventory.PouchInventory;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.*;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import packets.PacketPouchInventoryNameUpdate;

import java.util.Optional;

public class PouchItemInventoryContainerForm<T extends PouchInventoryContainer> extends ContainerForm<T> {
    private final ChangeAllowedPouchStorageAction changeAllowedStorage;
    private final ChangeLimitsPouchStorageAction changeLimitsStorage;
    private final PriorityLimitPouchStorageAction priorityLimitStorage;
    private final FullUpdatePouchStorageAction fullUpdateSettlementStorage;
    private final OpenPouchStorageConfigAction openPouchStorageConfig;
    public FormLabelEdit label;
    public FormContentIconButton edit;
    public LocalMessage renameTip;
    protected FormContainerSlot[] slots;
    private FormContentIconButton configureStorageButton;
    private boolean openStorageConfig;
    private FormSwitcher switcher;
    private PouchStorageConfigForm storageConfigForm;


    public static TypeParser<?>[] getParsers(FontOptions fontOptions) {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions)};
    }
    public PouchItemInventoryContainerForm(Client client, T container, int height) {
        super(client,408,  height, container);
        FormFlow iconFlow = new FormFlow(this.getWidth() - 4);
        InternalInventoryItemInterface pouchItem = container.inventoryitem;
        addButtons(container, iconFlow, pouchItem);
        this.addSlots();
        PouchInventory pouchInventory = container.getPouchInventory();
        FontOptions labelOptions = new FontOptions(20);
        this.label = this.addComponent(new FormLabelEdit("", labelOptions, Settings.UI.activeTextColor, 4, 4, iconFlow.next() - 8, 50), -1000);
        this.label.onMouseChangedTyping((e) -> this.runEditUpdate());
        this.label.onSubmit((e) -> this.runEditUpdate());
        this.label.allowCaretSetTyping = pouchInventory.canSetInventoryName();
        this.label.allowItemAppend = true;
        this.label.setParsers(getParsers(labelOptions));
        this.label.setText(pouchInventory.getInventoryName(Optional.of(container.inventoryItemSlot.getItem())).translate());
        this.renameTip = new LocalMessage("ui", "renamebutton");
        if (pouchInventory.canSetInventoryName()) {
            this.edit = this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.container_rename), this.renameTip));
            this.edit.onClicked((e) -> {
                this.label.setTyping(!this.label.isTyping());
                this.runEditUpdate();
            });
        }
        this.switcher = new FormSwitcher();
        addStorageConfigButton(this, iconFlow.next(-26) - 24, 4);
        container.onEvent(PouchOpenStorageConfigEvent.class, (event) -> {
            if (this.openStorageConfig) {
                this.setupConfigStorage(event);
            }

            this.updateConfigureButtons();
        });
        this.openPouchStorageConfig = container.openStorageConfigAction;
        this.changeAllowedStorage = container.registerAction(new ChangeAllowedPouchStorageAction(container));
        this.changeLimitsStorage = container.registerAction(new ChangeLimitsPouchStorageAction(container));
        this.priorityLimitStorage = container.registerAction(new PriorityLimitPouchStorageAction(container));
        this.fullUpdateSettlementStorage = container.registerAction(new FullUpdatePouchStorageAction(container));
    }


    private void addButtons(T container, FormFlow iconFlow, InternalInventoryItemInterface pouchItem) {
        if (pouchItem.canQuickStackInventory()) {
            createButton(iconFlow, new GameSprite(Settings.UI.inventory_quickstack_out), "inventoryquickstack", "inventoryquickstackinfo", container.quickStackButton);
            createButton(iconFlow, (new GameSprite(Settings.UI.container_loot_all)).mirrorY(), "inventorytransferall", "inventorytransferallinfo", container.transferAll);
        }

        if (pouchItem.canRestockInventory()) {
            createButton(iconFlow, Settings.UI.inventory_quickstack_in, "inventoryrestock", (e) -> {
                container.restockButton.runAndSend();
            });
        }
        createButton(iconFlow, Settings.UI.container_loot_all, "inventorylootall", (e) -> {
            container.lootButton.runAndSend();
        });
        if (pouchItem.canSortInventory()) {
            createButton(iconFlow, Settings.UI.inventory_sort, "inventorysort", (e) -> {
                container.sortButton.runAndSend();
            });
        }
    }

    private void createButton(FormFlow iconFlow, GameTexture UI, String nameTranslationKey, FormEventListener<FormInputEvent<FormButton>> action) {
        FormContentIconButton sortButton;
        sortButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(UI), new GameMessage[]{new LocalMessage("ui", nameTranslationKey)}));
        sortButton.onClicked(action);
        sortButton.setCooldown(500);
    }

    private void createButton(FormFlow iconFlow, GameSprite buttonIcon, String nameTranslationKey, String useTranslationKey, EmptyCustomAction action) {
        FormContentIconButton button;
        button = (FormContentIconButton)this.addComponent(new FormContentIconButton(iconFlow.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, buttonIcon, new GameMessage[0]) {
            public GameTooltips getTooltips() {
                StringTooltips tooltips = new StringTooltips(Localization.translate("ui", nameTranslationKey));
                if (!Screen.isKeyDown(340) && !Screen.isKeyDown(344)) {
                    tooltips.add(Localization.translate("ui", "shiftmoreinfo"), GameColor.LIGHT_GRAY);
                } else {
                    tooltips.add(Localization.translate("ui", useTranslationKey, "key", TypeParsers.getInputParseString(Control.INV_LOCK) + "+" + TypeParsers.getInputParseString(-100)), (GameColor)GameColor.LIGHT_GRAY, 400);
                }

                return tooltips;
            }
        });
        button.onClicked((e) -> {
            action.runAndSend();
        });
        button.setCooldown(500);
    }

    public void addStorageConfigButton(Form form, int x, int y) {
        this.configureStorageButton = (FormContentIconButton)form.addComponent(new FormContentIconButton(x, y, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.container_storage_add), new GameMessage[0]) {
            public GameTooltips getTooltips() {
                return new StringTooltips(Localization.translate("ui", "settlementconfigurestorage"));
            }
        });
        this.configureStorageButton.onClicked((e) -> {
            this.openStorageConfig = true;
            this.openPouchStorageConfig.runAndSend();
        });
        this.configureStorageButton.setCooldown(500);
        this.updateConfigureButtons();
    }
    protected void updateConfigureButtons() {
        this.configureStorageButton.setActive(true);
        this.configureStorageButton.setIconSprite(new GameSprite(Settings.UI.container_storage_config));
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
        PouchInventory pouchInventory = this.container.getPouchInventory();
        InventoryItem inventoryItem = this.container.inventoryItemSlot.getItem();
        int slot = this.container.inventoryItemSlot.getInventorySlot();

        if (pouchInventory.canSetInventoryName()) {
            if (this.label.isTyping()) {
                this.edit.setIconSprite(new GameSprite(Settings.UI.container_rename_save));
                this.renameTip = new LocalMessage("ui", "savebutton");
            } else {
                if (!this.label.getText().equals(pouchInventory.getInventoryName(Optional.of(inventoryItem)).toString())) {
                    pouchInventory.setInventoryName(this.label.getText(), inventoryItem);
                    this.client.network.sendPacket(new PacketPouchInventoryNameUpdate(this.label.getText(), slot));
                }

                this.edit.setIconSprite(new GameSprite(Settings.UI.container_rename));
                this.renameTip = new LocalMessage("ui", "renamebutton");
                this.label.setText(pouchInventory.getInventoryName(Optional.of(inventoryItem)).translate());
            }

            this.edit.setTooltips(this.renameTip);
        }
    }
    public void setupConfigStorage(final PouchOpenStorageConfigEvent update) {
        PouchInventory inventory = container.getPouchInventory();
        GameMessage name = inventory.getInventoryName(Optional.ofNullable(container.inventoryItemSlot.getItem()));
        PouchStorageConfigForm newStorageConfig = (PouchStorageConfigForm)this.addComponent(new PouchStorageConfigForm("storageConfig", 500, 350, inventory, name, update.filter, update.priority) {
            public void onItemsChanged(Item[] items, boolean allowed) {
                changeAllowedStorage.runAndSend(items, allowed);
            }

            public void onItemLimitsChanged(Item item, ItemCategoriesFilter.ItemLimits limits) {
                changeLimitsStorage.runAndSend(item, limits);
            }

            public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                changeAllowedStorage.runAndSend(category, allowed);
            }

            public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
                changeLimitsStorage.runAndSend(category, maxItems);
            }

            public void onFullChange(ItemCategoriesFilter filter, int priority) {
                fullUpdateSettlementStorage.runAndSend(filter, priority);
            }

            public void onPriorityChange(int priority) {
                priorityLimitStorage.runAndSendPriority(priority);
            }

            public void onLimitChange(ItemCategoriesFilter.ItemLimitMode mode, int limit) {
                priorityLimitStorage.runAndSendLimit(mode, limit);
            }

            @Override
            public void onRemove() {

            }

            @Override
            public void onBack() {

            }
        }/*, (form, active) -> {
            if (!active) {
                this.switcher.removeComponent(form);
                this.storageConfigForm = null;
                // this.manager.subscribeStorage.unsubscribe(subscriptionID);
            }

        }*/);
        this.switcher.makeCurrent(newStorageConfig);
        this.storageConfigForm = newStorageConfig;
        ContainerComponent.setPosFocus(this.storageConfigForm);
        this.openStorageConfig = false;
    }

}

