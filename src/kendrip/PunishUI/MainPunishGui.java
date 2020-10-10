/*
 * Name: Punishment GUI Plugin
 * Version: 1.0
 * Author: Kendrip
 * Description: This plugin is the GUI version of Bukkit/Spigot Punishments
 */

package kendrip.PunishUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class MainPunishGui extends JavaPlugin implements Listener {
    //    Punishment Variables
    private String defaultWarnReason = "Camping ";
    private String defaultKickReason = "Camping ";
    private String defaultMuteReason = "Toxicity";
    private String defaultMuteDuration = "6h";
    private String defaultBanReason = "Cheating";
    private String defaultBanDuration = "30d";
    private int banPunishHistory;
    //    Command
    private String[] command = {"ui", "rr", "BanHistory", "bh"};
    //    Variables
    private Inventory inv;
    private String punishName = null;
    private List<String> lore = new ArrayList<>();
    private HashMap<String, String> banList = new HashMap<>();
//    Clipboard

    @Override
    public void onEnable() {
        makeInventory();
        registerEvents();
        banHashMap();
    }

    @Override
    public void onDisable() {
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

//         UI Command
        if (label.equalsIgnoreCase(command[0])) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This is a player only command!");
                return true;
            }
//             If command sender is player
            Player player = ((Player) sender);
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage( "https://" + player.getUniqueId().toString().replace('-', '.'));
                    return false;
                } else {
                    punishName = args[0];
                    player.sendMessage(ChatColor.RED + "Punishing " + ChatColor.YELLOW + args[0]);
                    player.openInventory(inv);
                }
            }
            return true;
        }

//        RR Command
        if (label.equalsIgnoreCase(command[1])) {
            Player player = ((Player) sender);
            if (args.length == 1 && args[0] != null) {
                player.chat("/msg " + args[0] + " https://youtube.com/kendrip");
                return true;
            }
        }

//        BanHistory Command
        if (label.equalsIgnoreCase(command[2]) || label.equalsIgnoreCase(command[3])) {
            if (!(sender instanceof Player)) {
                if (args.length == 1) {
                    sender.sendMessage(args[0] + " currently has " + ChatColor.RED + getBanPunishHistory());
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + command[2] + " <player> get");
                    sender.sendMessage(ChatColor.RED + "Usage: /" + command[2] + " <player> set <Ban History>");
                }
            }
            // If sender is player
            Player player = ((Player) sender);
            if (args.length == 2 && args[1].equalsIgnoreCase("get")) {
                player.sendMessage(args[0] + " currently has " + ChatColor.RED + getBanPunishHistory() + ChatColor.WHITE + " ban(s)");
                return true;
            }
            if (args.length == 3 && args[1].equalsIgnoreCase("set")) {
                setBanPunishHistory(args[2]);
                player.sendMessage(args[0] + " ban(s) has been set  to " + ChatColor.RED + "" + getBanPunishHistory());
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        try {
            if (!event.getInventory().equals(inv)) return;
            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().getItemMeta() == null) return;
//        Removing this fixed a bug idk why but i left it this way.
//         if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
            event.setCancelled(true);

            Player player = ((Player) event.getWhoClicked());
//         Ban

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Ban")) {
                if (event.getCurrentItem().getItemMeta().hasLore()) {
//                    TODO Get ban history from a database plugin creates.
                    switch (banPunishHistory) {
                        case 0:
                            player.chat(banList.get("Ban0"));
                            break;
                        case 1:
                            player.chat(banList.get("Ban1"));
                            break;
                        case 2:
                            player.chat(banList.get("Ban2"));
                            break;
                        case 3:
                            player.chat(banList.get("Ban3"));
                            break;
                        default:
                            player.chat(banList.get("BanPerm"));
                            break;
                    }
//                    TODO GEt Punish name from command properly.
                    if (punishName.equalsIgnoreCase("Kendrip")) banPunishHistory++;
                }
            }
//         Mute
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Mute")) {
                if (event.getCurrentItem().getItemMeta().hasLore()) {
                    player.chat(("/mute " + punishName + " " + defaultMuteDuration + " " + defaultMuteReason));
                    player.closeInventory();
                }
            }
//         Warn
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Warn")) {
                if (event.getCurrentItem().getItemMeta().hasLore()) {
                    player.chat("/warn " + punishName + " " + defaultWarnReason);
                    player.closeInventory();
                }
            }
//         Kick
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Kick")) {
                if (event.getCurrentItem().getItemMeta().hasLore()) {
                    player.chat("/kick " + punishName + " " + defaultKickReason);
                    player.closeInventory();
                }
            }
//        Close Menu
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Close")) {
                if (event.getCurrentItem().getItemMeta().hasLore()) {
                    player.closeInventory();
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void makeInventory() {
        inv = Bukkit.createInventory(null, 36, ChatColor.RED + "Punish GUI");

//         Ban Player
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Ban");
        lore.add(ChatColor.WHITE + "Ban's a player from the network.");
        lore.add(ChatColor.GRAY + "Duration: " + ChatColor.RED + defaultBanDuration);
        lore.add(ChatColor.GRAY + "Reason: " + ChatColor.RED + defaultBanReason);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(16, item);

//         Mute Player
        item.setType(Material.BOOK_AND_QUILL);
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Mute");
        lore.clear();
        lore.add(ChatColor.WHITE + "Mute's a player on the network.");
        lore.add(ChatColor.GRAY + "Duration: " + ChatColor.RED + defaultMuteDuration);
        lore.add(ChatColor.GRAY + "Reason: " + ChatColor.RED + defaultMuteReason);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(12, item);

//         Warn Player
        item.setType(Material.PAPER);
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Warn");
        lore.clear();
        lore.add(ChatColor.WHITE + "Warn's a player on the network.");
        lore.add(ChatColor.GRAY + "Reason: " + ChatColor.RED + defaultWarnReason);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(10, item);

//         Kick Player
        item.setType(Material.GLOWSTONE_DUST);
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Kick");
        lore.clear();
        lore.add(ChatColor.WHITE + "Kick's a player from the network.");
        lore.add(ChatColor.GRAY + "Reason: " + ChatColor.RED + defaultKickReason);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(14, item);

//         Close Menu
        item.setType(Material.BARRIER);
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Close");
        lore.clear();
        lore.add(ChatColor.GRAY + "Close Punish GUI");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(31, item);
    }

    private void banHashMap() {
        banList.put("Ban0", "/ban " + "Kendrip" + " " + "7d" + " " + defaultBanReason);
        banList.put("Ban1", "/ban " + "Kendrip" + " " + defaultBanDuration + " " + defaultBanReason);
        banList.put("Ban2", "/ban " + "Kendrip" + " " + "60d" + " " + defaultBanReason);
        banList.put("Ban3", "/ban " + "Kendrip" + " " + "120d" + " " + defaultBanReason);
        banList.put("BanPerm", "/ban " + "Kendrip");
    }

    private int getBanPunishHistory() {
        return banPunishHistory;
    }

    private void setBanPunishHistory(String banPunishHistory) {
        try {
            this.banPunishHistory = Integer.parseInt(banPunishHistory);
        } catch (Exception ignored) {
//      Am not  too sure what to do here if  "args[2]" is a String
//      This is primarily because i don't how to send the player that ran the command a message.
//      If i did know then i would use the line  below.
//      player.sendMessage(ChatColor.RED + "Please use number to edit ban history.");
        }
    }
}
