package fr.alexdoru.mwe.features.ctf;

import fr.alexdoru.mwe.api.MWECommandBase;
import fr.alexdoru.mwe.api.enums.MWMap;
import fr.alexdoru.mwe.api.events.MapEvent;
import fr.alexdoru.mwe.chat.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class CommandTestCTF extends MWECommandBase {

    private MWMap map;
    private FlagObjective flag1, flag2;

    public CommandTestCTF() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void on(MapEvent event) {
        map = MWMap.fromName(event.mapName);
    }

    @Override
    public String getCommandName() {
        return "ctf";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        final boolean spawnHere = args.length > 0 && args[0].equalsIgnoreCase("here");
        if (flag1 == null) {
            if (spawnHere || map == null) {
                final Entity entity = Minecraft.getMinecraft().thePlayer;
                flag1 = new FlagObjective(map == null ? MWMap.ANCHORED : map, entity.posX, entity.posZ);
                MinecraftForge.EVENT_BUS.register(flag1);
                ChatUtil.debug("spawned flag here");
            } else {
                flag1 = new FlagObjective(map, new Random());
                MinecraftForge.EVENT_BUS.register(flag1);
                flag2 = flag1.getOppositeSideFlag();
                if (flag2 != null) {
                    MinecraftForge.EVENT_BUS.register(flag2);
                }
                ChatUtil.debug("spawned flag");
            }
        } else {
            MinecraftForge.EVENT_BUS.unregister(flag1);
            flag1 = null;
            if (flag2 != null) {
                MinecraftForge.EVENT_BUS.unregister(flag2);
                flag2 = null;
            }
        }
    }

}
