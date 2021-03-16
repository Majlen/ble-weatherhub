package cz.majlen.weather_ble;

import com.github.hypfvieh.bluetooth.DeviceManager;
import cz.majlen.weather_ble.bluetooth.TemperatureBeacon;
import org.freedesktop.Hexdump;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.UInt16;

import java.util.Map;

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
			System.out.println("Advertising data:");
			if (bytes == null) {
				System.out.println("\tnull :/");
			} else {
				System.out.println("\t" + Hexdump.toHex(bytes));
			}
			
			Map<String, byte[]> serviceData = beacon.readServiceData();
			System.out.println("Service data:");
			if (serviceData == null) {
				System.out.println("\tnull :/");
			} else {
				printServiceData(serviceData);
			}
			
			Map<UInt16, byte[]> manufData = beacon.readManufacturerData();
			System.out.println("Manufacturer data:");
			if (manufData == null) {
				System.out.println("\tnull :/");
			} else {
				printManufacturerData(manufData);
			}
			
			Thread.sleep(1000);
		}
	}
	
	static void printServiceData(Map<String, byte[]> data) {
		data.forEach((name, binary) -> System.out.println("\t" + name + ": " + Hexdump.toHex(binary)));
	}
	
	static void printManufacturerData(Map<UInt16, byte[]> data) {
		data.forEach((name, binary) -> System.out.println("\t" + name.intValue() + ": " + Hexdump.toHex(binary)));
	}
}
