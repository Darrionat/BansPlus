package me.Darrionat.BansPlus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.Darrionat.BansPlus.Commands.Ban;
import me.Darrionat.BansPlus.Commands.IPBan;
import me.Darrionat.BansPlus.Commands.TempBan;
import me.Darrionat.BansPlus.Commands.Unban;
import me.Darrionat.BansPlus.Files.FileManager;
import me.Darrionat.BansPlus.Handlers.ConfigBansManager;
import me.Darrionat.BansPlus.Handlers.ConfigIPBansManager;
import me.Darrionat.BansPlus.Handlers.DatabaseBansManager;
import me.Darrionat.BansPlus.Handlers.DatabaseIPBansManager;
import me.Darrionat.BansPlus.Listeners.PlayerLogin;
import me.Darrionat.BansPlus.Listeners.PlayerLoginIP;
import me.Darrionat.BansPlus.UI.BanUI;
import me.Darrionat.BansPlus.Utils.Utils;

public class Main extends JavaPlugin {

	FileConfiguration config;
	public boolean mysqlEnabled;

	public void onEnable() {
		this.config = this.getConfig();
		// Initializing classes and GUI
		BanUI.initialize(this);
		new Ban(this);
		new TempBan(this);
		new IPBan(this);
		new Unban(this);
		
		new PlayerLogin(this);
		new PlayerLoginIP(this);

		new ConfigBansManager(this);
		new ConfigIPBansManager(this);
		DatabaseBansManager dbManager = new DatabaseBansManager(this);
		DatabaseIPBansManager dbIPManager = new DatabaseIPBansManager(this);

		saveConfigs();
		if (config.getBoolean("MySQL.Enabled")) {
			mysqlSetup();
			dbManager.createBansTable();
			dbIPManager.createIPBansTable();
			mysqlEnabled = true;
		} else {
			mysqlEnabled = false;
		}

	}

	public void onDisable() {

	}

	public void saveConfigs() {
		// Save Files
		FileManager fileManager = new FileManager(this);
		if (fileManager.fileExists("bannedplayers") == false) {
			fileManager.setup("bannedplayers");
		}
		if (fileManager.fileExists("bannedips") == false) {
			fileManager.setup("bannedips");
		}
		saveDefaultConfig();
	}

	private Connection connection;
	public String host, database, username, password, bansTable, ipBansTable;
	public int port;

	public void mysqlSetup() {
		host = config.getString("MySQL.host");
		port = config.getInt("MySQL.port");
		database = config.getString("MySQL.database");
		username = config.getString("MySQL.username");
		password = config.getString("MySQL.password");
		bansTable = "punishments_bans";
		ipBansTable = "punishments_ip_bans";
		try {
			synchronized (this) {
				if (getConnection() != null && !getConnection().isClosed()) {
					return;
				}
			}
			Class.forName("com.mysql.jdbc.Driver");
			setConnection(DriverManager.getConnection(
					"jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, ""));
			// setConnection(DriverManager.getConnection(
			// "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database,
			// this.username, this.password));
			System.out.println(Utils.chat("&a" + this.getDescription().getName() + " MySQL Connected"));
		} catch (SQLException exe) {
			exe.printStackTrace();
			disableMySQL();
		} catch (ClassNotFoundException exe) {
			exe.printStackTrace();
			disableMySQL();
		}

	}

	public void disableMySQL() {
		mysqlEnabled = false;
		System.out.println(Utils.chat("&cFailed to connect. Turning off MySQL, and using bannedplayers.yml"));
		System.out.println(Utils.chat(
				"&4CAUTION: &cIf your database's data is not the same as bannedplayers.yml, banned players will be able to join!"));
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
