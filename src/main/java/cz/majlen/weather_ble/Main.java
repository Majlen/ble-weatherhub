package cz.majlen.weather_ble;

import com.github.hypfvieh.bluetooth.wrapper.*;
import cz.majlen.weather_ble.bluetooth.DbusHandler;
import cz.majlen.weather_ble.bluetooth.TemperatureBeacon;
import cz.majlen.weather_ble.config.Config;
import cz.majlen.weather_ble.datastore.Influx;
import cz.majlen.weather_ble.datastore.Temperature;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.Optional;

public class Main {
	static void printValue(byte[] value) {
		if (value != null && value.length > 0) {
			System.out.print("HR raw = {");
			for (byte b : value) {
				System.out.printf("%02x,", b);
			}
			System.out.print("}");
		}
	}
	
	public static void main(String[] args) {
		Config config = Config.getConfig("config.json").orElseThrow();
		Influx influx = new Influx(config.getInflux());
		
		for (int i = 0; i < 10; i++) {
			Temperature temp = new Temperature("test", 10.3 + i);
			influx.write(temp);
		}
		
		TemperatureBeacon beacon;
		DbusHandler handler = new DbusHandler();
		try {
			beacon = new TemperatureBeacon(args[1]);
			beacon.registerDbusHandler(handler);
		} catch (DBusException e) {
			System.err.println(e.getMessage());
			return;
		}
		
		Optional<BluetoothGattService> optionalService = beacon.getService(TemperatureBeacon.NORDIC_UART_SERVICE);
		if (optionalService.isEmpty()) {
			System.err.println("Couldn't find service");
			return;
		}
		BluetoothGattService service = optionalService.get();
		
		Optional<BluetoothGattCharacteristic> optionalChar = beacon.getCharacteristic(
				service, TemperatureBeacon.NORDIC_UART_TX_CHARACTERISTIC);
		if (optionalChar.isEmpty()) {
			System.err.println("Couldn't find TX characteristic");
			return;
		}
		BluetoothGattCharacteristic characteristic = optionalChar.get();
		handler.setDbusPath(characteristic.getDbusPath());

		try {
			characteristic.startNotify();
		} catch (DBusException e) {
			System.err.println(e.getMessage());
		}
		
		System.out.println("Trying to read HR values.");
		byte[] hrRaw = characteristic.getValue();
		System.out.println("getValue()");
		printValue(hrRaw);
		hrRaw = handler.getValue();
		System.out.println("handler");
		printValue(hrRaw);
		
		try {
			characteristic.stopNotify();
		} catch (DBusException e) {
			System.out.println(e.getMessage());
		}
		beacon.disconnect();
	}
}
