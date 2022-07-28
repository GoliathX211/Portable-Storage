package PortableStorage.forms;

import necesse.engine.*;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.*;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.Inventory;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

import java.awt.*;
import java.awt.dnd.DropTarget;

public abstract class PouchStorageConfigForm extends Form {
    public final ItemCategoriesFilter filter;
    private ClipboardTracker<ConfigData> listClipboard;
    private static FormContentIconButton pasteButton;
    public final ItemCategoriesFilterForm filterForm;
    public final FormDropdownSelectionButton<Integer> prioritySelect;
    public final FormDropdownSelectionButton<ItemCategoriesFilter.ItemLimitMode> limitMode;
    public final FormTextInput limitInput;

    public PouchStorageConfigForm(String name, int width, int height, Inventory inventory, GameMessage header, ItemCategoriesFilter filter, int currentPriority) {
        super(name, width, height);
        this.filter = filter;
        FormFlow flow = new FormFlow(5);
        if (header != null) {
            this.addComponent(new FormLocalLabel(header, new FontOptions(20), -1, 5, flow.next(30)));
        }

        int priorityY = flow.next(28);
        this.prioritySelect = (FormDropdownSelectionButton)this.addComponent(new FormDropdownSelectionButton(4, priorityY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getWidth() - 8));
        this.prioritySelect.options.add(300, this.getPriorityText(300));
        this.prioritySelect.options.add(200, this.getPriorityText(200));
        this.prioritySelect.options.add(100, this.getPriorityText(100));
        this.prioritySelect.options.add(0, this.getPriorityText(0));
        this.prioritySelect.options.add(-100, this.getPriorityText(-100));
        this.prioritySelect.options.add(-200, this.getPriorityText(-200));
        this.prioritySelect.options.add(-300, this.getPriorityText(-300));
        this.updatePrioritySelect(currentPriority);
        this.prioritySelect.onSelected((e) -> {
            this.onPriorityChange((Integer)e.value);
        });
        int limitY = flow.next(28);
        this.limitMode = (FormDropdownSelectionButton)this.addComponent(new FormDropdownSelectionButton(4, limitY, FormInputSize.SIZE_24, ButtonColor.BASE, this.getWidth() / 2 - 6));
        ItemCategoriesFilter.ItemLimitMode[] var12 = ItemCategoriesFilter.ItemLimitMode.values();
        int contentY = var12.length;

        int contentHeight;
        for(contentHeight = 0; contentHeight < contentY; ++contentHeight) {
            ItemCategoriesFilter.ItemLimitMode value = var12[contentHeight];
            this.limitMode.options.add(value, value.displayName, value.tooltip == null ? null : () -> {
                return value.tooltip;
            });
        }

        this.updateLimitMode();
        this.limitInput = (FormTextInput)this.addComponent(new FormTextInput(this.getWidth() / 2 + 2, limitY, FormInputSize.SIZE_24, this.getWidth() / 2 - 6, 7));
        this.updateLimitInput();
        this.limitInput.setRegexMatchFull("([0-9]+)?");
        this.limitInput.rightClickToClear = true;
        this.limitInput.onSubmit((e) -> {
            try {
                int next;
                if (this.limitInput.getText().isEmpty()) {
                    next = Integer.MAX_VALUE;
                } else {
                    next = Integer.parseInt(this.limitInput.getText());
                }

                if (filter.maxAmount != next) {
                    filter.maxAmount = next;
                    this.updateLimitInput();
                    this.onLimitChange(filter.limitMode, filter.maxAmount);
                }
            } catch (NumberFormatException var4) {
                this.updateLimitInput();
            }

        });
        this.limitMode.onSelected((e) -> {
            if (filter.limitMode != e.value) {
                filter.limitMode = (ItemCategoriesFilter.ItemLimitMode)e.value;
                this.limitInput.tooltip = filter.limitMode.inputPlaceholder;
                this.limitInput.placeHolder = filter.limitMode.inputPlaceholder;
                this.onLimitChange(filter.limitMode, filter.maxAmount);
            }

        });
        int searchY = flow.next(28);
        contentY = flow.next();
        contentHeight = height - contentY - 32;
        final FormContentBox filterContent = (FormContentBox)this.addComponent(new FormContentBox(0, contentY, this.getWidth(), contentHeight));
        ItemCategoryExpandedSetting expandedSetting = Settings.getItemCategoryExpandedSetting(name);
        this.filterForm = (ItemCategoriesFilterForm)filterContent.addComponent(new ItemCategoriesFilterForm(4, 28, filter, true, expandedSetting, true) {
            public void onDimensionsChanged(int width, int height) {
                filterContent.setContentBox(new Rectangle(0, 0, Math.max(PouchStorageConfigForm.this.getWidth(), width), this.getY() + height));
            }

            public void onItemsChanged(Item[] items, boolean allowed) {
                PouchStorageConfigForm.this.onItemsChanged(items, allowed);
            }

            public void onItemLimitsChanged(Item item, ItemCategoriesFilter.ItemLimits limits) {
                PouchStorageConfigForm.this.onItemLimitsChanged(item, limits);
            }

            public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                PouchStorageConfigForm.this.onCategoryChanged(category, allowed);
            }

            public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
                PouchStorageConfigForm.this.onCategoryLimitsChanged(category, maxItems);
            }
        });
        ((FormLocalTextButton)filterContent.addComponent(new FormLocalTextButton("ui", "allowallbutton", 4, 0, this.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((e) -> {
            if (!this.filterForm.filter.master.isAllAllowed() || !this.filterForm.filter.master.isAllDefault()) {
                this.filterForm.filter.master.setAllowed(true);
                this.filterForm.updateAllButtons();
                this.onCategoryChanged(this.filterForm.filter.master, true);
            }

        });
        ((FormLocalTextButton)filterContent.addComponent(new FormLocalTextButton("ui", "clearallbutton", this.getWidth() / 2 + 2, 0, this.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((e) -> {
            if (this.filterForm.filter.master.isAnyAllowed()) {
                this.filterForm.filter.master.setAllowed(false);
                this.filterForm.updateAllButtons();
                this.onCategoryChanged(this.filterForm.filter.master, false);
            }

        });
        FormTextInput searchInput = (FormTextInput)this.addComponent(new FormTextInput(4, searchY, FormInputSize.SIZE_24, this.getWidth() - 28 - 28 - 28 - 8, -1, 500));
        searchInput.placeHolder = new LocalMessage("ui", "searchtip");
        searchInput.onChange((e) -> {
            this.filterForm.setSearch(searchInput.getText());
        });
        ((FormContentIconButton)this.addComponent(new FormContentIconButton(this.getWidth() - 28, searchY, FormInputSize.SIZE_24, ButtonColor.RED, new GameSprite(Settings.UI.container_storage_remove), new GameMessage[]{new LocalMessage("ui", "settlementremovestorage")}))).onClicked((e) -> {
            this.onRemove();
        });
        this.pasteButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(this.getWidth() - 28 - 28, searchY, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.paste_button), new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
        this.pasteButton.onClicked((e) -> {
            ConfigData data = (ConfigData)this.listClipboard.getValue();
            if (data != null && (filter.loadFromCopy(data.filter) || (Integer)this.prioritySelect.getSelected() != data.priority)) {
                this.filterForm.updateAllButtons();
                this.updatePrioritySelect(data.priority);
                this.updateLimitMode();
                this.updateLimitInput();
                this.onFullChange(filter, data.priority);
            }

        });
        ((FormContentIconButton)this.addComponent(new FormContentIconButton(this.getWidth() - 28 - 28 - 28, searchY, FormInputSize.SIZE_24, ButtonColor.BASE, new GameSprite(Settings.UI.copy_button), new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((e) -> {
            SaveData save = (new ConfigData((Integer)this.prioritySelect.getSelected(), filter)).getSaveData();
            Screen.putClipboard(save.getScript());
            this.listClipboard.forceUpdate();
        });
        this.listClipboard = new ClipboardTracker<ConfigData>() {
            public ConfigData parse(String clipboard) {
                try {
                    return new ConfigData(new LoadData(clipboard));
                } catch (Exception var3) {
                    return null;
                }
            }

            public void onUpdate(ConfigData value) {
                PouchStorageConfigForm.pasteButton.setActive(value != null);
            }
        };
        ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "backbutton", width / 2 - 4, this.getHeight() - 28, width / 2, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((e) -> {
            this.onBack();
        });
    }

    public void updatePrioritySelect(int priority) {
        this.prioritySelect.setSelected(priority, this.getPriorityText(priority));
    }

    public void updateLimitMode() {
        this.limitMode.setSelected(this.filter.limitMode, this.filter.limitMode.displayName);
    }

    public void updateLimitInput() {
        this.limitInput.tooltip = this.filter.limitMode.inputPlaceholder;
        this.limitInput.placeHolder = this.filter.limitMode.inputPlaceholder;
        if (!this.limitInput.isTyping()) {
            if (this.filter.maxAmount != Integer.MAX_VALUE) {
                this.limitInput.setText(String.valueOf(this.filter.maxAmount));
            } else {
                this.limitInput.setText("");
            }
        }

    }

    private GameMessage getPriorityText(int priority) {
        if (priority == 300) {
            return new LocalMessage("ui", "prioritytop");
        } else if (priority == 200) {
            return new LocalMessage("ui", "priorityhigh");
        } else if (priority == 100) {
            return new LocalMessage("ui", "priorityhigher");
        } else if (priority == 0) {
            return new LocalMessage("ui", "prioritynormal");
        } else if (priority == -100) {
            return new LocalMessage("ui", "prioritylower");
        } else if (priority == -200) {
            return new LocalMessage("ui", "prioritylow");
        } else {
            return (GameMessage)(priority == -300 ? new LocalMessage("ui", "prioritylast") : new StaticMessage("" + priority));
        }
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.listClipboard.update();
        super.draw(tickManager, perspective, renderBox);
    }

    public abstract void onItemsChanged(Item[] var1, boolean var2);

    public abstract void onItemLimitsChanged(Item var1, ItemCategoriesFilter.ItemLimits var2);

    public abstract void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2);

    public abstract void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1, int var2);

    public abstract void onFullChange(ItemCategoriesFilter var1, int var2);

    public abstract void onPriorityChange(int var1);

    public abstract void onLimitChange(ItemCategoriesFilter.ItemLimitMode var1, int var2);

    public abstract void onRemove();

    public abstract void onBack();

    private static class ConfigData {
        public final int priority;
        public final ItemCategoriesFilter filter;

        public ConfigData(int priority, ItemCategoriesFilter filter) {
            this.priority = priority;
            this.filter = filter;
        }

        public ConfigData(LoadData save) {
            this.priority = save.getInt("priority", 0, false);
            this.filter = new ItemCategoriesFilter(false);
            this.filter.applyLoadData(save.getFirstLoadDataByName("filter"));
        }

        public SaveData getSaveData() {
            SaveData save = new SaveData("config");
            save.addInt("priority", this.priority);
            SaveData filtersSave = new SaveData("filter");
            this.filter.addSaveData(filtersSave);
            save.addSaveData(filtersSave);
            return save;
        }
    }
}
