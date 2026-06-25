package com.raul.fraud_rule_engine_service.service;

import java.util.Optional;

public interface GeoLocationService {
	Optional<Location> lookup(String countryCode);

	double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2);

	record Location(double latitude, double longitude) {
	}
}
