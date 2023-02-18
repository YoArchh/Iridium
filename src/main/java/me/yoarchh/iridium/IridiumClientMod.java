package me.yoarchh.iridium;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

@Environment(EnvType.CLIENT)
public class IridiumClientMod implements ClientModInitializer
{
    private static IridiumClientMod INSTANCE;

    private static final Logger LOGGER = (Logger) LogManager.getLogger("Iridium");

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;

        LOGGER.info("Initialized Iridium! Enjoy!");
    }

    public static IridiumClientMod getInstance() { return INSTANCE; }

    public static Logger getLogger() { return LOGGER; }
}
