package cz.majlen.weather_ble.bluetooth;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;
import org.freedesktop.dbus.exceptions.DBusException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;


public class TemperatureBeacon {
	private static final MessageFormat BLUETOOTH_UUID_FORMAT = new MessageFormat("0000{0}-0000-1000-8000-00805f9b34fb");
	public static final String NORDIC_UART_SERVICE = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
	public static final String NORDIC_UART_TX_CHARACTERISTIC = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";
	
	DeviceManager btManager;
	BluetoothDevice device;
	String mac;
	
	public TemperatureBeacon(String mac) throws DBusException {
		this.mac = mac;
		this.btManager = DeviceManager.getInstance();
	}
	
	public byte[] readAdvertisingData() {
		for (int i = 0; i < 10; i++) {
			List<BluetoothDevice> list = btManager.getDevices();
			for (BluetoothDevice device : list) {
				if (device.getAddress().equals(this.mac))
					this.device = device;
				
				return this.device.getAdvertisingFlags();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		return null;
		
	}
	
	public boolean connect() {
		for (int i = 0; i < 10; i++) {
			List<BluetoothDevice> list = btManager.getDevices();
			for (BluetoothDevice device : list) {
				if (device.getAddress().equals(this.mac))
					this.device = device;
					return true;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		return false;
	}
	
	public void disconnect() {
		device.disconnect();
	}
	
	public Optional<BluetoothGattService> getService(String uuid) {
		if (uuid.length() == 4) {
			uuid = BLUETOOTH_UUID_FORMAT.format(uuid);
		}

		for (int i = 0; i < 10; i++) {
			BluetoothGattService service = device.getGattServiceByUuid(uuid);
			if (service != null) {
				return Optional.of(service);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		return Optional.empty();
	}
	
	public Optional<BluetoothGattCharacteristic> getCharacteristic(BluetoothGattService service, String uuid) {
		if (uuid.length() == 4) {
			uuid = BLUETOOTH_UUID_FORMAT.format(uuid);
		}
		
		BluetoothGattCharacteristic characteristic = service.getGattCharacteristicByUuid(uuid);
		if (characteristic != null) {
			return Optional.of(characteristic);
		}
		return Optional.empty();
	}

	public void registerDbusHandler(DbusHandler handler) throws DBusException {
		this.btManager.registerPropertyHandler(handler);
	}
}
