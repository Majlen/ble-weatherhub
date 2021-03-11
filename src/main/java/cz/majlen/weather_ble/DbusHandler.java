package cz.majlen.weather_ble;

import org.freedesktop.dbus.handlers.AbstractPropertiesChangedHandler;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

public class DbusHandler extends AbstractPropertiesChangedHandler {
	private String dbusPath;
	private byte[] value;
	
	public void setDbusPath(String dbusPath) {
		this.dbusPath = dbusPath;
	}

	private synchronized byte[] getSetValue(byte[] newValue, boolean update) {
		if (update) {
			value = newValue;
		}
		return value != null ? value.clone() : null;
	}
	
	public byte[] getValue() {
		return getSetValue(null, false);
	}
	
	@Override
	public void handle(Properties.PropertiesChanged propertiesChanged) {
		if (propertiesChanged != null) {
			if (!dbusPath.equals(propertiesChanged.getPath())) {
				return;
			}
			Map<String, Variant<?>> data = propertiesChanged.getPropertiesChanged();
			if (!data.containsKey("Value")) {
				return;
			}
			if (!(data.get("Value").getValue() instanceof byte[])) {
				return;
			}
			getSetValue((byte[]) data.get("Value").getValue(), true);
		}
	}
}
