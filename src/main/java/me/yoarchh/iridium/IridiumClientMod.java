package me.yoarchh.iridium;

import me.yoarchh.iridium.utils.IridiumLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;

@Environment(EnvType.CLIENT)
public class IridiumClientMod implements ClientModInitializer
{
    private static IridiumClientMod INSTANCE;

    // (YoArchh) Might change the logger's name. Not sure.
    private static final IridiumLogger LOGGER = new IridiumLogger("Iridium Core");

    private final Version version = FabricLoader.getInstance().getModContainer("iridium").get().getMetadata().getVersion();

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;

        LOGGER.info("Running Minecraft with Iridium v{}", version.getFriendlyString());
    }

    public static IridiumClientMod getInstance() { return INSTANCE; }

    public static IridiumLogger getLogger() { return LOGGER; }

    public Version getVersion() { return this.version; }
}
