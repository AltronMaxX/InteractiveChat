package com.loohp.interactivechat.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.bungeemessaging.BungeeMessageSender;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderParser {
	
	static {
		Bukkit.getScheduler().runTaskTimerAsynchronously(InteractiveChat.plugin, () -> {
			if (InteractiveChat.bungeecordMode) {
				if (InteractiveChat.useTooltipOnTab) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						parse(new ICPlayer(player), InteractiveChat.tabTooltip);
					}
				}
			}
		}, 0, 200);
	}
	
	public static String parse(ICPlayer player, String str) {
		if (player.isLocal()) {
			if (InteractiveChat.bungeecordMode) {
				List<ValuePairs<String, String>> pairs = new ArrayList<>();
				for (Entry<String, String> entry : getAllPlaceholdersContained(player.getLocalPlayer(), str).entrySet()) {
					pairs.add(new ValuePairs<>(entry.getKey(), entry.getValue()));
				}
				try {
					BungeeMessageSender.forwardPlaceholders(player.getUniqueId(), pairs);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return PlaceholderAPI.setPlaceholders(player.getLocalPlayer(), str);
		} else {
			for (Entry<String, String> entry : player.getRemotePlaceholdersMapping().entrySet()) {
				str = str.replace(entry.getKey(), entry.getValue());
			}
			return str;
		}
	}
	
	public static Map<String, String> getAllPlaceholdersContained(Player player, String str) {
		Map<String, String> matchingPlaceholders = new HashMap<>();
		Collection<PlaceholderExpansion> expansions = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansions();
		Pattern regex = Pattern.compile("(?i)%(" + expansions.parallelStream().map(each -> each.getIdentifier()).collect(Collectors.joining("|")) + ")_.*%");
		Matcher matcher = regex.matcher(str);
		while (matcher.find()) {
			String matching = matcher.group();
			matchingPlaceholders.put(matching, PlaceholderAPI.setPlaceholders(player, matching));
		}
		return matchingPlaceholders;
	}

}