package cz.majlen.weather_ble;

import cz.majlen.weather_ble.bluetooth.TemperatureBeacon;
import org.freedesktop.Hexdump;
import org.freedesktop.dbus.exceptions.DBusException;

public class MainTest {
	public static void main(String[] args) throws DBusException, InterruptedException {
		if (args.length != 1) {
			System.out.println("Specify MAC");
			System.exit(1);
		}
		
		TemperatureBeacon beacon = new TemperatureBeacon(args[0]);
		while (true) {
			byte[] bytes = beacon.readAdvertisingData();
			System.out.println(Hexdump.toHex(bytes));
			Thread.sleep(1000);
		}
	}
}
