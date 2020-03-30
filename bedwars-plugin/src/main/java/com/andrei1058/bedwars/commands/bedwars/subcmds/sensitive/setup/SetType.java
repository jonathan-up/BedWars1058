package com.andrei1058.bedwars.commands.bedwars.subcmds.sensitive.setup;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.server.SetupType;
import com.andrei1058.bedwars.arena.Misc;
import com.andrei1058.bedwars.arena.SetupSession;
import com.andrei1058.bedwars.api.command.ParentCommand;
import com.andrei1058.bedwars.api.command.SubCommand;
import com.andrei1058.bedwars.configuration.Permissions;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class SetType extends SubCommand {

    public SetType(ParentCommand parent, String name) {
        super(parent, name);
        setArenaSetupCommand(true);
        setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }

    private static List<String> available = Arrays.asList("Solo", "Doubles", "3v3v3v3", "4v4v4v4");

    @Override
    public boolean execute(String[] args, CommandSender s) {
        if (s instanceof ConsoleCommandSender) return false;
        Player p = (Player) s;
        SetupSession ss = SetupSession.getSession(p.getUniqueId());
        if (ss == null){
            s.sendMessage("§c ▪ §7You're not in a setup session!");
            return true;
        }
        if (args.length == 0){
           sendUsage(p);
        } else {
            if (!available.contains(args[0])){
                sendUsage(p);
                return true;
            }
            List groups = BedWars.config.getYml().getStringList("arenaGroups");
            String input = args[0].substring(0, 1).toUpperCase()+args[0].substring(1).toLowerCase();
            if (!groups.contains(input)){
                groups.add(input);
                BedWars.config.set("arenaGroups", groups);
                int maxInTeam = 1;
                if (input.equalsIgnoreCase("Doubles")){
                    maxInTeam = 2;
                } else if (input.equalsIgnoreCase("3v3v3v3")){
                    maxInTeam = 3;
                } else if (input.equalsIgnoreCase("4v4v4v4")){
                    maxInTeam = 4;
                }
                ss.getConfig().set("maxInTeam", maxInTeam);
            }
            ss.getConfig().set("group", input);
            p.sendMessage("§6 ▪ §7Arena group changed to: §d"+input);
            if (ss.getSetupType() == SetupType.ASSISTED){
                Bukkit.dispatchCommand(p, getParent().getName());
            }
        }
        return true;
    }

    @Override
    public List<String> getTabComplete() {
        return available;
    }

    private void sendUsage(Player p){
        p.sendMessage("§9 ▪ §7Usage: "+getParent().getName()+" "+getSubCommandName()+" <type>");
        p.sendMessage("§9Available types: ");
        for (String st : available){
            p.spigot().sendMessage(Misc.msgHoverClick("§1 ▪ §e"+st+" §7(click to set)", "§dClick to make the arena "+st, "/"+getParent().getName()+" "+getSubCommandName()+" "+st, ClickEvent.Action.RUN_COMMAND));
        }
    }

    @Override
    public boolean canSee(CommandSender s, com.andrei1058.bedwars.api.BedWars api) {
        if (s instanceof ConsoleCommandSender) return false;

        Player p = (Player) s;
        if (!SetupSession.isInSetupSession(p.getUniqueId())) return false;

        return hasPermission(s);
    }
}