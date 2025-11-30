package com.github.smallinger.copperagebackport.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.Item;

/**
 * Custom Copper Horse Armor that uses our mod's texture instead of minecraft namespace.
 * 
 * VERSION DIFFERENCE:
 * - 1.20.1: Vanilla HorseArmorItem(int, String, Properties) always looks for textures in minecraft namespace.
 *           This class overrides getTexture() to return our texture location.
 * - 1.21.1: Uses AnimalArmorItem which derives texture from ArmorMaterial key automatically.
 *           No custom class needed there.
 * 
 * Texture path: minecraft:textures/entity/horse/armor/horse_armor_copper.png
 * (We use minecraft namespace since the item is registered under minecraft:copper_horse_armor)
 */
public class CopperHorseArmorItem extends HorseArmorItem {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(
        "minecraft", 
        "textures/entity/horse/armor/horse_armor_copper.png"
    );
    
    public CopperHorseArmorItem(int protection, Item.Properties properties) {
        // Pass "copper" as identifier - it won't be used since we override getTexture()
        super(protection, "copper", properties);
    }
    
    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }
}
