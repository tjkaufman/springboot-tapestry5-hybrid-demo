package com.tjkaufman.tutorial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.tapestry5.http.services.RequestGlobals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
//import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.ViewResolver;

import com.tjkaufman.tutorial.tapestry.services.SomeInterface;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=Launcher.class)
//@SpringBootConfiguration(classes = Launcher.class)
public class DemoApplicationTests {

    @Autowired
    private AnnotationConfigServletWebServerApplicationContext server;

    @LocalServerPort
    private int port;
	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RequestGlobals requestGlobals;

	@Autowired
	SomeInterface someInterface;

	@Autowired
	@Qualifier("mvcViewResolver")
	ViewResolver viewResolver;

	@Test
	public void contextLoads() {
		assertNotNull(applicationContext);
	}

	@Test
	public void tapestryServiceToSpringInjection() {
		assertNotNull(requestGlobals);
	}

	@Test
	public void springServiceToTapestryInjection() {
		assertNotNull(someInterface);
		assertEquals(someInterface.testMethod(), viewResolver);
	}

	   @Test
	    public void printHomePage()  throws Exception {
	       String resp = sendGet("http://127.0.0.1:" + port + "/");
	       System.out.println(resp);
	       assertNotNull(resp);
	    }

	   private String sendGet(String url) throws Exception {


	        URL obj = new URL(url);
	        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	        // optional default is GET
	        con.setRequestMethod("GET");

	        //add request header
	        con.setRequestProperty("User-Agent", "Test");

	        int responseCode = con.getResponseCode();
	        System.out.println("\nSending 'GET' request to URL : " + url);
	        System.out.println("Response Code : " + responseCode);

	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();

	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();

	        //print result
	        return response.toString();

	    }
	
}
