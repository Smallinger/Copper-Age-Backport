package com.github.smallinger.copperagebackport.item;

import com.github.smallinger.copperagebackport.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.Item;

/**
 * Custom Copper Horse Armor that uses our mod's texture instead of minecraft namespace.
 * 
 * VERSION DIFFERENCE:
 * - 1.20.1: Vanilla HorseArmorItem(int, String, Properties) always looks for textures in minecraft namespace.
 *           This class overrides getTexture() to return our mod's texture location.
 * - 1.21.1: Uses AnimalArmorItem which derives texture from ArmorMaterial key automatically.
 *           No custom class needed there.
 * 
 * Texture path: copperagebackport:textures/entity/horse/armor/horse_armor_copper.png
 */
public class CopperHorseArmorItem extends HorseArmorItem {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(
        Constants.MOD_ID, 
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
