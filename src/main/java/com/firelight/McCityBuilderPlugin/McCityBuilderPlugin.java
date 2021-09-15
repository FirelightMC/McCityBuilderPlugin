package com.firelight.McCityBuilderPlugin;

import com.firelight.McCityBuilderPlugin.events.EventListener;
import com.firelight.McCityBuilderPlugin.tasks.Task;
import com.firelight.McCityBuilderPlugin.utils.CommandHandler;
import com.foxxite.fxcore.config.Config;
import com.foxxite.fxcore.config.Language;
import com.foxxite.fxcore.misc.UpdateChecker;
import com.foxxite.fxcore.sql.SQLHandler;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.foxxite.fxcore.misc.UpdateChecker.UpdateCheckResult.UP_TO_DATE;

public class McCityBuilderPlugin extends JavaPlugin {

    // Premium Placeholders
    public static final String RESOURCE_ID = "%%__RESOURCE__%%";
    public static final String USER_ID = "%%__USER__%%";
    public static final String NONCE = "%%__NONCE__%%";

    // Update checker code
    @Getter
    private final int spigotResourceID = 0;
    @Getter
    private UpdateChecker updateChecker;

    // Other Stuff
    private Timer timer = new Timer();
    @Getter
    private SQLHandler sqlHandler;
    @Getter
    private Language language;
    @Getter
    private Config configRaw;
    @Getter
    private FileConfiguration configuration;
    @Getter
    private PluginLogger pluginLogger;

    // Behaviour Code
    private EventListener eventListener;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {

        this.pluginLogger = new PluginLogger(this);

        //Register config files
        this.language = new Language(this);
        this.configRaw = new Config(this);
        this.configuration = this.configRaw.getConfig();

        //Update Checker
        this.updateChecker = new UpdateChecker(this.spigotResourceID, this);
        this.showUpdateMessage();

        //Register SQL handler
        this.sqlHandler = new SQLHandler(this, new ArrayList<>());

        //Register commands
        this.commandHandler = new CommandHandler(this);
        this.getCommand("command").setExecutor(this.commandHandler);

        //Register Timers
        this.timer.schedule(new Task(this), 0, 1000);

        //Register event listeners
        this.eventListener = new EventListener();
        this.getServer().getPluginManager().registerEvents(this.eventListener, this);

        this.pluginLogger.log(new LogRecord(Level.INFO, "Foxxite's plugin enabled"));

    }

    @Override
    public void onDisable() {
        this.timer.cancel();
        this.timer = null;

        this.getCommand("command").setExecutor(null);
        this.commandHandler = null;

        this.sqlHandler.closeConnection();
        this.sqlHandler = null;

        this.pluginLogger.log(new LogRecord(Level.INFO, "Foxxite's plugin disabled"));

    }

    public void reloadConfig() {
        this.configuration = this.configRaw.getConfig();
    }

    private void showUpdateMessage() {
        if (this.updateChecker.getUpdateCheckResult() != (UP_TO_DATE)) {
            final HashMap<String, String> placeholders = new HashMap<>();

            final String newVersion = (this.updateChecker.getLatestVersionString() != null ? this.updateChecker.getLatestVersionString() : "N/A");
            final String updateUrl = (this.updateChecker.getResourceURL() != null ? this.updateChecker.getResourceURL() : "N/A");

            placeholders.put("{newVersion}", newVersion);
            placeholders.put("{updateUrl}", updateUrl);
            placeholders.put("{checkResult}", this.updateChecker.getUpdateCheckResult().toString());

            final List<String> updateMSG = this.language.getMultiLineMessageCustom("update", placeholders);
            for (final String message : updateMSG) {
                this.pluginLogger.info(message);
            }
        } else {
            this.pluginLogger.info("Plugin is up to date.");
        }
    }
}
