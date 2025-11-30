package com.github.smallinger.copperagebackport.client.gui;

import com.github.smallinger.copperagebackport.Constants;
import com.github.smallinger.copperagebackport.client.gui.options.Option;
import com.github.smallinger.copperagebackport.client.gui.options.OptionGroup;
import com.github.smallinger.copperagebackport.client.gui.options.OptionPage;
import com.github.smallinger.copperagebackport.client.gui.options.control.ControlElement;
import com.github.smallinger.copperagebackport.client.gui.widget.Dim2i;
import com.github.smallinger.copperagebackport.client.gui.widget.FlatButtonWidget;
import com.github.smallinger.copperagebackport.client.gui.widget.ScrollBarWidget;
import com.github.smallinger.copperagebackport.client.gui.widget.SearchFieldWidget;
import com.github.smallinger.copperagebackport.config.CommonConfig;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Config screen with sidebar navigation and search.
 */
public class ConfigScreen extends Screen {

    private static final int SIDEBAR_WIDTH = 110;
    private static final int OPTION_WIDTH = 240;
    private static final int SCROLLBAR_WIDTH = 8;

    private final List<OptionPage> pages = new ArrayList<>();
    private final List<ControlElement<?>> controls = new ArrayList<>();

    private final Screen prevScreen;

    private OptionPage currentPage;

    private FlatButtonWidget applyButton, closeButton, undoButton;
    private FlatButtonWidget donateButton, hideDonateButton;
    private FlatButtonWidget discordButton;
    private SearchFieldWidget searchField;
    private ScrollBarWidget scrollBar;

    private String searchQuery = "";
    private boolean hasPendingChanges;
    private ControlElement<?> hoveredElement;
    
    private int scrollOffset = 0;
    private int optionsAreaY;
    private int optionsAreaHeight;
    private int totalContentHeight;

    public ConfigScreen(Screen prevScreen) {
        super(Component.translatable("config.copperagebackport.title"));

        this.prevScreen = prevScreen;

        // Add option pages
        this.pages.addAll(ConfigOptions.createPages());
    }

    public static ConfigScreen create(Screen parent) {
        return new ConfigScreen(parent);
    }

    public void setPage(OptionPage page) {
        this.currentPage = page;
        this.scrollOffset = 0; // Reset scroll when changing pages
        this.rebuildGUI();
    }

    @Override
    protected void init() {
        super.init();
        this.rebuildGUI();
    }

    private void rebuildGUI() {
        this.controls.clear();
        this.clearWidgets();

        if (this.currentPage == null) {
            if (this.pages.isEmpty()) {
                throw new IllegalStateException("No pages are available?!");
            }
            this.currentPage = this.pages.get(0);
        }

        // Calculate layout dimensions
        int margin = 10;
        int topBarHeight = 26;
        int bottomBarHeight = 36;
        
        // Main content area
        int contentY = topBarHeight + margin;
        int contentHeight = this.height - contentY - bottomBarHeight;
        
        // Donate button in top right
        int donateWidth = this.font.width(Component.translatable("config.copperagebackport.donate")) + 14;
        this.donateButton = new FlatButtonWidget(new Dim2i(this.width - donateWidth - 28, 4, donateWidth, 20), 
            Component.translatable("config.copperagebackport.donate"), this::openDonatePage);
        // X button closes the screen
        this.hideDonateButton = new FlatButtonWidget(new Dim2i(this.width - 24, 4, 20, 20), 
            Component.literal("x"), this::closeScreen);
        
        // Search field at top - full width minus donate buttons
        int searchWidth = this.width - donateWidth - 32 - margin;
        this.searchField = new SearchFieldWidget(new Dim2i(margin, 4, searchWidth, 20),
            Component.translatable("config.copperagebackport.search"));
        this.searchField.setResponder(this::onSearchChanged);
        
        this.addRenderableWidget(this.searchField);
        this.addRenderableWidget(this.donateButton);
        this.addRenderableWidget(this.hideDonateButton);

        this.rebuildGUIPages(contentY, contentHeight);
        this.rebuildGUIOptions(contentY, contentHeight);

        // Bottom buttons
        this.undoButton = new FlatButtonWidget(new Dim2i(this.width - 211, this.height - 30, 65, 20), 
            Component.translatable("config.copperagebackport.reset"), this::undoChanges);
        this.applyButton = new FlatButtonWidget(new Dim2i(this.width - 142, this.height - 30, 65, 20), 
            Component.translatable("config.copperagebackport.apply"), this::applyChanges);
        this.closeButton = new FlatButtonWidget(new Dim2i(this.width - 73, this.height - 30, 65, 20), 
            Component.translatable("gui.done"), this::onClose);

        this.addRenderableWidget(this.undoButton);
        this.addRenderableWidget(this.applyButton);
        this.addRenderableWidget(this.closeButton);
    }

    private void onSearchChanged(String query) {
        this.searchQuery = query.toLowerCase(Locale.ROOT);
        this.scrollOffset = 0; // Reset scroll when search changes
        // When searching, rebuild options
        int margin = 10;
        int topBarHeight = 26;
        int bottomBarHeight = 36;
        int contentY = topBarHeight + margin;
        int contentHeight = this.height - contentY - bottomBarHeight;
        this.rebuildGUIOptions(contentY, contentHeight);
    }

    private void rebuildGUIPages(int contentY, int contentHeight) {
        // Sidebar - vertical category buttons on the left
        int x = 10;
        int y = contentY;

        for (OptionPage page : this.pages) {
            FlatButtonWidget button = new FlatButtonWidget(new Dim2i(x, y, SIDEBAR_WIDTH - 12, 18), page.getName(), () -> this.setPage(page));
            button.setSelected(this.currentPage == page);

            y += 20;

            this.addRenderableWidget(button);
        }
        
        // Discord button at the bottom (same height as Done/Apply/Reset buttons)
        this.discordButton = new FlatButtonWidget(new Dim2i(x, this.height - 30, SIDEBAR_WIDTH - 12, 20), 
            Component.translatable("config.copperagebackport.discord"), this::openDiscordPage);
        this.discordButton.setBorderColor(0xFF5865F2); // Discord blurple color
        this.addRenderableWidget(this.discordButton);
    }

    private void rebuildGUIOptions(int contentY, int contentHeight) {
        // Remove old control widgets
        for (ControlElement<?> control : this.controls) {
            this.removeWidget(control);
        }
        this.controls.clear();
        
        // Remove old scrollbar
        if (this.scrollBar != null) {
            this.removeWidget(this.scrollBar);
        }
        
        // Store options area dimensions
        this.optionsAreaY = contentY;
        this.optionsAreaHeight = contentHeight;

        // Options area - right of sidebar
        int x = SIDEBAR_WIDTH + 16;
        int y = contentY - this.scrollOffset;
        int optionWidth = this.width - x - SCROLLBAR_WIDTH - 16;
        
        // First pass - calculate total height
        int calcY = 0;
        for (OptionGroup group : this.currentPage.getGroups()) {
            for (Option<?> option : group.getOptions()) {
                if (!this.searchQuery.isEmpty()) {
                    String optionName = option.getName().getString().toLowerCase(Locale.ROOT);
                    if (!optionName.contains(this.searchQuery)) {
                        continue;
                    }
                }
                calcY += 18;
            }
            calcY += 4;
        }
        this.totalContentHeight = calcY;

        // Second pass - create controls
        for (OptionGroup group : this.currentPage.getGroups()) {
            for (Option<?> option : group.getOptions()) {
                if (!this.searchQuery.isEmpty()) {
                    String optionName = option.getName().getString().toLowerCase(Locale.ROOT);
                    if (!optionName.contains(this.searchQuery)) {
                        continue;
                    }
                }

                var control = option.getControl();
                ControlElement<?> element = control.createElement(new Dim2i(x, y, optionWidth, 18));

                this.addRenderableWidget(element);
                this.controls.add(element);

                y += 18;
            }
            y += 4;
        }
        
        // Create scrollbar
        Dim2i scrollBarDim = new Dim2i(this.width - SCROLLBAR_WIDTH - 10, contentY, SCROLLBAR_WIDTH, contentHeight);
        Dim2i scrollAreaDim = new Dim2i(SIDEBAR_WIDTH + 10, contentY, this.width - SIDEBAR_WIDTH - 20, contentHeight);
        
        this.scrollBar = new ScrollBarWidget(scrollBarDim, this.totalContentHeight, contentHeight, this::onScroll, scrollAreaDim);
        this.scrollBar.setOffset(this.scrollOffset);
        this.addRenderableWidget(this.scrollBar);
    }
    
    private void onScroll(int newOffset) {
        this.scrollOffset = newOffset;
        // Update control positions based on scroll offset
        updateControlPositions();
    }
    
    private void updateControlPositions() {
        int x = SIDEBAR_WIDTH + 16;
        int y = this.optionsAreaY - this.scrollOffset;
        int optionWidth = this.width - x - SCROLLBAR_WIDTH - 16;
        
        int controlIndex = 0;
        for (OptionGroup group : this.currentPage.getGroups()) {
            for (Option<?> option : group.getOptions()) {
                if (!this.searchQuery.isEmpty()) {
                    String optionName = option.getName().getString().toLowerCase(Locale.ROOT);
                    if (!optionName.contains(this.searchQuery)) {
                        continue;
                    }
                }
                
                if (controlIndex < this.controls.size()) {
                    ControlElement<?> control = this.controls.get(controlIndex);
                    control.setDim(new Dim2i(x, y, optionWidth, 18));
                    controlIndex++;
                }
                
                y += 18;
            }
            y += 4;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        this.updateControls();

        // Render all widgets normally first (except controls)
        super.render(graphics, mouseX, mouseY, delta);
        
        // Then render controls with scissoring to clip scrolled content
        if (this.optionsAreaHeight > 0) {
            int scissorX = SIDEBAR_WIDTH + 10;
            int scissorY = this.optionsAreaY;
            int scissorWidth = this.width - SIDEBAR_WIDTH - 20;
            int scissorHeight = this.optionsAreaHeight;
            
            graphics.enableScissor(scissorX, scissorY, scissorX + scissorWidth, scissorY + scissorHeight);
            
            // Render controls again (will be clipped) - this overdraws but ensures proper clipping
            for (ControlElement<?> control : this.controls) {
                control.render(graphics, mouseX, mouseY, delta);
            }
            
            graphics.disableScissor();
        }

        if (this.hoveredElement != null) {
            this.renderOptionTooltip(graphics, this.hoveredElement);
        }
    }

    private void updateControls() {
        ControlElement<?> hovered = this.getActiveControls()
                .filter(ControlElement::isHovered)
                .findFirst()
                .orElse(this.getActiveControls()
                        .filter(ControlElement::isFocused)
                        .findFirst()
                        .orElse(null));

        boolean hasChanges = this.getAllOptions()
                .anyMatch(Option::hasChanged);

        this.applyButton.setEnabled(hasChanges);
        this.undoButton.setVisible(hasChanges);
        this.closeButton.setEnabled(!hasChanges);

        this.hasPendingChanges = hasChanges;
        this.hoveredElement = hovered;
    }

    private Stream<Option<?>> getAllOptions() {
        return this.pages.stream()
                .flatMap(s -> s.getAllOptions().stream());
    }

    private Stream<ControlElement<?>> getActiveControls() {
        return this.controls.stream();
    }

    private void renderOptionTooltip(GuiGraphics graphics, ControlElement<?> element) {
        int textPadding = 3;
        int boxPadding = 3;

        Dim2i dim = element.getDim();
        
        // Position tooltip BELOW the element
        int boxX = dim.x();
        int boxY = dim.getLimitY() + boxPadding;

        // Tooltip width matches the element width
        int boxWidth = dim.width();

        Option<?> option = element.getOption();
        if (option.getTooltip() == null) return;
        
        var splitWidth = boxWidth - (textPadding * 2);
        List<FormattedCharSequence> tooltip = new ArrayList<>(this.font.split(option.getTooltip(), splitWidth));

        int boxHeight = (tooltip.size() * 12) + boxPadding;
        int boxYLimit = boxY + boxHeight;
        int boxYCutoff = this.height - 40;

        // If the box is going to be cutoff on the Y-axis, show it ABOVE the element instead
        if (boxYLimit > boxYCutoff) {
            boxY = dim.y() - boxHeight - boxPadding;
        }

        // Push pose and translate to higher Z level so tooltip renders on top of everything
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        
        // Draw tooltip background
        graphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xE0000000);
        
        // Draw tooltip border (same color as active category)
        int borderColor = 0xFF94E4D3;
        graphics.fill(boxX, boxY, boxX + boxWidth, boxY + 1, borderColor); // Top
        graphics.fill(boxX, boxY + boxHeight - 1, boxX + boxWidth, boxY + boxHeight, borderColor); // Bottom
        graphics.fill(boxX, boxY, boxX + 1, boxY + boxHeight, borderColor); // Left
        graphics.fill(boxX + boxWidth - 1, boxY, boxX + boxWidth, boxY + boxHeight, borderColor); // Right

        for (int i = 0; i < tooltip.size(); i++) {
            graphics.drawString(this.font, tooltip.get(i), boxX + textPadding, boxY + textPadding + (i * 12), 0xFFFFFFFF);
        }
        
        graphics.pose().popPose();
    }

    private void applyChanges() {
        this.getAllOptions().forEach(option -> {
            if (option.hasChanged()) {
                option.applyChanges();
            }
        });

        CommonConfig.save();
    }

    private void undoChanges() {
        this.getAllOptions().forEach(Option::reset);
    }

    private void openDonatePage() {
        if (Util.getPlatform() != null) {
            Util.getPlatform().openUri("https://ko-fi.com/smallinger");
        }
    }

    private void openDiscordPage() {
        if (Util.getPlatform() != null) {
            Util.getPlatform().openUri("https://discord.gg/hGrWUW9vSb");
        }
    }

    private void closeScreen() {
        this.minecraft.setScreen(this.prevScreen);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.prevScreen);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !this.hasPendingChanges;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.scrollBar != null && this.scrollBar.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}