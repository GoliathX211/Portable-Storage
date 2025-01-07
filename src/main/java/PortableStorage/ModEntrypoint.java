package PortableStorage;

import PortableStorage.registry.*;
import necesse.engine.modLoader.annotations.ModEntry;

/**
 *  Entry point for your mod, you should rarely have to do anything in here. All registrations are setup in /registry
 */
@ModEntry
public class ModEntrypoint {

    public void init() {
        ModItemsRegistry.RegisterAll();
        ModPacketRegistry.RegisterAll();
    }

    // Load resources such as textures and music.
    public void initResources() {
        ModTextureRegistry.RegisterAll();
    }

    public void postInit() {
        ModRecipeRegistry.RegisterAll();
    }

}
