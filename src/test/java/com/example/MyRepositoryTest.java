package com.example;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MyRepositoryTest {

    private HttpServer server;
    private WebTarget target;

    private String location = "JUnitTest";
    
    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
        
        // Create a dummy file in the "location" directory
        String contents = "JUnit test";
        String filename = Main.ROOT_REPOSITORY + File.separatorChar + location + File.separatorChar + "dummy.txt";
        File file = new File(filename);
        file.getParentFile().mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(contents.getBytes());        
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testGetMetadata() {
        String responseMsg = target.path("repository/metadata/JUnitTest/dummy.txt").request(MediaType.APPLICATION_JSON).get(String.class);
        JSONObject obj = new JSONObject(responseMsg);
        assertEquals ("JUnitTest/dummy.txt", obj.get("filename"));
        assertEquals (10, obj.getInt("filesize"));
    }
    
    @Test
    public void testGetMetadataDirectory() throws Exception {
    	// Create dummy file only for testing
    	String pathBase = Main.ROOT_REPOSITORY + File.separator + "JUnitTest3" + File.separator;
    	for (int i = 0; i < 10; ++i) {
        	File file = new File (pathBase + "dummy" + i + ".txt");
        	file .getParentFile().mkdirs();
        	FileOutputStream fileOutputStream = new FileOutputStream(file);
        	fileOutputStream.write("Dummy".getBytes());
    	}

    	//String responseMsg = target.path("repository/metadata").queryParam("id", "JUnitTest/dummy.txt").request().get(String.class);
        String responseMsg = target.path("repository/metadata/JUnitTest3/").request(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println ("Response: " + responseMsg);
        JSONArray obj = new JSONArray(responseMsg);
        assertEquals (10, obj.length());
    }

    @Test
    public void testGetData() {
        String responseMsg = target
        .path("repository/file/JUnitTest/dummy.txt")
        .request()
        .get(String.class);
        assertEquals ("JUnit test", responseMsg);
    }

    
    @Test
    public void testPut() {
    	byte[] payload = "It's works".getBytes();
    	ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
    	
        Response response = target
        		.path("repository/file/JUnitTest2/dummy2.txt")
        		.request(MediaType.APPLICATION_JSON)
        		.put(Entity.entity(inputStream, MediaType.APPLICATION_OCTET_STREAM));
        String responseString = response.readEntity(String.class);
        JSONObject obj = new JSONObject(responseString);
        assertEquals ("JUnitTest2/dummy2.txt", obj.get("filename"));
        assertEquals (10, obj.getInt("filesize"));
    }

    @Test
    public void testPost() {
    	byte[] payload = "It's works".getBytes();
    	ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
    	
        Response response = target
        		.path("repository/file/JUnitTest2/dummy2.txt")
        		.request(MediaType.APPLICATION_JSON)
        		.post(Entity.entity(inputStream, MediaType.APPLICATION_OCTET_STREAM));
        String responseString = response.readEntity(String.class);
        JSONObject obj = new JSONObject(responseString);
        assertEquals ("JUnitTest2/dummy2.txt", obj.get("filename"));
        assertEquals (10, obj.getInt("filesize"));
    }
    
    @Test
    public void testDelete() {
    	
    	try {
        	// Create dummy file only for testing
        	File file = new File (Main.ROOT_REPOSITORY + File.separator + "JUnitTest2" + File.separator + "dummy3.txt");
        	FileOutputStream fileOutputStream = new FileOutputStream(file);
        	fileOutputStream.write("Dummy".getBytes());
        	
        	// Testing code
            Response response = target
            		.path("repository/file/JUnitTest2/dummy3.txt")
            		.request(MediaType.APPLICATION_JSON)
            		.delete();
            int status = response.getStatus();
            assertEquals (200, status);
    	} catch (Exception ex) {
    		assertTrue("Exception: " + ex.getMessage(), false);
    	}
    }
    
}
