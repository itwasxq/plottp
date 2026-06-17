package com.panda.gardenplotbuttons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GardenPlotButtonsClient implements ClientModInitializer {
    public static final String MOD_ID = "gardenplotbuttons";
    public static final Logger LOGGER = LoggerFactory.getLogger("GardenPlotButtons");

    @Override
    public void onInitializeClient() {
        GardenPlotConfig.load();

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (isConfigurePlotsScreen(screen) && screen instanceof ContainerScreen containerScreen) {
                ScreenEvents.remove(screen).register(removedScreen -> parseConfigurePlots(containerScreen));
                return;
            }

            if (screen instanceof InventoryScreen inventoryScreen) {
                GardenPlotWidget widget = new GardenPlotWidget(GardenPlotConfig.getX(scaledWidth), GardenPlotConfig.getY(scaledHeight));
                Screens.getWidgets(inventoryScreen).add(widget);
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> GardenPlotConfig.save());
        LOGGER.info("Garden Plot Buttons loaded successfully!");
    }

    private static boolean isConfigurePlotsScreen(Screen screen) {
        String title = ChatFormatting.stripFormatting(screen.getTitle().getString());
        return title != null && title.trim().equalsIgnoreCase("Configure Plots");
    }

    private static void parseConfigurePlots(ContainerScreen screen) {
        if (!(screen.getMenu() instanceof ChestMenu menu)) return;
        boolean changed = false;

        for (int row = 0; row < 5; row++) {
            for (int slotId = row * 9 + 2; slotId < row * 9 + 7; slotId++) {
                if (slotId == 22) continue; // Barn icon slot.
                if (slotId < 0 || slotId >= menu.slots.size()) continue;

                Slot slot = menu.slots.get(slotId);
                ItemStack stack = slot.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (stack.is(Items.RED_STAINED_GLASS_PANE) || stack.is(Items.OAK_BUTTON) || stack.is(Items.BLACK_STAINED_GLASS_PANE)) continue;

                String rawName = ChatFormatting.stripFormatting(stack.getHoverName().getString());
                if (rawName == null || rawName.isBlank()) continue;

                String plotName = extractPlotName(rawName);
                if (plotName.isBlank()) continue;

                int plotIndex = row * 5 + (slotId % 9 - 2);
                GardenPlotConfig.setPlotName(plotIndex, plotName);
                changed = true;
            }
        }

        if (changed) {
            GardenPlotConfig.save();
            LOGGER.info("Learned Garden plot names from Configure Plots screen.");
        }
    }

    private static String extractPlotName(String rawName) {
        String clean = rawName.trim();
        String[] parts = clean.split("-", 2);
        if (parts.length >= 2) return parts[1].trim();
        return clean;
    }

    static void sendCommand(String command) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        String normalized = command.trim();
        if (normalized.startsWith("/")) normalized = normalized.substring(1);
        client.player.connection.sendCommand(normalized);
    }

    static void clickSound() {
        Minecraft client = Minecraft.getInstance();
        if (client.getSoundManager() == null) return;
        client.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
    }
}
