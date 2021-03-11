package cz.majlen.weather_ble;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;

public class Influx {
	private InfluxDBClient client;

	//TODO: move to config file
	private static final char[] token = "weatherhub".toCharArray();
	
	public Influx() {
		InfluxDBClientFactory.create("http://localhost:8086", token);
	}
	
	public void write(Temperature temp) {
		WriteApi writer = client.getWriteApi();
		writer.writeMeasurement(WritePrecision.S, temp);
	}
}
