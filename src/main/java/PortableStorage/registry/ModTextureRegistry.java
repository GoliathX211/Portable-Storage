package PortableStorage.registry;

import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;

public class ModTextureRegistry {
    public static GameTexture EXAMPLE_TEXTURE = null;

    public static void RegisterAll() {
        /* Register textures here! */
        GameTexture SmallBag = GameTexture.fromFile("SmallBag.png");
        GameTexture MediumBag = GameTexture.fromFile("MediumBag.png");
        GameTexture LargeBag = GameTexture.fromFile("LargeBag.png");
        GameTexture ExtraLargeBag = GameTexture.fromFile("ExtraLargeBag.png");
    }


}
