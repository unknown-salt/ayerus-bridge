package com.unknown_salt.BridgeBot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = "bridgebot", name = "Bridge Bot", version = "1.0")
public class BridgeBot {

    private static final Logger LOGGER = LogManager.getLogger("BridgeBot");

    private final String[] bots = {
            "c003e39e77cb4ac3ae2fd3d6e2090731",
            "693cd916c7084b3c9b1a80f3669f4842"
    };

    private final List<String> botNames = new ArrayList<>();
    private Pattern chatPattern = null;
    private boolean isInitialized = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Mod loading...");
        MinecraftForge.EVENT_BUS.register(this);

        fetchBotNames();
    }

    private void fetchBotNames() {
        try {
            botNames.clear();

            for (String id : bots) {
                String url = "https://api.minecraftservices.com/minecraft/profile/lookup/" + id;
                String json = httpGet(url);

                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                String name = obj.has("name") ? obj.get("name").getAsString() : null;

                if (name != null && !name.isEmpty()) {
                    botNames.add(name);
                    LOGGER.info("Fetched bot name: {}", name);
                } else {
                    LOGGER.warn("Bot name not found for id: {}", id);
                }
            }

            if (!botNames.isEmpty()) {
                String botPattern = String.join("|", botNames);
                String regexStr = "^(?:.{2,4})?(?:Guild|G) (?:.{2,4})?> (?:\\[.+?\\] )?(?:&7|§7)?(?:" + botPattern
                        + ")(?: (?:.{2,4})?\\[[^]]+\\])?(?:.{2,4})?: (?:&r|§r)?(.+?):(?: )?(.*)$";
                chatPattern = Pattern.compile(regexStr);
                LOGGER.info("Successfully received bot names and compiled regex pattern.");
                isInitialized = true;
            } else {
                LOGGER.error("No bot names were fetched; initialization aborted.");
            }

        } catch (Exception e) {
            LOGGER.error("Failed to fetch bot names", e);
        }
    }

    private String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("HTTP GET failed with code " + status);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } finally {
            conn.disconnect();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ClientChatReceivedEvent event) {
        if (!isInitialized) {
            return;
        }

        String message = event.message.getUnformattedText();

        Matcher matcher = chatPattern.matcher(message);

        if (matcher.matches()) {
            String discordName = matcher.group(1);
            String chatMessage = matcher.group(2);

            event.setCanceled(true);

            String formatted = "§3Discord > §b" + discordName + "§7: §7" + chatMessage;
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(formatted));
        }
    }
}