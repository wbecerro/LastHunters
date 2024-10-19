package wbe.lastHunters.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.items.Bow;
import wbe.lastHunters.items.Catalyst;
import wbe.lastHunters.items.CatalystType;
import wbe.lastHunters.items.Rod;
import wbe.lastHunters.util.Utilities;

public class CommandListener implements CommandExecutor {

    private LastHunters plugin = LastHunters.getInstance();

    private Utilities utilities = new Utilities();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("LastHunters")) {
            Player player = null;
            if(sender instanceof Player) {
                player = (Player) sender;
            }

            if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
                if(!sender.hasPermission("lasthunters.command.help")) {
                    sender.sendMessage(LastHunters.messages.noPermission);
                    return false;
                }

                for(String line : LastHunters.messages.help) {
                    sender.sendMessage(line.replace("&", "§"));
                }
            } else if(args[0].equalsIgnoreCase("rod")) {
                if(!sender.hasPermission("lasthunters.command.rod")) {
                    sender.sendMessage(LastHunters.messages.noPermission);
                    return false;
                }

                if(args.length < 2) {
                    sender.sendMessage(LastHunters.messages.notEnoughArgs);
                    sender.sendMessage(LastHunters.messages.rodArguments);
                    return false;
                }

                int chance = Integer.parseInt(args[1]);
                if(args.length > 2) {
                    player = Bukkit.getPlayer(args[2]);
                }

                Rod rod = new Rod(chance);
                if(player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), rod);
                } else {
                    player.getInventory().addItem(rod);
                }
            } else if(args[0].equalsIgnoreCase("bow")) {
                if(!sender.hasPermission("lasthunters.command.bow")) {
                    sender.sendMessage(LastHunters.messages.noPermission);
                    return false;
                }

                if(args.length < 2) {
                    sender.sendMessage(LastHunters.messages.notEnoughArgs);
                    sender.sendMessage(LastHunters.messages.bowArguments);
                    return false;
                }

                int uses = Integer.parseInt(args[1]);
                if(args.length > 2) {
                    player = Bukkit.getPlayer(args[2]);
                }

                Bow bow = new Bow(uses);
                if(player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), bow);
                } else {
                    player.getInventory().addItem(bow);
                }
            } else if(args[0].equalsIgnoreCase("catalyst")) {
                if(!sender.hasPermission("lasthunters.command.catalyst")) {
                    sender.sendMessage(LastHunters.messages.noPermission);
                    return false;
                }

                if(args.length < 2) {
                    sender.sendMessage(LastHunters.messages.notEnoughArgs);
                    sender.sendMessage(LastHunters.messages.catalystArguments);
                    return false;
                }

                String catalystType = args[1];
                if(args.length > 2) {
                    player = Bukkit.getPlayer(args[2]);
                }

                CatalystType type = utilities.searchCatalyst(catalystType);
                if(type == null) {
                    sender.sendMessage(LastHunters.messages.undefinedHead.replace("%head%", catalystType));
                    return false;
                }
                Catalyst catalyst = new Catalyst(type);
                if(player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), catalyst);
                } else {
                    player.getInventory().addItem(catalyst);
                }
            } else if(args[0].equalsIgnoreCase("fishChance")) {
                if(!sender.hasPermission("lasthunters.command.fishChance")) {
                    sender.sendMessage(LastHunters.messages.noPermission);
                    return false;
                }

                if(args.length < 2) {
                    sender.sendMessage(LastHunters.messages.notEnoughArgs);
                    sender.sendMessage(LastHunters.messages.fishChanceArguments);
                }

                ItemStack item = player.getInventory().getItemInMainHand();
                utilities.addRodChance(item, Integer.parseInt(args[1]));
                sender.sendMessage(LastHunters.messages.addedFishChance);
            } else if(args[0].equalsIgnoreCase("doubleChance")) {
                if(!sender.hasPermission("lasthunters.command.doubleChance")) {
                    sender.sendMessage(LastHunters.messages.noPermission);
                    return false;
                }

                if(args.length < 2) {
                    sender.sendMessage(LastHunters.messages.notEnoughArgs);
                    sender.sendMessage(LastHunters.messages.doubleChanceArguments);
                }

                ItemStack item = player.getInventory().getItemInMainHand();
                utilities.addDoubleChance(item, Integer.parseInt(args[1]));
                sender.sendMessage(LastHunters.messages.addedDoubleChance);
            } else if(args[0].equalsIgnoreCase("reload")) {
                if(!sender.hasPermission("lasthunters.command.reload")) {
                    sender.sendMessage(LastHunters.messages.noPermission);
                    return false;
                }

                plugin.reloadConfiguration();
                sender.sendMessage(LastHunters.messages.reload);
            } else if(args[0].equalsIgnoreCase("addReward")) {
                if(!sender.hasPermission("lasthunters.command.addReward")) {
                    sender.sendMessage(LastHunters.messages.noPermission);
                    return false;
                }

                if(args.length < 3) {
                    sender.sendMessage(LastHunters.messages.notEnoughArgs);
                    sender.sendMessage(LastHunters.messages.addRewardArguments);
                }

                String rarity = args[1];
                String id = args[2];
                ItemStack item = player.getInventory().getItemInMainHand();
                utilities.addReward(rarity, id, item);
                player.sendMessage(LastHunters.messages.addedReward.replace("%rarity%", rarity));
            }
        }

        return false;
    }
}
