package com.chaoticcade.CSLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent.TransactionType;
import com.Acrobot.ChestShop.UUIDs.NameManager;

public class cslogger extends JavaPlugin implements Listener{
	
    @Override
    public void onEnable() {
       System.out.println("CSLogger Online!");
       getServer().getPluginManager().registerEvents(this, this);
       getDataFolder().mkdir(); //TODO Update so it checks and doesn't blindly create folder
    }

    @Override
    public void onDisable() {
       System.out.println("CSLogger Disabled!");
    }

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("csl")) {
        	
        	if (args[0].equalsIgnoreCase("page")) {
        		
        		if (isStringInt(args[1]) == false) {
        			sender.sendMessage("Invalid page number! Must use a number!");
        		}
        		else {
        			try {
        				int pageNumber = Integer.parseInt(args[1]);
        				readLog(sender, pageNumber);
        				} 
        			catch (Exception e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        				}
        			}
        		return true;
        		}
        	if (args[0].equalsIgnoreCase("del")) {
        		//delete file
    			File playerFile = new File(getDataFolder() + "\\" + ((Player) sender).getPlayer().getUniqueId().toString() + ".log");
    			Path playerPath = playerFile.toPath();
    			if (playerFile.exists() == true) {
    				try {
    					Files.delete(playerPath);
    					sender.sendMessage("Deleting log file.");
    					} 
    				catch (IOException e) {
    					// TODO Auto-generated catch block
    					System.out.println("Unable to delete " + playerFile + "!");
    					e.printStackTrace();
    					}
    				finally {
    					//do nothing
    				}
    			}
    				else {
    					sender.sendMessage("No transaction data to delete!");
    				}
    			
    				return true;
        	}
        		}
        	else {
        		sender.sendMessage("Invalid command!");
        		return true;
        	}
        	
        	return false;
        	
	}
        	
	
    
    @EventHandler
    public void onJoin(PlayerJoinEvent plo) {
    	//check if player has file and send them message if they do.
    	
        File playerFile = new File(getDataFolder() + "\\" + (plo.getPlayer().getUniqueId().toString() + ".log"));
        if (playerFile.exists() == true) {
        	plo.getPlayer().sendMessage("You have offline sales records!");
        	}
    	}
    
    
    
    @EventHandler
    public void onChestShopTransaction(TransactionEvent e) throws Exception {
    	UUID shopOwnerUUID = UUIDFetcher.getUUIDOf(e.getOwnerAccount().getName());
    	Player shopOwner = Bukkit.getPlayer(e.getOwnerAccount().getName());
    	if (NameManager.isAdminShop(shopOwnerUUID) == true){
    		//Do nothing if shop is AdminShop
    		}
    	else {
    		
    		if (shopOwner != null ) {
    		//If player is online - do nothing.
    		}
    		else {
    	
    			String playerFile =  (getDataFolder() + "\\" + shopOwnerUUID.toString() + ".log");
    			File file = new File(playerFile);
    			if (file.createNewFile()) {
    				System.out.println("Transaction log file has been created for " + e.getOwnerAccount().getName() + ".");
    			} 
    			
    			else {
    			}   	 	
    			
    			String saleType = null;
    			if (e.getTransactionType() == TransactionType.BUY) {
    				saleType = "bought from you";
    			}
    			
    			if (e.getTransactionType() == TransactionType.SELL) {
    				saleType = "sold to you";
    			}
    			
    			String shopClient = e.getClient().getName();
    			Double itemPrice = e.getPrice();
    			String itemQuantity = e.getSign().getLine(1);
    			String itemName = e.getSign().getLine(3);
    			String logEntry = (shopClient + " " + saleType + " " + itemName + " x" + itemQuantity + " for " + itemPrice + "c." + '\n');   

    			//File file1 = new File(playerFile);
    			FileWriter fr = new FileWriter(file, true);
    			fr.write(logEntry);
    			fr.close();
    		}
    	}
    }
    
    public boolean isStringInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }
    
    private void readLog(CommandSender sender, int pageNumber) throws Exception {
	
    	Map<String, String> transactionData = new HashMap<String, String>();
    	String file = (getDataFolder() + "\\" + (((Player) sender).getUniqueId()).toString() + ".log");
    	BufferedReader br = new BufferedReader(new FileReader(file));  
    	String line = null;
    	int key = 0;
    	while ((line = br.readLine()) != null) {
    		String keyString = Integer.toString(key);
    		transactionData.put(keyString, line);
    		key++;
    	} 
    	br.close();

    	//Iterate from startLine to endLine
    	int startLine = ((pageNumber - 1) * 5);
    	int endLine = (pageNumber * 5);
    	
    	while (startLine < endLine) {
    		if (transactionData.get(Integer.toString(startLine)) != null) {
    		sender.sendMessage((startLine + 1) +  ". " + transactionData.get(Integer.toString(startLine)));
    		startLine++;
    		}
    		else {
    			sender.sendMessage("End of data.");
    		break;
    		}
    	}
    	
    	
    }
    	
}


    
    





