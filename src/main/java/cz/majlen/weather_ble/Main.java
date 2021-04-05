package cz.majlen.weather_ble;

import com.github.hypfvieh.bluetooth.DeviceManager;
import cz.majlen.weather_ble.bluetooth.BluetoothUtils;
import cz.majlen.weather_ble.bluetooth.RuuviWeatherBeacon;
import cz.majlen.weather_ble.bluetooth.WeatherBeacon;
import cz.majlen.weather_ble.config.Config;
import cz.majlen.weather_ble.datastore.Influx;
import cz.majlen.weather_ble.datastore.WeatherMeasurement;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private static Config loadConfig() {
		Optional<Config> optionalConfig = Config.getConfig("config.json");
		if (optionalConfig.isEmpty()) {
			log.error("Cannot load config");
			System.exit(1);
		}
		return optionalConfig.get();
	}
	
	private static void collectAndPublish(List<WeatherBeacon> beacons, Influx influx) {
		List<WeatherMeasurement> measurements = new ArrayList<>(beacons.size());
		for (WeatherBeacon beacon: beacons) {
			Optional<WeatherBeacon.Measurement> measurement = beacon.getMeasurement();
			if (measurement.isEmpty()) {
				continue;
			}
			WeatherBeacon.Measurement m = measurement.get();
			log.info(m.toString());
			measurements.add(new WeatherMeasurement(beacon.getName(), m.temperature, m.humidity, m.pressure, m.batteryVoltage));
		}
		influx.write(measurements);
	}
	
	public static void main(String[] args) {
		Config config = loadConfig();
		try {
			DeviceManager.createInstance(false);
		} catch (DBusException e) {
			log.error("Unable to create Bluetooth device manager", e);
			System.exit(1);
		}
		
		List<WeatherBeacon> beacons = config.getBeacons().stream()
				                              .map((beacon -> new RuuviWeatherBeacon(beacon.getMac(), beacon.getName())))
				                              .collect(Collectors.toList());
		Influx influx = new Influx(config.getInflux());
		
		boolean disco = BluetoothUtils.startBluetoothDiscovery();
		if (!disco) {
			log.warn("Discovery not started :/");
		}
		
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(() -> collectAndPublish(beacons, influx), 0, 10, TimeUnit.SECONDS);
		// Every 15 minutes clean the cache of discovered devices, so we don't transfer a lot of historical
		// devices every time we read the temperature
		executor.scheduleAtFixedRate(BluetoothUtils::removeAllCachedDevices, 900, 900, TimeUnit.SECONDS);
	}
}
