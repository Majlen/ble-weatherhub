package cz.majlen.weather_ble.bluetooth;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattCharacteristic;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothGattService;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.UInt16;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public abstract class WeatherBeacon {
	private static final Logger log = LoggerFactory.getLogger(WeatherBeacon.class);
	
	private static final MessageFormat BLUETOOTH_UUID_FORMAT = new MessageFormat("0000{0}-0000-1000-8000-00805f9b34fb");
	
	public static class Measurement {
		double temperature;
		double humidity;
		double pressure;
		double batteryVoltage;
		
		Measurement() {}
		
		Measurement(double temperature, double humidity, double pressure, double batteryVoltage) {
			this.temperature = temperature;
			this.humidity = humidity;
			this.pressure = pressure;
			this.batteryVoltage = batteryVoltage;
		}
		
		@Override
		public String toString() {
			return "Measurement{" +
			       "temperature=" + temperature +
			       ", humidity=" + humidity +
			       ", pressure=" + pressure +
			       ", batteryVoltage=" + batteryVoltage +
			       '}';
		}
	}
	
	DeviceManager btManager;
	BluetoothDevice device;
	BluetoothAdapter adapter;
	String mac;
	
	public WeatherBeacon(String mac) {
		this.mac = mac;
		this.btManager = DeviceManager.getInstance();
		this.adapter = btManager.getAdapter();
	}
	
	public Optional<Map<UInt16, byte[]>> readManufacturerData() {
		if (!this.adapter.isDiscovering()) {
			log.warn("Bluetooth adapter was not in discovering state. Starting discovery...");
			BluetoothUtils.startBluetoothDiscovery(this.adapter);
		}
		
		BluetoothDevice device = null;
		if (this.device != null) {
			device = this.device;
		} else {
			for (BluetoothDevice option : this.btManager.getDevices(true)) {
				if (option.getAddress().equals(this.mac)) {
					device = option;
					break;
				}
			}
		}
		if (device != null) {
			Map<UInt16, byte[]> output = device.getManufacturerData();
			if (output != null) {
				return Optional.of(output);
			}
		}
		return Optional.empty();
	}
	
	public Optional<byte[]> readManufacturerData(UInt16 manufacturerId) {
		Optional<Map<UInt16, byte[]>> data = readManufacturerData();
		if (data.isPresent()) {
			byte[] output = data.get().get(manufacturerId);
			if (output != null) {
				return Optional.of(output);
			}
		}
		return Optional.empty();
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
		this.device.disconnect();
	}
	
	public Optional<BluetoothGattService> getService(String uuid) {
		if (uuid.length() == 4) {
			uuid = BLUETOOTH_UUID_FORMAT.format(uuid);
		}

		for (int i = 0; i < 10; i++) {
			BluetoothGattService service = this.device.getGattServiceByUuid(uuid);
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
	
	abstract public Measurement getMeasurement();
	
}
