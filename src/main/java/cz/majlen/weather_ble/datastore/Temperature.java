package cz.majlen.weather_ble.datastore;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "temperature")
public class Temperature {
	@Column(tag = true) String beacon;
	@Column double value;
	@Column(timestamp = true) Instant timestamp;
	
	public Temperature(String beacon, double value) {
		this.beacon = beacon;
		this.value = value;
		this.timestamp = Instant.now();
	}
}
