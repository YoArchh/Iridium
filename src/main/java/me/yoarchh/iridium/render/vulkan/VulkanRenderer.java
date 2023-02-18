package me.yoarchh.iridium.render.vulkan;

import me.yoarchh.iridium.utils.IridiumLogger;

public class VulkanRenderer
{
    private static VulkanRenderer INSTANCE;

    private static final IridiumLogger LOGGER = new IridiumLogger("Iridium Renderer");

    private final VulkanContext vulkanContext;

    public VulkanRenderer()
    {
        INSTANCE = this;

        this.vulkanContext = new VulkanContext();
    }

    public void initialize()
    {
        LOGGER.info("Initializing Vulkan renderer...");

        this.vulkanContext.initialize();

        LOGGER.info("Successfully initialized Vulkan renderer!");
    }

    public void shutdown()
    {
        LOGGER.info("Shutting down Vulkan renderer...");

        this.vulkanContext.destroy();
    }

    public static VulkanRenderer getInstance() { return INSTANCE; }

    public static IridiumLogger getLogger() { return LOGGER; }

    public VulkanContext getContext() { return this.vulkanContext; }
}
