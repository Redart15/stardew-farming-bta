package luke.stardew;

import luke.stardew.world.WorldFeatureBush;
import luke.stardew.world.WorldFeatureCauliflower;
import luke.stardew.world.WorldFeatureMelon;
import luke.stardew.world.WorldFeatureTreeSeasonal;

import static net.minecraft.core.net.command.util.CommandHelper.registerWorldFeatureClass;

public class StardewWorldFeatures {
    private static boolean hasInit = false;

    private StardewWorldFeatures(){}

    public static void init() {
        if (!hasInit) {
            hasInit = true;
            initializeWorldFeatures();
        }
    }

    private static void initializeWorldFeatures() {
        registerWorldFeatureClass(WorldFeatureBush.class, "Bush");
        registerWorldFeatureClass(WorldFeatureCauliflower.class, "CauliflowerField");
        registerWorldFeatureClass(WorldFeatureMelon.class, "WatermelonField");
        registerWorldFeatureClass(WorldFeatureTreeSeasonal.class, "TreeSeasonal");
    }
}
