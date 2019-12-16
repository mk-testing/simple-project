package com.example;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.yasson.internal.serializer.ByteArrayBase64Deserializer;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.model.ParamQualifier;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("repository")
public class Repository {

	private String extractRelativePath(UriInfo uriInfo) {
    	URI absoluteUri = uriInfo.getAbsolutePath();
    	String path = absoluteUri.getPath();
    	String componentPath[] = path.split("/");
    	
    	String relativePath = String.join("/", Arrays.copyOfRange(componentPath, 4, componentPath.length));
    	return relativePath;
		
	}
	
	@Context
	HttpHeaders headers; 
	
    // Get file by ID
    @GET
    @Path("{any: file/.*}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile(@Context UriInfo uriInfo) throws Exception {
    	String relativePath = extractRelativePath(uriInfo);
    	
    	if (! FileUploaded.Exists(relativePath)) {
        	return Response.status(404).build();
    	} else {
        	FileUploaded fileUploaded = new FileUploaded(relativePath);
        	GenericEntity<byte[]> entity = new GenericEntity<byte[]>(fileUploaded.GetRawData()) {};

        	return Response.ok(entity, MediaType.APPLICATION_OCTET_STREAM)
        		.header("Content-Disposition", "attachment; filename=\"" + fileUploaded.getFilename() + "\"" ) //optional
        		.build();
    	}
    }    
    
    // Upload or update new file using standard web form
    @POST
    @Path("file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response PutMultipart(
    		@FormDataParam("location") String location,
    		@FormDataParam("file") InputStream inputStream,
    		@FormDataParam("file") FormDataContentDisposition fileMetadata) throws Exception {
    	Response response = null;
        try
        {
        	String relativePath = location + '/' + fileMetadata.getFileName();
        	FileUploaded fileUploaded = FileUploaded.UploadFile(relativePath, inputStream);
        	GenericEntity<FileUploaded> entity = new GenericEntity<FileUploaded>(fileUploaded) {};
        	response = Response.status(200).entity(entity).type(MediaType.APPLICATION_JSON).build();    		        	
        } catch (IOException e) 
        {
        	Response.status(500).build();
        }
        return response;
    }
    
    // Remove file
    @DELETE
    @Path("{any: file/.*}")
    public Response Delete(@Context UriInfo uriInfo,
    		@Context HttpHeaders headers) {
    	String relativePath = extractRelativePath(uriInfo);
    	if (FileUploaded.Exists(relativePath)) {
        	FileUploaded fileUploaded = new FileUploaded(relativePath);
        	fileUploaded.Delete();
        	
        	return Response.ok().build();
    	} else {
        	return Response.status(404).build();
    	}
    }
    
    // Upload new file
    @PUT
    @Path("{any: file/.*}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Put(@Context UriInfo uriInfo,
    		@Context HttpHeaders headers,
    		byte[] payload
	) {
    	String relativePath = extractRelativePath(uriInfo);
    	
    	Response response = null;
        try
        {
        	ByteArrayInputStream inputStream = new ByteArrayInputStream(payload);
        	FileUploaded fileUploaded = FileUploaded.UploadFile(relativePath, inputStream);
        	GenericEntity<FileUploaded> entity = new GenericEntity<FileUploaded>(fileUploaded) {};
        	response = Response.status(200).entity(entity).type(MediaType.APPLICATION_JSON).build();    		        	
        } catch (Exception e) 
        {
        	return Response.status(500).build();
        }
        return response;
    }

    // Update already uploaded file. In that case we don't have any differences between the behaviour of POST and PUT so I call PUT from POST
    @POST
    @Path("{any: file/.*}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Post(@Context UriInfo uriInfo,
    		@Context HttpHeaders headers,
    		byte[] payload) {
    	return Put(uriInfo, headers, payload);
    }
    

    // Get metadati related with a specifiche file
    @GET
    @Path("{any: metadata/.*}")
    public Response getInfo(@Context UriInfo uriInfo) {
    	String mediaType = MediaType.APPLICATION_JSON;;
    	if (headers.getMediaType() != null && headers.getMediaType().toString().equals("APPLICATION_XML")) {
    		mediaType = MediaType.APPLICATION_XML;
    	}

    	URI absoluteUri = uriInfo.getAbsolutePath();
    	String path = absoluteUri.getPath();
    	String componentPath[] = path.split("/");
    	
    	String relativePath = String.join("/", Arrays.copyOfRange(componentPath, 4, componentPath.length));
    	String absolutePath = Main.ROOT_REPOSITORY + relativePath;
    	File file = new File(absolutePath);
    	if (file.isDirectory()) {
    		List<FileUploaded> list = FileUploaded.GetListing(relativePath);
        	GenericEntity<List<FileUploaded>> entity = new GenericEntity<List<FileUploaded>>(list) {};
        	return Response.status(200).entity(entity).type(mediaType).build();    		
    	} else {
        	FileUploaded fileUploaded;
        	fileUploaded = new FileUploaded(relativePath);
        	GenericEntity<FileUploaded> entity = new GenericEntity<FileUploaded>(fileUploaded) {};
        	return Response.status(200).entity(entity).type(mediaType).build();
    	}
    }    
}
