package me.Darrionat.BansPlus.Handlers.Mutes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import me.Darrionat.BansPlus.Main;

public class DatabaseMutesManager {

	private Main plugin;

	public DatabaseMutesManager(Main plugin) {
		this.plugin = plugin;
	}

	public void createMutesTable() {
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "
					+ plugin.mutesTable
					+ " (UUID char(36), Start varchar(40), End varchar(40), Reason varchar(500),Username varchar(16),MutedBy varchar(16))");
			statement.execute();

		} catch (SQLException exe) {
			exe.printStackTrace();
		}
	}

	public void createPlayer(OfflinePlayer bPlayer, Date startDate, Date endDate, String reason, String username,
			String mutedBy) {
		UUID uuid = bPlayer.getUniqueId();
		try {
			// Replaces the person
			if (playerExists(uuid.toString())) {
				PreparedStatement statement = plugin.getConnection()
						.prepareStatement("DELETE FROM " + plugin.mutesTable + " WHERE UUID='" + uuid.toString() + "'");
				statement.execute();
			}
			PreparedStatement insert = plugin.getConnection().prepareStatement("INSERT INTO " + plugin.mutesTable
					+ "(UUID, START, END, REASON,USERNAME, MUTEDBY) VALUE (?,?,?,?,?,?)");
			insert.setString(1, uuid.toString());
			insert.setString(2, startDate.toString());
			if (endDate == null) {
				insert.setString(3, "Permanent");
			} else {
				insert.setString(3, endDate.toString());
			}
			insert.setString(4, reason);
			insert.setString(5, username);
			insert.setString(6, mutedBy);
			insert.executeUpdate();

			// Player inserted now

		} catch (SQLException exe) {
			exe.printStackTrace();
		}
	}

	public boolean playerExists(String uuid) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.mutesTable + " WHERE UUID=?");
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
					.prepareStatement("DELETE FROM " + plugin.mutesTable + " WHERE UUID=?");
			statement.setString(1, uuid);
			statement.execute();

		} catch (SQLException exe) {
			exe.printStackTrace();
		}
	}

	public String getInfo(String uuid, String column) {
		try {
			PreparedStatement statement = plugin.getConnection()
					.prepareStatement("SELECT * FROM " + plugin.mutesTable + " WHERE UUID=?");

			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			results.next();
			// START, END, REASON, NAME, USERNAME,BANNEDBY
			return results.getString(column);

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return null;
	}

	public List<String> getList() {
		List<String> list = new ArrayList<String>();
		try {
			PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.mutesTable);

			ResultSet results = statement.executeQuery();

			while (results.next()) {
				String uuidStr = results.getString("UUID");
				String name = getInfo(uuidStr, "USERNAME");

				list.add(name);
			}
			return list;

		} catch (SQLException exe) {
			exe.printStackTrace();

		}
		return list;
	}

}
