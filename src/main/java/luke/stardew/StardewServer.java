package luke.stardew;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.core.sound.SoundTypes;

import static luke.stardew.StardewMod.MOD_ID;

public class StardewServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        SoundTypes.loadSoundsJson(MOD_ID);
    }
}
