package cz.majlen.weather_ble;

import com.github.hypfvieh.bluetooth.DeviceManager;
import cz.majlen.weather_ble.bluetooth.RuuviWeatherBeacon;
import cz.majlen.weather_ble.bluetooth.WeatherBeacon;
import org.freedesktop.dbus.exceptions.DBusException;

public class MainTest {
	public static void main(String[] args) throws DBusException, InterruptedException {
		if (args.length != 1) {
			System.out.println("Specify MAC");
			System.exit(1);
		}
		
		DeviceManager.createInstance(false);
		WeatherBeacon beacon = new RuuviWeatherBeacon(args[0]);
		boolean disco = beacon.startDiscovery(args[0]);
		
		if (!disco) {
			System.out.println("Discovery not started :/");
		}
		
		while (true) {
			WeatherBeacon.Measurement measurement = beacon.getMeasurement();
			System.out.println(measurement);
			Thread.sleep(1000);
		}
	}
	
}
