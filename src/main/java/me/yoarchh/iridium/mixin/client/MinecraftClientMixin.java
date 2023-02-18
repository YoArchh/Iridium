package me.yoarchh.iridium.mixin.client;

import me.yoarchh.iridium.render.vulkan.VulkanRenderer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;close()V"), method = "stop")
    public void onMinecraftClientClose(CallbackInfo ci)
    {
        VulkanRenderer.getInstance().shutdown();
    }
}
