package cz.majlen.weather_ble.datastore;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import cz.majlen.weather_ble.config.InfluxConfig;

public class Influx {
	private InfluxDBClient client;
	
	public Influx(InfluxConfig config) {
		InfluxDBClientFactory.create(config.getConnection(), config.getToken().toCharArray());
	}
	
	public void write(Temperature temp) {
		WriteApi writer = client.getWriteApi();
		writer.writeMeasurement(WritePrecision.S, temp);
	}
}
