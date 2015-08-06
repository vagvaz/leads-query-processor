package eu.leads.infext.proc.com.geo;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import eu.leads.PropertiesSingleton;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SiteIPGeolocalization {
	
	DatabaseReader geoReader = null;
	
	public SiteIPGeolocalization() {
		//InputStream input = getClass().getClassLoader().getResourceAsStream("root/proc/com/geo/GeoIP.dat");
		File input = new File(PropertiesSingleton.getResourcesDir()+"/geo/GeoLite2-Country.mmdb");
	    try {
			geoReader = new DatabaseReader.Builder(input).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getDomainCountry(String baseUrlNorm) {
		
		String countryCode = null;
		
		if(geoReader!=null) {
			
			try {
				CountryResponse countryResponse = geoReader.country(InetAddress.getByName(baseUrlNorm));
				countryCode = countryResponse.getCountry().getIsoCode();
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GeoIp2Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return countryCode;
				
	}
	
//	public static void main(String[] args) {
//		SiteIPGeolocalization geo = new SiteIPGeolocalization();
//		String country = geo.getDomainCountry("www.adidas.se");
//		System.out.println(country);
//	}
	
}
