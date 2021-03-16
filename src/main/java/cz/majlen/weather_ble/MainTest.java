package cz.majlen.weather_ble;

import com.github.hypfvieh.bluetooth.DeviceManager;
import cz.majlen.weather_ble.bluetooth.TemperatureBeacon;
import org.freedesktop.Hexdump;
import org.freedesktop.dbus.exceptions.DBusException;

public class MainTest {
	public static void main(String[] args) throws DBusException, InterruptedException {
		if (args.length != 1) {
			System.out.println("Specify MAC");
			System.exit(1);
		}
		
		DeviceManager.createInstance(false);
		TemperatureBeacon beacon = new TemperatureBeacon(args[0]);
		boolean disco = beacon.startDiscovery(args[0]);
		
		if (!disco) {
			System.out.println("Discovery not started :/");
		}
		
		while (true) {
			byte[] bytes = beacon.readAdvertisingData();
			if (bytes == null) {
				System.out.println("null :/");
			}
			System.out.println(Hexdump.toHex(bytes));
			Thread.sleep(1000);
		}
	}
}
