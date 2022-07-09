package PortableStorage.registry;

import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureAnim;

import java.util.ArrayList;
import java.util.List;

public class ModTextureRegistry {
    public static GameTexture EXAMPLE_TEXTURE = null;
    public static GameTextureAnim vortexAnimation;
    public static void RegisterAll() {
        /* Register textures here! */
        GameTexture SmallBag = GameTexture.fromFile("SmallBag.png");
        GameTexture MediumBag = GameTexture.fromFile("MediumBag.png");
        GameTexture LargeBag = GameTexture.fromFile("LargeBag.png");
        GameTexture ExtraLargeBag = GameTexture.fromFile("ExtraLargeBag.png");

        GameTexture DeepBag1 = GameTexture.fromFile("DeepBag/deepbag1.png");
        GameTexture DeepBag2 = GameTexture.fromFile("DeepBag/deepbag2.png");
        GameTexture DeepBag3 = GameTexture.fromFile("DeepBag/deepbag3.png");
        GameTexture DeepBag4 = GameTexture.fromFile("DeepBag/deepbag4.png");
        GameTexture DeepBag5 = GameTexture.fromFile("DeepBag/deepbag5.png");
        GameTexture DeepBag6 = GameTexture.fromFile("DeepBag/deepbag6.png");
        GameTexture DeepBag7 = GameTexture.fromFile("DeepBag/deepbag7.png");
        GameTexture DeepBag8 = GameTexture.fromFile("DeepBag/deepbag8.png");
        GameTexture[] textures = new GameTexture[] {
                DeepBag1,
                DeepBag2,
                DeepBag3,
                DeepBag4,
                DeepBag5,
                DeepBag6,
                DeepBag7,
                DeepBag8,

        };
        vortexAnimation = new GameTextureAnim(16, 16, 1, textures);
    }


}
