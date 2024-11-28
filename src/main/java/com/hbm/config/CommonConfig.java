package com.hbm.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.hbm.main.MainRegistry;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {

	public static boolean createConfigBool(ForgeConfigSpec config, String category, String name, String comment,
										   boolean def) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push(category);
		ForgeConfigSpec.BooleanValue value = builder
				.comment(comment)
				.define(name, def);
		builder.pop();
		return value.get();
	}
	
	public static String createConfigString(ForgeConfigSpec  config, String category, String name, String comment,
											String def) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push(category);
		ForgeConfigSpec.ConfigValue<String> value = builder
				.comment(comment)
				.define(name, def);
		builder.pop();
		return value.get();
	}

	public static String[] createConfigStringList(ForgeConfigSpec config, String category, String name,
												  String comment, String[] defaultValues) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push(category);
		ForgeConfigSpec.ConfigValue<List<? extends String>> value = builder.comment(comment)
				.defineList(name, List.of(defaultValues),
						obj -> obj instanceof String);
		builder.pop();
		return value.get().toArray(new String[0]);
	}

	public static HashMap createConfigHashMap(ForgeConfigSpec  config, String category, String name, String comment, String keyType, String valueType, String[] defaultValues, String splitReg) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		HashMap<Object, Object> configDictionary = new HashMap<>();
		builder.push(category);
		ForgeConfigSpec.ConfigValue<List<? extends String>> value = builder
				.comment(comment)
				.defineList(name, List.of(defaultValues),
						obj -> obj instanceof String);
		builder.pop();
		for (String entry : value.get()) {
			String[] pairs = entry.split(splitReg, 0);
			configDictionary.put(parseType(pairs[0], keyType), parseType(pairs[1], valueType));
		}
		return configDictionary;
	}

	public static HashSet createConfigHashSet(ForgeConfigSpec  config, String category, String name,
											  String comment, String valueType, String[] defaultValues) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		HashSet<Object> configSet = new HashSet<>();
		builder.push(category);
		ForgeConfigSpec.ConfigValue<List<? extends String>> value = builder
				.comment(comment)
				.defineList(name, List.of(defaultValues),
						obj -> obj instanceof String);
		builder.pop();
		for (String entry : value.get()) {
			configSet.add(parseType(entry, valueType));
		}
		return configSet;
	}

	private static Object parseType(String value, String type){
		if(type == "Float"){
			return Float.parseFloat(value);
		}
		if(type == "Int"){
			return Integer.parseInt(value);
		}
		if(type == "Long"){
			return Float.parseFloat(value);
		}
		if(type == "Double"){
			return Double.parseDouble(value);
		}
		return value;
	}

	public static int createConfigInt(ForgeConfigSpec config, String category, String name, String comment,
									  int def) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push(category);
		ForgeConfigSpec.IntValue value = builder
				.comment(comment)
				.defineInRange(name, def, Integer.MIN_VALUE, Integer.MAX_VALUE);
		builder.pop();
		return value.get();
	}

	public static double createConfigDouble(ForgeConfigSpec  config, String category, String name,
											String comment, double def) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push(category);
		ForgeConfigSpec.DoubleValue value = builder
				.comment(comment)
				.defineInRange(name, def, Double.MIN_VALUE, Double.MAX_VALUE);
		builder.pop();
		return value.get();
	}

	public static int setDefZero(int value, int def) {

		if(value < 0) {
			MainRegistry.logger.error("Fatal error config: Randomizer value has been below zero, despite bound having to be positive integer!");
			MainRegistry.logger.error(String.format("Errored value will default back to %d, PLEASE REVIEW ForgeConfigSpec  DESCRIPTION BEFORE MEDDLING WITH VALUES!", def));
			return def;
		}

		return value;
	}
	
	public static int setDef(int value, int def) {
	
		if(value <= 0) {
			MainRegistry.logger.error("Fatal error config: Randomizer value has been set to zero, despite bound having to be positive integer!");
			MainRegistry.logger.error(String.format("Errored value will default back to %d, PLEASE REVIEW ForgeConfigSpec  DESCRIPTION BEFORE MEDDLING WITH VALUES!", def));
			return def;
		}
	
		return value;
	}

}
