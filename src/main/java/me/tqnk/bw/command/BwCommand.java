package me.tqnk.bw.command;

import lombok.Getter;
import me.tqnk.bw.MGM;
import me.tqnk.bw.events.MatchQueueRequestEvent;
import me.tqnk.bw.game.GameType;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.permissions.RankData;
import me.tqnk.bw.user.PlayerManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class BwCommand extends CommandModel {
    private List<CommandPerm> usages = new ArrayList<CommandPerm>(){{
        add(new CommandPerm(ChatColor.YELLOW + "/bw admin tp (Match ID)", RankData.ADMIN));
        add(new CommandPerm(ChatColor.YELLOW + "/bw help", RankData.DEFAULT));
    }};

    public BwCommand() {
        super(new ArrayList<String>() {{
            add("bw");
        }});
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player target = (Player) sender;
        int argCount = args.length;
        if(argCount >= 1) {
            String firstArg = args[0];
            if(firstArg.equalsIgnoreCase("admin")) {
                if(!sender.isOp()) {
                    target.sendMessage(ChatColor.RED + "/bw help");
                    return;
                }
                if(argCount >= 2) adminCommandHandler(target, args);
                else sendAdminUsages(target);
            } else if(firstArg.equalsIgnoreCase("help")) sendHelpUsages(target);
            else if(firstArg.equalsIgnoreCase("join")) {
                Bukkit.getPluginManager().callEvent(new MatchQueueRequestEvent(target, GameType.BEDWARS));
            }
        } else target.sendMessage(ChatColor.RED + "/bw help");
    }

    private void sendAdminUsages(Player p) {
        p.sendMessage(ChatColor.GOLD + "Admin Commands");
        p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---------------");
        for(CommandPerm usage : usages) if(usage.getRequirement().getPriority() >= 999) p.sendMessage(usage.getCommand());
    }

    private void sendHelpUsages(Player p) {
        p.sendMessage(ChatColor.GOLD + "CG" + ChatColor.YELLOW  + "Bed");
        p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---------------");
        for(CommandPerm usage : usages) if(usage.getRequirement().getPriority() < 999) p.sendMessage(usage.getCommand());
    }

    private void adminCommandHandler(Player target, String[] args) {
        int argCount = args.length;
        String firstRealArg = args[1];
        if(firstRealArg.equalsIgnoreCase("tp") || firstRealArg.equalsIgnoreCase("teleport")) {
            if(argCount >= 3) {
                int ID = checkIfValidID(args[2]);
                if(ID < 0) {
                    target.sendMessage(ChatColor.RED + "Invalid ID.");
                    return;
                }
                Match candidate = MGM.get().getMatchRuntimeManager().getMatchById(ID);
                if(candidate == null) {
                    target.sendMessage(ChatColor.RED + "An error occurred.");
                } else {
                    target.teleport(candidate.getMatchInfo().getSpawnArea(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            } else target.sendMessage(usages.get(0).getCommand());
        } else if(firstRealArg.equalsIgnoreCase("info")) {
            int ID = checkIfValidID(args[2]);
            if(ID < 0) {
                target.sendMessage(ChatColor.RED + "Invalid ID.");
                return;
            }
            Match candidate = MGM.get().getMatchRuntimeManager().getMatchById(ID);
            if(candidate == null) {
                target.sendMessage(ChatColor.RED + "An error occurred.");
            } else {
                target.sendMessage(ChatColor.YELLOW + "Match UUID " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + candidate.getIdentity().toString());
                target.sendMessage(ChatColor.YELLOW + "Match World " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + candidate.getHostWorld());
                target.sendMessage(ChatColor.YELLOW + "Match Spawn Area " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + candidate.getMatchInfo().getSpawnArea());
            }
        } else if(firstRealArg.equalsIgnoreCase("queuelist")) {
            int ID = checkIfValidID(args[2]);
            if(ID < 0) {
                target.sendMessage(ChatColor.RED + "Invalid ID.");
                return;
            }
            Match candidate = MGM.get().getMatchRuntimeManager().getMatchById(ID);
            if(candidate == null) {
                target.sendMessage(ChatColor.RED + "An error occurred.");
            } else {
                target.sendMessage(ChatColor.GOLD + "Players in Match " + ChatColor.YELLOW + ID);
                String allPlayers = "";
                for(Player person : candidate.getMatchInfo().getQueuedPlayers()) allPlayers = allPlayers + person.getDisplayName() + " ";
                target.sendMessage(allPlayers);
            }
        } else if(firstRealArg.equalsIgnoreCase("ingamelist")) {
            PlayerManager playerManager = MGM.get().getPlayerManager();
            String inGame = "";
            for(Player player : Bukkit.getOnlinePlayers()) if(playerManager.getPlayerContext(player).getInGame() != null) inGame = inGame + player.getDisplayName() + " ";
            target.sendMessage(ChatColor.GREEN + "People in any sort of game:");
            target.sendMessage(inGame);
        }
    }

    private int checkIfValidID(String fighter) {
        if(!NumberUtils.isNumber(fighter)) return -1;
        int x;
        try {
            x = Integer.parseInt(fighter);
        } catch(NumberFormatException e) {
            return -1;
        }
        return (x < 1 || x > MGM.get().getMatchRuntimeManager().getAllMatches().size()) ? -1 : x;
    }

    class CommandPerm {
        @Getter private String command;
        @Getter private RankData requirement;
        CommandPerm(String command, RankData requirement) {
            this.command = command;
            this.requirement = requirement;
        }
    }
}
