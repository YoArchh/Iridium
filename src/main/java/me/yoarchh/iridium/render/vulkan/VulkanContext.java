package me.yoarchh.iridium.render.vulkan;

import me.yoarchh.iridium.render.exception.IridiumRendererException;
import me.yoarchh.iridium.render.utils.PointerUtils;
import me.yoarchh.iridium.utils.IridiumLogger;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK12.*;

public class VulkanContext
{
    // Enable validation layers ONLY if we are in a development environment
    private static final boolean enableValidationLayers = FabricLoader.getInstance().isDevelopmentEnvironment();

    private static final Set<String> VALIDATION_LAYERS;

    private final IridiumLogger LOGGER = VulkanRenderer.getLogger();

    private VkInstance instanceHandle;

    private long debugMessengerHandle;

    static
    {
        if (enableValidationLayers)
        {
            VALIDATION_LAYERS = new HashSet<>();
            VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_validation");
        }
        else
        {

            VALIDATION_LAYERS = null;
        }
    }

    public void initialize()
    {
        if (enableValidationLayers && !areValidationLayersSupported())
            throw new IridiumRendererException("Failed to create Vulkan context! Validation layers were requested, but aren't supported!");

        try (MemoryStack memoryStack = MemoryStack.stackPush())
        {
            VkApplicationInfo applicationCreateInfo = VkApplicationInfo.calloc(memoryStack);
            applicationCreateInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            applicationCreateInfo.pApplicationName(memoryStack.UTF8Safe("Minecraft"));
            applicationCreateInfo.applicationVersion(VK_MAKE_VERSION(1, 19, 3));
            applicationCreateInfo.pEngineName(memoryStack.UTF8Safe("Iridium"));
            applicationCreateInfo.engineVersion(VK_MAKE_VERSION(1, 0, 0));
            applicationCreateInfo.apiVersion(VK_API_VERSION_1_2);
            applicationCreateInfo.pNext(MemoryUtil.NULL);

            VkInstanceCreateInfo instanceCreateInfo = VkInstanceCreateInfo.calloc(memoryStack);
            instanceCreateInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            instanceCreateInfo.pApplicationInfo(applicationCreateInfo);
            instanceCreateInfo.ppEnabledExtensionNames(getRequiredInstanceExtensions());

            if (enableValidationLayers)
            {
                instanceCreateInfo.ppEnabledLayerNames(PointerUtils.stringColletionToPointerBuffer(VALIDATION_LAYERS));

                VkDebugUtilsMessengerCreateInfoEXT debugMessengerCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(memoryStack);
                populateDebugMessengerCreateInfo(debugMessengerCreateInfo);

                instanceCreateInfo.pNext(debugMessengerCreateInfo.address());
            }

            PointerBuffer pInstance = memoryStack.mallocPointer(1);

            if (vkCreateInstance(instanceCreateInfo, null, pInstance) != VK_SUCCESS)
                throw new IridiumRendererException("Failed to create Vulkan context! Instance failed to create!");

            this.instanceHandle = new VkInstance(pInstance.get(0), instanceCreateInfo);
        }

        if (enableValidationLayers)
            setupDebugMessenger();

        LOGGER.info("Successfully created Vulkan context!");
    }

    private boolean areValidationLayersSupported()
    {
        boolean wasValidationLayerFound = false;

        try (MemoryStack memoryStack = MemoryStack.stackPush())
        {
            IntBuffer instanceLayerCount = memoryStack.ints(0);
            vkEnumerateInstanceLayerProperties(instanceLayerCount, null);

            VkLayerProperties.Buffer availableInstanceLayers = VkLayerProperties.malloc(instanceLayerCount.get(0), memoryStack);
            vkEnumerateInstanceLayerProperties(instanceLayerCount, availableInstanceLayers);

            for (String layerName : VALIDATION_LAYERS)
            {
                for (VkLayerProperties layerProperty : availableInstanceLayers)
                {
                    if (layerName.equals(layerProperty.layerNameString()))
                    {
                        wasValidationLayerFound = true;
                        break;
                    }
                }
            }
        }

        return wasValidationLayerFound;
    }

    private PointerBuffer getRequiredInstanceExtensions()
    {
        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

        if (enableValidationLayers)
        {
            MemoryStack memoryStack = MemoryStack.stackGet();

            PointerBuffer extensions = memoryStack.mallocPointer(glfwExtensions.capacity() + 1);
            extensions.put(glfwExtensions);
            extensions.put(memoryStack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));

            return extensions.rewind();
        }

        return glfwExtensions;
    }

    private void setupDebugMessenger()
    {
        if (!enableValidationLayers)
            return;

        try (MemoryStack memoryStack = MemoryStack.stackPush())
        {
            VkDebugUtilsMessengerCreateInfoEXT debugMessengerCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(memoryStack);
            populateDebugMessengerCreateInfo(debugMessengerCreateInfo);

            LongBuffer pDebugMessenger = memoryStack.longs(VK_NULL_HANDLE);

            if (createDebugUtilsMessenger(instanceHandle, debugMessengerCreateInfo, null, pDebugMessenger) != VK_SUCCESS)
                throw new IridiumRendererException("Failed to create Vulkan context! Failed to create debug messenger!");

            this.debugMessengerHandle = pDebugMessenger.get(0);
        }
    }

    private void populateDebugMessengerCreateInfo(VkDebugUtilsMessengerCreateInfoEXT debugMessengerCreateInfo)
    {
        debugMessengerCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
        debugMessengerCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
        debugMessengerCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT);
        debugMessengerCreateInfo.pfnUserCallback((messageSeverity, messageTypes, pCallbackData, pUserData) ->
        {
            try (VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData))
            {
                switch (messageSeverity)
                {
                    case VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> LOGGER.warn("[Vulkan Validation] {}", callbackData.pMessageString());
                    case VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> LOGGER.error("[Vulkan Validation] {}", callbackData.pMessageString());
                }
            }

            return VK_FALSE;
        });
    }

    private int createDebugUtilsMessenger(VkInstance instance, VkDebugUtilsMessengerCreateInfoEXT debugMessengerCreateInfo,
                                          VkAllocationCallbacks allocationCallbacks, LongBuffer pDebugMessenger)
    {
        if (vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != MemoryUtil.NULL)
            return vkCreateDebugUtilsMessengerEXT(instance, debugMessengerCreateInfo, allocationCallbacks, pDebugMessenger);

        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }

    public void destroy()
    {
        if (enableValidationLayers)
            destroyDebugUtilsMessenger(this.instanceHandle, this.debugMessengerHandle, null);

        vkDestroyInstance(this.instanceHandle, null);
    }

    private void destroyDebugUtilsMessenger(VkInstance instance, long debugMessenger, VkAllocationCallbacks allocationCallbacks)
    {
        if (vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT") != MemoryUtil.NULL)
            vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, allocationCallbacks);
    }

    public static Set<String> getValidationLayers()
    {
        return VALIDATION_LAYERS;
    }

    public static boolean areValidationLayersEnabled()
    {
        return enableValidationLayers;
    }

    public VkInstance getInstance()
    {
        return this.instanceHandle;
    }
}
