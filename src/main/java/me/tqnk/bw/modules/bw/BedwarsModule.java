package me.tqnk.bw.modules.bw;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.tqnk.bw.MGM;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.modules.periodical.Periodical;
import me.tqnk.bw.modules.scoreboard.ScoreboardManagerModule;
import me.tqnk.bw.modules.team.MatchTeam;
import me.tqnk.bw.modules.team.TeamManagerModule;
import me.tqnk.bw.status.GameStatus;
import me.tqnk.bw.user.PlayerManager;
import me.tqnk.bw.util.ItemUtil;
import me.tqnk.bw.util.MatchUtil;
import me.tqnk.bw.util.Parser;
import me.tqnk.bw.util.PlayerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BedwarsModule extends MatchModule implements Listener, Periodical {
    private ScoreboardManagerModule scoreboardManagerModule;
    private TeamManagerModule teamManagerModule;
    private PlayerManager playerManager;
    private Match match;
    private HashMap<MatchTeam, BWTeamInfo> teamInfoLink = new HashMap<>();
    private List<BWUserData> bwUserData = new ArrayList<>();
    private List<Block> blocksPlaced = new ArrayList<>();
    private List<Location> shopVillagerLocs = new ArrayList<>();
    private List<Villager> villagers = new ArrayList<>();
    private HashMap<MatchTeam, Location> bedLocs = new HashMap<>();
    private String shopName = ChatColor.GOLD + "Shop";

    @Override
    public void load(Match match) {
        this.match = match;
        this.playerManager = MGM.get().getPlayerManager();
        scoreboardManagerModule = match.getModule(ScoreboardManagerModule.class);
        teamManagerModule = match.getModule(TeamManagerModule.class);
        parseRemainderJson(match.getMap().getMetadata().getRawJson(), match.getHostWorld());
        initializeNPCs();
    }

    private void parseRemainderJson(JsonElement elem, World world) {
        JsonObject rawData = elem.getAsJsonObject();
        if(rawData.has("teams")) {
            for(JsonElement teamElement : rawData.getAsJsonArray("teams")) {
                JsonObject teamJson = teamElement.getAsJsonObject();
                Location bedLoc = Parser.convertLocation(world, teamJson.get("bed"));
                bedLocs.put(teamManagerModule.getMatchTeamById(teamJson.get("id").getAsString()), bedLoc);
            }
        }
        if(rawData.has("shops")) {
            for(JsonElement loc : rawData.getAsJsonArray("shops")) shopVillagerLocs.add(Parser.convertLocation(world, loc));
        }
    }

    private void initializeNPCs() {
        World hostWorld = match.getHostWorld();
        for(Location loc : shopVillagerLocs) {
            Villager shop = (Villager) hostWorld.spawnEntity(loc, EntityType.VILLAGER);
            shop.setCustomName(shopName);
            shop.setCustomNameVisible(true);
            shop.setInvulnerable(true);
            shop.setHealth(20);
            shop.setInvulnerable(true);
            shop.setRecipes(new ArrayList<MerchantRecipe>());
            shop.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200000, 255));
            villagers.add(shop);
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;
        if(event.getRightClicked() instanceof Villager) {
            Villager getMyBoi = (Villager) event.getRightClicked();
            if(villagers.contains(getMyBoi)) {
                shopGui(p);
            } else Bukkit.broadcastMessage("nope");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;
        if(event.getInventory().getName().equalsIgnoreCase(shopName)) {
            event.setCancelled(true);
            shopHandle(p, event.getCurrentItem(), event.getSlot());
        }
    }

    private void shopHandle(Player p, ItemStack clickedItem, int clickedSlot) {
        BWUserData candidate = getBWUserDataByPlayer(p);
        if(candidate == null) return;
        BWShopItem contained = candidate.getBwShopIndex().getShopIndex().get(clickedSlot);
        if(contained == null) return;
        if(meetsShopReq(p, contained.getCostMaterial())) {
            PlayerUtil.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.5F);

            int firstInst = p.getInventory().first(contained.getCostMaterial().getType());
            p.getInventory().getItem(firstInst).setAmount(p.getInventory().getItem(firstInst).getAmount() - contained.getCostMaterial().getAmount());
            p.getInventory().addItem(contained.getItem());

            p.updateInventory();
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough materials to purchase this!");
            PlayerUtil.playSound(p, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5F);
        }
    }

    private boolean meetsShopReq(Player p, ItemStack coster) {
        return p.getInventory().contains(coster.getType(), coster.getAmount());
    }

    private void shopGui(Player p) {
        Inventory yee = Bukkit.createInventory(null, 54, shopName);
        populateGui(yee, p, 0);
        p.closeInventory();
        p.openInventory(yee);
    }

    private void populateGui(Inventory yee, Player p, int pageNumber) {
        BWUserData candidate = findUserDataByPlayer(p);
        if(candidate == null) return;
        candidate.getBwShopIndex().applyIndexToInventory(yee, pageNumber);
        populateGuiWithDefaults(yee);
    }

    private void populateGuiWithDefaults(Inventory yee) {
        ItemStack someglazz = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta detail = someglazz.getItemMeta();
        detail.setDisplayName("");
        for(int x = 9; x <= 17; x++) yee.setItem(x, someglazz);
        yee.setItem(1, ItemUtil.createItem(Material.HARD_CLAY, ChatColor.YELLOW + "Building Blocks", Collections.singletonList(ChatColor.GRAY + "Buy " + ChatColor.GREEN + "blocks " + ChatColor.GRAY + "here!")));
        yee.setItem(2, ItemUtil.createItem(Material.WOOD_SWORD, ChatColor.YELLOW + "Weaponry", Collections.singletonList(ChatColor.GRAY + "Purchase some fine " + ChatColor.AQUA + "weapons")));
        yee.setItem(3, ItemUtil.createItem(Material.STONE_PICKAXE, ChatColor.YELLOW + "Tools", Collections.singletonList(ChatColor.GOLD + "Tools " + ChatColor.GRAY + "can be found here")));
        yee.setItem(4, ItemUtil.createItem(Material.TNT, ChatColor.YELLOW + "Special Items", Collections.singletonList(ChatColor.GRAY + "They had nowhere else to go")));
        yee.setItem(5, ItemUtil.createItem(Material.DIAMOND, ChatColor.YELLOW + "Team Upgrades", Collections.singletonList(ChatColor.GRAY + "Buy " + ChatColor.GOLD + "team upgrades" + ChatColor.GRAY + " here")));
    }

    @Override
    public void start() {
        TeamManagerModule teamManagerModule = match.getModule(TeamManagerModule.class);
        teamManagerModule.distributePlayersToTeams(match.getMatchInfo().getQueuedPlayers());
        for(Player p : match.getMatchInfo().getQueuedPlayers()) {
            p.sendMessage(ChatColor.GOLD + "Bedwars " + ChatColor.GRAY + "has started!");
            bwUserData.add(new BWUserData(p, 0, ItemUtil.translateChatColorToColor(playerManager.getPlayerContext(p).getInTeam().getChatTeamColor())));
            p.setScoreboard(scoreboardManagerModule.getNewSB());
        }
        teamManagerModule.sendAllTeamsToTheirSpawn();
        for(MatchTeam team : teamManagerModule.getAllTeams()) {
            BWTeamStatus status = (team.getPlayers().size() > 0) ? BWTeamStatus.ALIVE : BWTeamStatus.DEAD;
            teamInfoLink.put(team, new BWTeamInfo(bedLocs.get(team), status));
        }
        for(Player p : match.getMatchInfo().getQueuedPlayers()) {
            PlayerUtil.generalReadyPlayer(p, GameMode.SURVIVAL);
            sendHowToPlay(p);
            BWUserData.applyIdealItems(getBWUserDataByPlayer(p));
        }
    }

    private void sendHowToPlay(Player p) {
        p.sendMessage(new String[]{
                ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------",
                "",
                ChatColor.GRAY + "good luck lol",
                "",
                ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------"
        });
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;

        event.setRespawnLocation(match.getMatchInfo().getSpawnArea());
        p.setGameMode(GameMode.SPECTATOR);
        if(!(match.getStatus() == GameStatus.MID)) return;
        if(playerManager.getPlayerContext(p).getInTeam() != null) {
            MatchTeam teamPlayerOn = playerManager.getPlayerContext(p).getInTeam();
            if(shouldRespawn(teamPlayerOn)) {
                respawnCycle(p);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;

        if(!blocksPlaced.contains(event.getBlock())) blocksPlaced.add(event.getBlock());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;
        if(event.getBlock().getType().equals(Material.BED) || event.getBlock().getType().equals(Material.BED_BLOCK)) {
            MatchTeam whoseBed = whoseBedIsThat(event.getBlock().getLocation());
            if(whoseBed == null) event.setCancelled(true);
            else if(playerManager.getPlayerContext(p).getInTeam().equals(whoseBed)) {
                p.sendMessage(ChatColor.RED + "You cannot break your own bed!");
                event.setCancelled(true);
            } else {
                p.sendMessage("congrats you broke someone elses bed");
                event.setDropItems(false);
            }
            return;
        }
        if(!blocksPlaced.contains(event.getBlock())) {
            p.sendMessage(ChatColor.RED + "You can only break blocks that you have placed!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Player p = (Player) event.getWhoClicked();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;
        event.setCancelled(true);
    }

    // using radius because laziness
    private MatchTeam whoseBedIsThat(Location location) {
        for (Map.Entry<MatchTeam, Location> entry : bedLocs.entrySet()) if(location.distance(entry.getValue()) <= 5) return entry.getKey();
        return null;
    }

    private boolean shouldRespawn(MatchTeam team) {
        return (teamInfoLink.get(team).getBwTeamStatus().getAliveOrder() >= 2);
    }

    private void respawnCycle(Player p) {
        respawnCountdown(p, 0);
        setRespawnTick(p, 1, true);
    }

    private void setRespawnTick(Player p, int tick, boolean checkIfSet) {
        for(BWUserData userData : bwUserData) if(userData.getPlayer() == p && (!checkIfSet || userData.getRespawnTimer() <= 0)) userData.setRespawnTimer(tick);
    }

    private BWUserData getBWUserDataByPlayer(Player p) {
        for(BWUserData userData : bwUserData) if(userData.getPlayer() == p) return userData;
        return null;
    }

    @Override
    public void tick() {
        for(BWUserData userData : bwUserData) {
            int resp = userData.getRespawnTimer();
            if(resp > 0) {
                if (resp % 20 == 0) respawnCountdown(userData.getPlayer(), resp);
                userData.setRespawnTimer(resp + 1);
                if (resp >= 100) sendPlayerToBase(userData.getPlayer());
            }
        }
    }

    @EventHandler
    public void imHungry(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        if(event.getEntity() instanceof Player) ((Player) event.getEntity()).setFoodLevel(20);
    }

    private void sendPlayerToBase(Player p) {
        setRespawnTick(p, 0, false);
        PlayerUtil.generalReadyPlayer(p, GameMode.SURVIVAL);
        MatchTeam playerOn = playerManager.getPlayerContext(p).getInTeam();
        if(playerOn == null) return;
        p.teleport(playerOn.getSpawnArea(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        BWUserData.applyIdealItems(getBWUserDataByPlayer(p));
    }

    private void respawnCountdown(Player p, int ticks) {
        int realSeconds = ticks / 20;
        realSeconds = 5 - realSeconds;
        if(realSeconds > 0) {
            ChatColor[] colorFade = {ChatColor.GREEN, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.AQUA, ChatColor.RED};
            int index = (5 - realSeconds);
            p.sendTitle("", colorFade[index] + "" + (realSeconds), 2, 15, 2);
            PlayerUtil.playSound(p, Sound.BLOCK_NOTE_HAT, 1F);
        }
    }

    public BWUserData findUserDataByPlayer(Player p) {
        for(BWUserData userData : bwUserData) if(userData.getPlayer() == p) return userData;
        return null;
    }
}
