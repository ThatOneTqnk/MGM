package me.tqnk.bw.modules.bw;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.tqnk.bw.MGM;
import me.tqnk.bw.events.MatchQuitEvent;
import me.tqnk.bw.game.GameType;
import me.tqnk.bw.game.MatchModule;
import me.tqnk.bw.match.Match;
import me.tqnk.bw.modules.bw.generator.BWGenerator;
import me.tqnk.bw.modules.periodical.Periodical;
import me.tqnk.bw.modules.scoreboard.ScoreboardManagerModule;
import me.tqnk.bw.modules.team.MatchTeam;
import me.tqnk.bw.modules.team.TeamManagerModule;
import me.tqnk.bw.status.GameStatus;
import me.tqnk.bw.user.PlayerContext;
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
    // Info related to the core of the Match (Regardless of GameType)
    private ScoreboardManagerModule scoreboardManagerModule;
    private TeamManagerModule teamManagerModule;
    private PlayerManager playerManager;
    private Match match;

    // BW Match Unique Data for players and teams
    private HashMap<MatchTeam, BWTeamInfo> teamInfoLink = new HashMap<>();
    private List<BWUserData> bwUserData = new ArrayList<>();

    // BW Match Unique Data for locations and misc
    private List<Block> blocksPlaced = new ArrayList<>();
    private List<Location> shopVillagerLocs = new ArrayList<>();
    private List<Villager> villagers = new ArrayList<>();
    private List<BWGenerator> generators = new ArrayList<>();
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

    @EventHandler
    public void onMatchQuit(MatchQuitEvent event) {
        PlayerContext ctx = event.getLeaver();
        if(ctx == null || !MatchUtil.determineMatchCorrespondence(ctx.getHost(), this.match) || ctx.getInGame().getMatchInfo().getGameType() != GameType.BEDWARS) return;
        endMeNow(ctx);
        MatchUtil.removeFromAll(ctx);
        pingTeams();
    }

    private void endMeNow(PlayerContext ctx) {
        ctx.getInTeam().remove(ctx.getHost());
        for(int x = 0; x < bwUserData.size(); x++) if(bwUserData.get(x).getPlayer() == ctx.getHost()) bwUserData.remove(x);
    }

    private void pingTeams() {
        for(Map.Entry<MatchTeam, BWTeamInfo> entry : teamInfoLink.entrySet()) {
            if(entry.getValue().getBwTeamStatus() == BWTeamStatus.NORESPAWN && entry.getKey().getPlayers().size() == 0) {
                MatchUtil.sendToQueued(this.match, new String[] {"", ChatColor.WHITE + "" + ChatColor.BOLD + entry.getKey().getChatTeamColor() + "" + entry.getKey().getDisplayName() + ChatColor.RESET + "" + ChatColor.WHITE + " team has been eliminated!", ""});
                entry.getValue().setBwTeamStatus(BWTeamStatus.DEAD);
            }
        }
    }

    private void parseRemainderJson(JsonElement elem, World world) {
        JsonObject rawData = elem.getAsJsonObject();
        if(rawData.has("teams")) {
            for(JsonElement teamElement : rawData.getAsJsonArray("teams")) {
                JsonObject teamJson = teamElement.getAsJsonObject();
                Location bedLoc = Parser.convertLocation(world, teamJson.get("bed"));
                teamInfoLink.put(teamManagerModule.getMatchTeamById(teamJson.get("id").getAsString()), new BWTeamInfo(bedLoc));
            }
        }
        if(rawData.has("shops")) {
            for(JsonElement loc : rawData.getAsJsonArray("shops")) shopVillagerLocs.add(Parser.convertLocation(world, loc));
        }
        if(rawData.has("generators")) {
            for(JsonElement gen : rawData.getAsJsonArray("generators")) {
                JsonObject genData = gen.getAsJsonObject();
                String genType = genData.get("type").getAsString();
                JsonElement genLoc = genData.get("location");
                generators.add(new BWGenerator(BWGenerator.BWGenType.valueOf(genType.toUpperCase()), Parser.convertLocation(world, genLoc)));
            }
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
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if(!MatchUtil.determineMatchCorrespondence(p, this.match)) return;
        if(event.getClickedInventory().equals(p.getInventory())) {
            // disable armor interaction
            if(event.getSlot() >= 100 && event.getSlot() <= 103) {
                event.setCancelled(true);
                return;
            }
        }
        if(event.getClickedInventory().getName().equalsIgnoreCase(shopName)) {
            event.setCancelled(true);
            shopHandle(event.getClickedInventory(), p, event.getCurrentItem(), event.getSlot());
        }
    }

    private void shopHandle(Inventory inv, Player p, ItemStack clickedItem, int clickedSlot) {
        if(clickedSlot >= 0 && clickedSlot <= 8) {
            guiNavigate(inv, clickedSlot, p);
            return;
        }
        BWUserData candidate = getBWUserDataByPlayer(p);
        if(candidate == null) return;
        BWShopItem contained = candidate.getBwShopIndex().getShopIndex().get(clickedSlot + (candidate.getBwShopIndex().getCurrentPage() * 54));
        if(contained == null) return;
        if(meetsShopReq(p, contained.getCostMaterial())) {
            if(contained.getBwLevelable() != null && contained.getBwLevelable().getMagnitude() <= candidate.getLevelContainer().getLevels().get(contained.getBwLevelable().getLevelType())) {
                p.sendMessage(ChatColor.RED + "You don't really need this");
                PlayerUtil.playSound(p, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5F);
                return;
            }
            if(contained.getBwLevelable() != null && (contained.getBwLevelable().getLevelType() == BWLevelable.BWLevel.AXE || contained.getBwLevelable().getLevelType() == BWLevelable.BWLevel.PICKAXE)) {
               if(contained.getBwLevelable().getLevelType() == BWLevelable.BWLevel.AXE) {
                   p.getInventory().remove(Material.WOOD_AXE);
                   p.getInventory().remove(Material.STONE_AXE);
                   p.getInventory().remove(Material.GOLD_AXE);
                   p.getInventory().remove(Material.IRON_AXE);
                   p.getInventory().remove(Material.DIAMOND_AXE);
               } else {
                   p.getInventory().remove(Material.WOOD_PICKAXE);
                   p.getInventory().remove(Material.STONE_PICKAXE);
                   p.getInventory().remove(Material.GOLD_PICKAXE);
                   p.getInventory().remove(Material.IRON_PICKAXE);
                   p.getInventory().remove(Material.DIAMOND_PICKAXE);
               }
            }
            if(contained.getBwLevelable() != null && contained.getBwLevelable().getLevelType() == BWLevelable.BWLevel.ARMOR) {
                if(candidate.getLevelContainer().getLevels().get(BWLevelable.BWLevel.ARMOR) >= contained.getBwLevelable().getMagnitude()) {
                    p.sendMessage(ChatColor.RED + "You don't need this!");
                    PlayerUtil.playSound(p, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5F);
                    p.closeInventory();
                    return;
                }
                PlayerUtil.equipPlayerWith(p, exchangeArmorLevel(contained.getBwLevelable().getMagnitude()), candidate.getPlayerColor());
            } else p.getInventory().addItem(contained.getItem());
            PlayerUtil.removeItems(p.getInventory(), contained.getCostMaterial().getType(), contained.getCostMaterial().getAmount());
            if(contained.getBwLevelable() != null) handleLevelChange(contained.getBwLevelable(), candidate);
            if(contained.getBwLevelable() != null && (contained.getBwLevelable().getLevelType() == BWLevelable.BWLevel.AXE || contained.getBwLevelable().getLevelType() == BWLevelable.BWLevel.PICKAXE)) {
                candidate.getBwShopIndex().updateIndex();
                candidate.getBwShopIndex().applyIndexToInventory(inv, candidate.getBwShopIndex().getCurrentPage());
            }
            PlayerUtil.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1.5F);
            p.updateInventory();
        } else {
            p.sendMessage(ChatColor.RED + "You don't have enough materials to purchase this!");
            PlayerUtil.playSound(p, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5F);
        }
    }

    private String exchangeArmorLevel(int level) {
        switch (level) {
            case 3:
                return "diamond";
            case 2:
                return "iron";
            case 1:
                return "chainmail";
            default:
                return "leather";
        }
    }

    private void handleLevelChange(BWLevelable levelable, BWUserData data) {
        if(levelable.getMagnitude() > data.getLevelContainer().getLevels().get(levelable.getLevelType())) data.getLevelContainer().getLevels().put(levelable.getLevelType(), levelable.getMagnitude());
        data.refreshIdealDefaults();
    }

    private void guiNavigate(Inventory inv, int clickedSlot, Player p) {
        populateGui(inv, p, clickedSlot);
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
        candidate.getBwShopIndex().setCurrentPage(pageNumber);
        candidate.getBwShopIndex().updateIndex();
        candidate.getBwShopIndex().applyIndexToInventory(yee, pageNumber);
        populateGuiWithDefaults(yee, pageNumber);
    }

    private void populateGuiWithDefaults(Inventory yee, int pageNumber) {
        ItemStack someglazz = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta detail = someglazz.getItemMeta();
        detail.setDisplayName(ChatColor.RESET + "");
        for(int x = 9; x <= 17; x++) yee.setItem(x, someglazz);
        yee.setItem(pageNumber + 9, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
        yee.setItem(0, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.YELLOW + "Quick Buy", Collections.singletonList(ChatColor.GRAY + "Buy " + ChatColor.AQUA + "convenient items" + ChatColor.GRAY + " here!")));
        yee.setItem(1, ItemUtil.createItem(Material.HARD_CLAY, ChatColor.YELLOW + "Building Blocks", Collections.singletonList(ChatColor.GRAY + "Buy " + ChatColor.GREEN + "blocks " + ChatColor.GRAY + "here!")));
        yee.setItem(2, ItemUtil.createItem(Material.GOLD_SWORD, ChatColor.YELLOW + "Weaponry", Collections.singletonList(ChatColor.GRAY + "Purchase some fine " + ChatColor.AQUA + "weapons")));
        yee.setItem(3, ItemUtil.createItem(Material.CHAINMAIL_BOOTS, ChatColor.YELLOW + "Armor", Collections.singletonList(ChatColor.GRAY + "Purchase " + ChatColor.AQUA + "armor" + ChatColor.GRAY + " here")));
        yee.setItem(4, ItemUtil.createItem(Material.STONE_PICKAXE, ChatColor.YELLOW + "Tools", Collections.singletonList(ChatColor.GOLD + "Tools " + ChatColor.GRAY + "can be found here")));
        yee.setItem(5, ItemUtil.createItem(Material.BOW, ChatColor.YELLOW + "Projectiles", Collections.singletonList(ChatColor.GOLD + "Shoot 'em all")));
        yee.setItem(6, ItemUtil.createItem(Material.BREWING_STAND_ITEM, ChatColor.YELLOW + "Potions", Collections.singletonList(ChatColor.GREEN + "Dope")));
        yee.setItem(7, ItemUtil.createItem(Material.TNT, ChatColor.YELLOW + "Special Items", Collections.singletonList(ChatColor.GRAY + "They had nowhere else to go")));
        yee.setItem(8, ItemUtil.createItem(Material.DIAMOND, ChatColor.YELLOW + "Team Upgrades", Collections.singletonList(ChatColor.GRAY + "Buy " + ChatColor.GOLD + "team upgrades" + ChatColor.GRAY + " here")));
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
            teamInfoLink.get(team).setBwTeamStatus(status);
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

    private void breakBed(Player p, MatchTeam team) {
        BWTeamInfo teamInfo = teamInfoLink.get(team);
        PlayerContext ctx = playerManager.getPlayerContext(p);
        if(teamInfo == null || ctx == null || ctx.getInTeam() == null || teamInfo.getBwTeamStatus().getAliveOrder() <= 1) return;
        MatchTeam coolerTeam = ctx.getInTeam();
        String brokeBed = ChatColor.WHITE.toString() + ChatColor.BOLD + "> " + coolerTeam.getChatTeamColor() + p.getName() + ChatColor.GRAY + " destroyed " + team.getChatTeamColor() + team.getDisplayName() + ChatColor.GRAY + "'s bed!";
        for(Player playa : match.getMatchInfo().getQueuedPlayers()) {
            playa.sendMessage("");
            playa.sendMessage(brokeBed);
            playa.sendMessage("");
        }
        MatchUtil.playToQueued(match, Sound.ENTITY_ENDERDRAGON_GROWL, 1F);
        teamInfo.setBwTeamStatus(BWTeamStatus.NORESPAWN);
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
                breakBed(p, whoseBed);
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
        for(Map.Entry<MatchTeam, BWTeamInfo> entry : teamInfoLink.entrySet()) if(location.distance(entry.getValue().getBedLocation()) <= 5) return entry.getKey();
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
