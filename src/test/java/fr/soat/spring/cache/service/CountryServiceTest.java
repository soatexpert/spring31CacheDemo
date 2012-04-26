package fr.soat.spring.cache.service;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.soat.spring.cache.model.Temperature;
import fr.soat.spring.cache.ws.GnhWebService;
import fr.soat.spring.cache.ws.MeteoWebService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CountryServiceTest {

	private MeteoWebService meteoWebService;
	private GnhWebService gnhWebService;

	@Autowired
	private CountryService service;

	@Autowired
	private CacheManager cacheManager;

	@Before
	public void before() {
		gnhWebService = PowerMockito.mock(GnhWebService.class);
		PowerMockito.when(gnhWebService.getGrossNationalHappiness("france")).thenReturn(2000);
		service.setGnhWebService(gnhWebService);

		meteoWebService = PowerMockito.mock(MeteoWebService.class);
		PowerMockito.when(meteoWebService.getTemperature("france")).thenReturn(20, 23);
		service.setMeteoWebService(meteoWebService);

		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}

	@Test
	public void testRetrieveGrossNationalHappiness() throws Exception {
		String country = "france";
		// First call, the web-service is expected to be called
		service.retrieveGrossNationalHappiness(country);
		// Second call, the web-service is not expected to be called
		service.retrieveGrossNationalHappiness(country);
		// Assert that the web-service has been called once whereas the service was called twice
		PowerMockito.verifyPrivate(gnhWebService, Mockito.times(1)).invoke("getGrossNationalHappiness", country);
	}

	@Test
	public void testGetCachedTemperature() throws Exception {
		String country = "france";
		// First call, the web-service is expected to be called
		service.retrieveCachedTemperature(country);
		// We wait for cache value to expire
		Thread.sleep(1020);
		// Second call, the web-service is expected to be called
		service.retrieveCachedTemperature(country);
		// Assert that the web-service has been called twice as value expire between the two calls
		PowerMockito.verifyPrivate(meteoWebService, Mockito.times(2)).invoke("getTemperature", country);
	}

	@Test
	public void testGetRealTimeTemperature() throws Exception {
		String country = "france";
		// RealTime call, the web-service is expected to be called
		service.retrieveRealTimeTemperature(country);
		// RealTime call, the web-service is expected to be called
		Temperature firstTemperature = service.retrieveRealTimeTemperature(country);
		// Assert that the web-service has been called twice
		PowerMockito.verifyPrivate(meteoWebService, Mockito.times(2)).invoke("getTemperature", country);
		// Second cached call, the web-service is not expected to be called as value have been cached by previous RealTime call
		Temperature secondTemperature = service.retrieveCachedTemperature(country);
		// Assert that the web-service has been called twice whereas the service was called 3 times
		PowerMockito.verifyPrivate(meteoWebService, Mockito.times(2)).invoke("getTemperature", country);
		// Assert values are the same between first RealTime call and the cached one
		assertThat(firstTemperature, CoreMatchers.equalTo(secondTemperature));
		// RealTime call, the web-service is expected to be called
		service.retrieveRealTimeTemperature(country);
		// Assert that the web-service has been call each time the real time method was
		PowerMockito.verifyPrivate(meteoWebService, Mockito.times(3)).invoke("getTemperature", country);
	}
}
