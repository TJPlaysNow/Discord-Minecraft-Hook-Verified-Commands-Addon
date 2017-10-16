package com.pzg.www.minecrafthook.verifycommands.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.pzg.www.api.config.Config;
import com.pzg.www.discord.object.CommandMethod;
import com.pzg.www.discord.object.Method;
import com.pzg.www.minecrafthook.events.UserVerifyEvent;
import com.pzg.www.minecrafthook.main.APILink;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class PluginMain extends JavaPlugin implements Listener {
	
	private APILink mchApi;
	
	private Config config;
	
	private List<String> commands;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		mchApi = new APILink(this);
		reloadConfig();
		
		if (!config.getConfig().getBoolean("Verify.Active", false)) {
			commands = new ArrayList<String>();
			commands.add("/say Hello, this is a test command!");
			commands.add("/say Player: {PlayerName} just verified there discord: {DiscordName}!");
			config.getConfig().set("Verify.Active", true);
			config.getConfig().set("Verify.Commands", commands);
			config.saveConfig();
			reloadConfig();
		}
		
		commands = config.getConfig().getStringList("Verify.Commands");
		
		mchApi.getBot().addCommand(new CommandMethod("addvc", Permissions.ADMINISTRATOR.toString(), new Method() {
			@Override
			public void method(IUser user, IChannel channel, IGuild guild, String label, List<String> args, IMessage message) {
				String command = message.getContent();
				command = command.replace("!addvc `", "");
				command = command.replace("`", "");
				commands.add(command);
				config.getConfig().set("Verify.Commands", commands);
				config.saveConfig();
				reloadConfig();
			}
		}));
	}
	
	@EventHandler
	public void userVerifed(UserVerifyEvent event) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(event.getMinecraftPlayerUUID());
		IUser du = event.getUser();
		String playerName = "";
		if (op != null)
			playerName = op.getName();
		String discordUserName = "";
		if (du != null)
			discordUserName = du.getName();
		for (String command : commands) {
			String com = command.replace("{PlayerName}", playerName).replace("{DiscordName}", discordUserName).replace("/", "");
			System.out.println("Running command: \"" + com + "\".");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), com);
		}
	}
	
	public void reloadConfig() {
		config = new Config("plugins/Minecraft Hook", "Config.yml", this);
	}
}