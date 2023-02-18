package me.yoarchh.iridium.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin
{
    /**
     * @author YoArchh
     * @reason Defers the creation of the rules required to migrate data from older versions of Minecraft to newer versions until necessary. Makes the game boot faster.
     */
    @Overwrite
    public static void enableDataFixerOptimization() {}
}
