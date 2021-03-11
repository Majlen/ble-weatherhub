package cz.majlen.weather_ble.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class Config {
	private InfluxConfig influx;
	private List<BLEBeacon> beacons;
	
	public InfluxConfig getInflux() {
		return influx;
	}
	
	public void setInflux(InfluxConfig influx) {
		this.influx = influx;
	}
	
	public List<BLEBeacon> getBeacons() {
		return beacons;
	}
	
	public void setBeacons(List<BLEBeacon> beacons) {
		this.beacons = beacons;
	}
	
	public static Optional<Config> getConfig(String path) {
		ObjectMapper mapper = new ObjectMapper();
		
		InputStream is = Config.class.getResourceAsStream("/config.json");
		if (is == null) {
			// TODO: retry some common paths
			return Optional.empty();
		}
		try {
			return Optional.of(mapper.readValue(is , Config.class));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return Optional.empty();
	}
}
