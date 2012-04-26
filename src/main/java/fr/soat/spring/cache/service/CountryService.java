package fr.soat.spring.cache.service;

import fr.soat.spring.cache.model.Temperature;
import fr.soat.spring.cache.ws.GnhWebService;
import fr.soat.spring.cache.ws.MeteoWebService;

public interface CountryService {
	Integer retrieveGrossNationalHappiness(String country);

	Temperature retrieveCachedTemperature(String country);

	Temperature retrieveRealTimeTemperature(String country);

	void setGnhWebService(GnhWebService meteoWebService);

	void setMeteoWebService(MeteoWebService meteoWebService);
}
