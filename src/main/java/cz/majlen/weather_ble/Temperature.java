package cz.majlen.weather_ble;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;

@Measurement(name = "temperature")
public class Temperature {
	@Column(tag = true) String beacon;
	@Column double value;
	@Column(timestamp = true) Instant timestamp;
}
