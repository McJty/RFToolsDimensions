package mcjty.rftoolsdim.compat;

import mcjty.rftoolsbase.api.teleportation.ITeleportationManager;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;

import java.util.function.Function;

public class RFToolsUtilityCompat {

    public static void register() {
        if (ModList.get().isLoaded("rftoolsutility")) {
            registerInternal();
        }

    }

    private static boolean registered;
    public static ITeleportationManager teleportationManager = null;

    private static void registerInternal() {
        if (registered) {
            return;
        }
        registered = true;
        InterModComms.sendTo("rftoolsutility", "getTeleportationManager", GetTeleportationManager::new);
    }


    public static void createTeleporter(ISeedReader reader, BlockPos pos, String name) {
        if (teleportationManager != null) {
            teleportationManager.createReceiver(reader.getWorld(), pos, name, -1);
        }
    }

    public static class GetTeleportationManager implements Function<ITeleportationManager, Void> {

        @Override
        public Void apply(ITeleportationManager tm) {
            RFToolsUtilityCompat.teleportationManager = tm;
            return null;
        }
    }
}
