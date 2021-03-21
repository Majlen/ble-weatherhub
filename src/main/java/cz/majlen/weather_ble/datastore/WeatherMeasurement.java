package cz.majlen.weather_ble.datastore;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "sensor-beacon")
public class WeatherMeasurement {
	@Column(tag = true) String beacon;
	@Column double temperature;
	@Column double humidity;
	@Column double pressure;
	@Column double batteryVoltage;
	@Column(timestamp = true) Instant timestamp;
	
	public WeatherMeasurement(String beacon, double temperature, double humidity, double pressure, double batteryVoltage) {
		this.beacon = beacon;
		this.temperature = temperature;
		this.humidity = humidity;
		this.pressure = pressure;
		this.batteryVoltage = batteryVoltage;
		this.timestamp = Instant.now();
	}
}
