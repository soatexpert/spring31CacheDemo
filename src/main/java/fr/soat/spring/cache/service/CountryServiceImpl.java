package fr.soat.spring.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import fr.soat.spring.cache.model.Temperature;
import fr.soat.spring.cache.ws.GnhWebService;
import fr.soat.spring.cache.ws.MeteoWebService;

@Component
public class CountryServiceImpl implements CountryService {

	@Autowired(required = false)
	private GnhWebService gnhWebService;

	@Autowired(required = false)
	private MeteoWebService meteoWebService;

	@Cacheable(value = "cache-gnh", key = "#country")
	public Integer retrieveGrossNationalHappiness(String country) {
		return gnhWebService.getGrossNationalHappiness(country);
	}

	@Cacheable(value = "cache-temperature", key = "#country")
	@CacheEvict(value = "cache-temperature", beforeInvocation = true, condition = "#root.caches[0].get(#country) != null and T(System).currentTimeMillis() - #root.caches[0].get(#country).get().getValidAt() > 1000")
	public Temperature retrieveCachedTemperature(String country) {
		return retrieveTemperatureInternal(country);
	}

	@CachePut(value = "cache-temperature", key = "#country")
	public Temperature retrieveRealTimeTemperature(String country) {
		return retrieveTemperatureInternal(country);
	}

	private Temperature retrieveTemperatureInternal(String country) {
		Temperature result = new Temperature();
		result.setValue(meteoWebService.getTemperature(country));
		result.setValidAt(System.currentTimeMillis());
		return result;
	}

	public void setGnhWebService(GnhWebService gnhWebService) {
		this.gnhWebService = gnhWebService;
	}

	public void setMeteoWebService(MeteoWebService meteoWebService) {
		this.meteoWebService = meteoWebService;
	}
}
