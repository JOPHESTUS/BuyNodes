package me.jophestus.buynodes;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BuyNodes extends JavaPlugin {
    Logger log = this.getLogger();
    PluginDescriptionFile pdfFile;
    public static BuyNodes plugin;
    public static Economy econ = null;
    public static Permission perms = null;

    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE,
                    " Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupConfig();

    }

    public void mbaxterIsTheBest() {

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer()
                .getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private void setupConfig() {
        // TODO Auto-generated method stub
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        // TODO Auto-generated method stub

        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("buynode")) {
            if (sender.hasPermission("buynodes.buy")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GOLD + "[BuyNodes] "
                            + ChatColor.GREEN
                            + "You haven't entered enough args.");
                    player.sendMessage(ChatColor.GOLD + "[BuyNodes] "
                            + ChatColor.GREEN
                            + "Usage: /buynode [permission node]");
                    return true;
                }

                String bfnode = args[0];
                String node = args[0];
                bfnode = bfnode.replace(".", "%");
                String buymessage;
                String alreadyhas;
                String notenough;
                String notforsale;
                if (!getConfig().contains(bfnode + ".buymessage")) {
                    buymessage = ChatColor.GOLD + "[BuyNodes] " + ChatColor.GREEN + "You have successfully bought " + node;
                } else {
                    buymessage = getConfig().getString(bfnode + ".buymessage");
                    buymessage = buymessage.replace("&", "§");
                    buymessage = buymessage.replace("%n", node);
                }

                if (!getConfig().contains(bfnode + ".alreadyhas")) {
                    alreadyhas = ChatColor.GOLD + "[BuyNodes] " + ChatColor.GREEN + "You already have that permission";
                } else {
                    alreadyhas = getConfig().getString(bfnode + ".alreadyhas");
                    alreadyhas = alreadyhas.replace("&", "§");
                    alreadyhas = alreadyhas.replace("%n", node);
                }

                if (!getConfig().contains(bfnode + ".notenough")) {
                    notenough = ChatColor.GOLD + "[BuyNodes] " + ChatColor.GREEN + "You can't afford that";
                } else {
                    notenough = getConfig().getString(bfnode + ".notenough");
                    notenough = notenough.replace("&", "§");
                    notenough = notenough.replace("%n", node);
                }

                if (!getConfig().contains(bfnode + ".notforsale")) {
                    notforsale = ChatColor.GOLD + "[BuyNodes] " + ChatColor.GREEN + "That permission node is not for sale";
                } else {
                    notforsale = getConfig().getString(bfnode + ".notforsale");
                    notforsale = notforsale.replace("&", "§");
                    notforsale = notforsale.replace("%n", node);
                }
                int price = getConfig().getInt(bfnode + ".price");


                if (!getConfig().contains(bfnode + ".perm") || player.hasPermission(getConfig().getString(bfnode + ".perm"))) {


                    if (!getConfig().contains(bfnode + ".price")) {
                        player.sendMessage(notforsale);

                    } else {
                        if (econ.has(player.getName(), price)) {
                            if (sender.hasPermission(node)) {
                                player.sendMessage(alreadyhas);
                            } else {

                                econ.withdrawPlayer(player.getName(), price);

                                perms.playerAdd(player, node);
                                player.sendMessage(buymessage);

                            }
                        } else {
                            player.sendMessage(notenough);
                        }
                    }
                } else {
                    player.sendMessage(notforsale);
                }

            } else {
                player.sendMessage(ChatColor.GOLD + "[BuyNodes] "
                        + ChatColor.GREEN
                        + "You don't have permission to do that.");
                return true;
            }
        }

        return super.onCommand(sender, command, label, args);

    }


}