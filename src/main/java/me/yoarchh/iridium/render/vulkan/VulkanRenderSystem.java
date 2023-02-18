package me.yoarchh.iridium.render.vulkan;

import com.mojang.blaze3d.systems.RenderSystem;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Locale;

public class VulkanRenderSystem
{
    private static String cpuInfo;

    public static void initRenderer()
    {
        RenderSystem.assertInInitPhase();

        try
        {
            CentralProcessor centralProcessor = new SystemInfo().getHardware().getProcessor();
            cpuInfo = String.format(Locale.ROOT, "%dx %s",
                    centralProcessor.getLogicalProcessorCount(), centralProcessor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
        }
        catch (Throwable throwable)
        {
            // empty catch block
        }

        new VulkanRenderer().initialize();
    }

    public static String getCPUInfo()
    {
        return cpuInfo == null ? "CPU Info Unknown" : cpuInfo;
    }
}
