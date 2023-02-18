package me.yoarchh.iridium;

import me.yoarchh.iridium.utils.IridiumLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class IridiumClientMod implements ClientModInitializer
{
    private static IridiumClientMod INSTANCE;

    // (YoArchh) Might change the logger's name. Not sure.
    private static final IridiumLogger LOGGER = new IridiumLogger("Iridium Core");

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;

        LOGGER.info("Initialized Iridium! Enjoy!");
    }

    public static IridiumClientMod getInstance() { return INSTANCE; }

    public static IridiumLogger getLogger() { return LOGGER; }
}
