package com.raul.fraud_rule_engine_service.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.raul.fraud_rule_engine_service.config.FraudProperties;
import com.raul.fraud_rule_engine_service.service.GeoLocationService;

@Service
public class ConfigGeoLocationService implements GeoLocationService {

	private final Map<String, FraudProperties.GeoLocation> geoLocations;

	public ConfigGeoLocationService(FraudProperties properties) {
		this.geoLocations = properties.geoLocations();
	}

	@Override
	public Optional<Location> lookup(String countryCode) {
		if (countryCode == null) {
			return Optional.empty();
		}
		var location = geoLocations.get(countryCode.toUpperCase());
		return location == null ? Optional.empty() : Optional.of(new Location(location.latitude(), location.longitude()));
	}

	@Override
	public double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
		var earthRadiusKm = 6371.0d;
		var dLat = Math.toRadians(lat2 - lat1);
		var dLon = Math.toRadians(lon2 - lon1);
		var a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return earthRadiusKm * c;
	}
}
