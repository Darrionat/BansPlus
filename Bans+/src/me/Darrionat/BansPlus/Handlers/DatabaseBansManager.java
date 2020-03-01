package me.Darrionat.BansPlus.Handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import me.Darrionat.BansPlus.Main;

public class DatabaseBansManager {

	private Main plugin;

	public DatabaseBansManager(Main plugin) {
		this.plugin = plugin;
	}

	public void createBansTable() {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ plugin.bansTable
					+ " (UUID char(36), Start varchar(40), End varchar(40), Reason varchar(500),BannedBy varchar(16))");
			statement.execute();

		} catch (SQLException exe) {
			exe.printStackTrace();
		}
	}

	public void createPlayer(OfflinePlayer bPlayer, Date startDate, Date endDate, String reason, String bannedBy) {
		UUID uuid = bPlayer.getUniqueId();
		try {
			// Replaces the person
			if (playerExists(uuid.toString())) {
				PreparedStatement statement = plugin.getConnection()
						.prepareStatement("DELETE FROM " + plugin.bansTable + " WHERE UUID='" + uuid.toString() + "'");
				statement.execute();
			}
			PreparedStatement insert = plugin.getConnection().prepareStatement(
					"INSERT INTO " + plugin.bansTable + "(UUID, START, END, REASON, BANNEDBY) VALUE (?,?,?,?,?)");
			insert.setString(1, uuid.toString());
			insert.setString(2, startDate.toString());
			if (endDate == null) {
				insert.setString(3, "Permanent");
			} else {
				insert.setString(3, endDate.toString());
			}
			insert.setString(4, reason);
			insert.setString(5, bannedBy);
			insert.executeUpdate();

			// Player inserted now

		} catch (SQLException exe) {
			exe.printStackTrace();
		}
	}

	public boolean playerExists(String uuid) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.bansTable + " WHERE UUID=?");
			statement.setString(1, uuid);

			ResultSet results = statement.executeQuery();
			if (results.next()) {
				// the player was found
				return true;
			}
			// Player not found
		} catch (SQLException exe) {
			exe.printStackTrace();
		}
		return false;
	}

	public void removePlayer(String uuid) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("DELETE FROM " + plugin.bansTable + " WHERE UUID=?");
			statement.setString(1, uuid);
			statement.execute();

		} catch (SQLException exe) {
			exe.printStackTrace();
		}
	}

	public String getStartTime(String uuid) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.bansTable + " WHERE UUID=?");

			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("START");

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return null;
	}

	public String getEndTime(String uuid) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.bansTable + " WHERE UUID=?");

			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("END");

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return null;
	}

	public String getReason(String uuid) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.bansTable + " WHERE UUID=?");

			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("REASON");

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return null;
	}

	public String getBannedBy(String uuid) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.bansTable + " WHERE UUID=?");

			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			return results.getString("BANNEDBY");

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return null;
	}

}
