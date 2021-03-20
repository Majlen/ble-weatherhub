package cz.majlen.weather_ble.bluetooth;

import org.freedesktop.dbus.types.UInt16;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Optional;

public class RuuviWeatherBeacon extends WeatherBeacon {
	private static final Logger log = LoggerFactory.getLogger(RuuviWeatherBeacon.class);
	
	private static final UInt16 RUUVI_MANUFACTURER_ID = new UInt16(0x0499);
	public static final String NORDIC_UART_SERVICE = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
	public static final String NORDIC_UART_TX_CHARACTERISTIC = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";
	
	public RuuviWeatherBeacon(String mac) {
		super(mac);
	}
	
	@Override
	public Measurement getMeasurement() {
		Optional<byte[]> data = readManufacturerData(RUUVI_MANUFACTURER_ID);
		if (data.isEmpty()) {
			return new Measurement();
		}
		try {
			return parseMeasurement(data.get());
		} catch (ParseException e) {
			log.error("Parsing weather data failed", e);
			return new Measurement();
		}
	}
	
	private static Measurement parseMeasurement(byte[] data) throws ParseException {
		if (data.length != 24) {
			throw new ParseException("Payload of weird size", 0);
		}
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		byte format = byteBuffer.get();
		if (format != 5) {
			throw new ParseException("Unknown data format", 0);
		}
		short temperatureReading = byteBuffer.getShort();
		int humidityReading = getUnsignedShort(byteBuffer);
		if (humidityReading > 40000) {
			throw new ParseException("Humidity reading over 100%", 3);
		}
		int pressureReading = getUnsignedShort(byteBuffer);
		// Read accelerometer values. Not interested in those.
		byteBuffer.getShort();
		byteBuffer.getShort();
		byteBuffer.getShort();
		int powerInfoReading = getUnsignedShort(byteBuffer);
		
		double temperature = temperatureReading * 0.005;
		double humidity = humidityReading * 0.000025;
		double pressure = pressureReading + 50000;
		double battery = 1.6 + (((powerInfoReading & 0xffe0) >> 5) / 1000.0);
		
		return new Measurement(temperature, humidity, pressure, battery);
	}
	
	private static int getUnsignedShort(ByteBuffer byteBuffer) {
		return (byteBuffer.getShort() & 0xffff);
	}
}
