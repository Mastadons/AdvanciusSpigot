package net.advancius.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class ColorUtils {

    public static String translateColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static TextComponent toTextComponent(String message) {
        return new TextComponent(ColorUtils.translateColor(message));
    }
}
