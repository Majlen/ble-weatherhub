package cz.majlen.weather_ble.bluetooth;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BluetoothUtils {
	private static final Logger log = LoggerFactory.getLogger(BluetoothUtils.class);

	public static boolean startBluetoothDiscovery() {
		return startBluetoothDiscovery(DeviceManager.getInstance().getAdapter());
	}
	
	public static boolean startBluetoothDiscovery(BluetoothAdapter adapter) {
		Map<String, Variant<?>> filters = new HashMap<>();
		filters.put("Transport", new Variant<>("le"));
		try {
			adapter.setDiscoveryFilter(filters);
		} catch (DBusException e) {
			log.error("Exception setting discovery filters", e);
			
		}
		return adapter.startDiscovery();
	}
	
	public static void removeAllCachedDevices() {
		DeviceManager btManager = DeviceManager.getInstance();
		for (BluetoothDevice device: btManager.getDevices(true)) {
			try {
				btManager.getAdapter().removeDevice(device.getRawDevice());
			} catch (DBusException e) {
				log.error("Unable to remove device", e);
			}
		}
	}
}
