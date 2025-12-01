package com.github.smallinger.copperagebackport;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/**
 * Custom BlockSetTypes for Copper Age Backport.
 * COPPER type allows opening by hand and uses copper metal sounds.
 * 
 * Note: We create a new BlockSetType without calling register() - this works fine
 * as the VALUES set is only used for iteration/lookup which we don't need.
 */
public class ModBlockSetTypes {
    
    // Create SoundEvents directly without registry - works as long as sounds.json has the definitions
    private static final SoundEvent COPPER_DOOR_CLOSE_SOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation("minecraft", "block.copper_door.close"));
    private static final SoundEvent COPPER_DOOR_OPEN_SOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation("minecraft", "block.copper_door.open"));
    private static final SoundEvent COPPER_TRAPDOOR_CLOSE_SOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation("minecraft", "block.copper_trapdoor.close"));
    private static final SoundEvent COPPER_TRAPDOOR_OPEN_SOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation("minecraft", "block.copper_trapdoor.open"));
    
    /**
     * BlockSetType for copper blocks (trapdoors, doors, buttons, etc.)
     * - canOpenByHand: true (unlike IRON which is false)
     * - Uses COPPER sound type
     * - Uses our custom copper door and trapdoor sounds
     */
    public static final BlockSetType COPPER = new BlockSetType(
        "copper",
        true, // canOpenByHand - copper can be opened by hand, unlike iron
        SoundType.COPPER, // soundType
        COPPER_DOOR_CLOSE_SOUND, // doorClose - our custom copper door sound
        COPPER_DOOR_OPEN_SOUND,  // doorOpen - our custom copper door sound
        COPPER_TRAPDOOR_CLOSE_SOUND, // trapdoorClose - our custom copper trapdoor sound
        COPPER_TRAPDOOR_OPEN_SOUND,  // trapdoorOpen - our custom copper trapdoor sound
        SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, // pressurePlateClickOff
        SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,  // pressurePlateClickOn
        SoundEvents.STONE_BUTTON_CLICK_OFF, // buttonClickOff (copper buttons have their own sounds)
        SoundEvents.STONE_BUTTON_CLICK_ON   // buttonClickOn
    );
}
