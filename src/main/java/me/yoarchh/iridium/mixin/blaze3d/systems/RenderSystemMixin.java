package me.yoarchh.iridium.mixin.blaze3d.systems;

import com.mojang.blaze3d.systems.RenderSystem;
import me.yoarchh.iridium.render.vulkan.VulkanRenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin
{
    @Inject(at = @At("HEAD"), method = "initRenderer", cancellable = true)
    private static void initializeVulkanRenderer(int debugVerbosity, boolean debugSync, CallbackInfo ci)
    {
        VulkanRenderSystem.initRenderer();

        ci.cancel();
    }
}
