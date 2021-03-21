package cz.majlen.weather_ble.datastore;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import cz.majlen.weather_ble.config.InfluxConfig;

import java.util.List;

public class Influx {
	private InfluxDBClient client;
	private String bucket;
	private String org;
	
	public Influx(InfluxConfig config) {
		this.client = InfluxDBClientFactory.create(config.getConnection(), config.getToken().toCharArray());
		this.bucket = config.getBucket();
		this.org = config.getOrg();
	}
	
	public void write(WeatherMeasurement temp) {
		WriteApi writer = this.client.getWriteApi();
		writer.writeMeasurement(bucket, org, WritePrecision.S, temp);
	}
	
	public void write(List<WeatherMeasurement> temp) {
		WriteApi writer = this.client.getWriteApi();
		writer.writeMeasurement(bucket, org, WritePrecision.S, temp);
	}
}
