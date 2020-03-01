package me.Darrionat.BansPlus.Handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import me.Darrionat.BansPlus.Main;

public class DatabaseIPBansManager {

	private Main plugin;

	public DatabaseIPBansManager(Main plugin) {
		this.plugin = plugin;
	}

	public void createIPBansTable() {
		try {
			// IPv4 000.000.000.000 (15 characters)
			// IPv6 2001:0db8:0000:0000:0000:8a2e:0370:7334 (39 characters)
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("CREATE TABLE IF NOT EXISTS " + plugin.ipBansTable
							+ " (IP varchar(39), Start varchar(40),Reason varchar(500),BannedBy varchar(16))");

			statement.execute();

		} catch (SQLException exe) {
			exe.printStackTrace();
		}

	}

	public void createIP(String ip, Date startDate, String reason, String bannedBy) {
		try {
			// Replaces the person
			if (ipExists(ip)) {
				PreparedStatement statement = plugin.getConnection()
						.prepareStatement("DELETE FROM " + plugin.ipBansTable + " WHERE IP=?");
				statement.setString(1, ip);
				statement.execute();
			}

			PreparedStatement insert = plugin.getConnection().prepareStatement(
					"INSERT INTO " + plugin.ipBansTable + "(IP, START, REASON, BANNEDBY) VALUE (?,?,?,?)");
			insert.setString(1, ip);
			insert.setString(2, startDate.toString());
			insert.setString(3, reason);
			insert.setString(4, bannedBy);
			insert.executeUpdate();
			// IP inserted now
		} catch (SQLException exe) {
			exe.printStackTrace();
		}
	}

	public boolean ipExists(String ip) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.ipBansTable + " WHERE IP=?");
			statement.setString(1, ip);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				// the ip was found
				return true;
			}
			// Player not found
		} catch (SQLException exe) {
			exe.printStackTrace();
		}
		return false;
	}

	public void removeIP(String ip) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("DELETE FROM " + plugin.ipBansTable + " WHERE IP=?");
			statement.setString(1, ip);
			statement.execute();

		} catch (SQLException exe) {
			exe.printStackTrace();
		}
	}

	public String getStartTime(String ip) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.ipBansTable + " WHERE IP=?");

			statement.setString(1, ip);
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("START");

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return null;
	}

	public String getReason(String ip) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.ipBansTable + " WHERE IP=?");

			statement.setString(1, ip);
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("REASON");

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return null;
	}

	public String getBannedBy(String ip) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.ipBansTable + " WHERE IP=?");

			statement.setString(1, ip);
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("BANNEDBY");

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return null;
	}

}