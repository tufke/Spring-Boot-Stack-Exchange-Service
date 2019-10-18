package nl.merapar.stack.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import nl.merapar.stack.service.rest.exception.ServiceException;

@Component
public class URLConnectionUtil {

	@Value("${http.client.ssl.accept-all-trust-store:false}")
	private boolean acceptAll;
	
	public URLConnection getUrlConnection(String url) throws IOException {
		if(StringUtils.isNotBlank(url) && url.toLowerCase().startsWith("https:")) {
			URL httpsurl = new URL(url);
			HttpsURLConnection connection = (HttpsURLConnection)httpsurl.openConnection();
			connection.setSSLSocketFactory(getDefaultSSLSocketFactory());	
	    	
			// Since only our own certs are trusted, hostname verification is probably safe to bypass
	    	HostnameVerifier hv = new HostnameVerifier() {
	            @Override
	            public boolean verify(String hostName, SSLSession sslSession) {
	                return true;
	            }
	        };

	        connection.setHostnameVerifier(hv);	    	
	    	validateConnection(connection);
	    	return connection;
		}
		if(StringUtils.isNotBlank(url) && url.toLowerCase().startsWith("http:")) {
			URL fileUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection)fileUrl.openConnection();
			validateConnection(connection);
			return connection;
		}
		if(StringUtils.isNotBlank(url) && url.toLowerCase().startsWith("file:")) {
			URL fileUrl = new URL(url);
			URLConnection connection = fileUrl.openConnection();
			validateConnection(connection);
			return connection;
		}		
		
		throw new ServiceException("Unsupported protocol in url: " + url);

	}
	
	private boolean validateConnection(HttpURLConnection con) throws IOException {
		int httpStatus = con.getResponseCode();
    	if (httpStatus==HttpStatus.OK.value()) {
    		return true;
    	}
    	else {
    		throw new ServiceException("HttpResponse on requested url: " + httpStatus + " can not read from url");
    	} 		
	}
	
	private boolean validateConnection(URLConnection con) throws IOException {
		int length = con.getContentLength();
    	if (length > 0) {
    		return true;
    	}
    	else {
    		throw new ServiceException("File does not exist");
    	} 		
	}	
	
	private SSLSocketFactory getDefaultSSLSocketFactory() {
    	SSLSocketFactory sslsocketfactory = null;
    	
    	if(acceptAll) {
	    	// Install the all-trusting trust manager
	    	try {
	    	    SSLContext sslContext = SSLContext.getInstance("SSL"); 
	    	    sslContext.init(null, getAcceptAllTrustManager(), new java.security.SecureRandom()); 
	    	    //HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	    	    sslsocketfactory = sslContext.getSocketFactory();
	    	} 
	    	catch (GeneralSecurityException e) {}
    	}
    	else {
    		/**
    		 * use the configured truststore in aplication.properties (http.client.ssl.trust-store)
    		 * the certificates from that truststore are loaded because <link>SSLRemoteConfiguration<link> class sets
    		 * the truststore in javax.net.ssl.trustStore SystemProperty
    		 */
    		sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    	}
    	
    	return sslsocketfactory;
	}
	
	private TrustManager[] getAcceptAllTrustManager() {
    	// Create a trust manager that does not validate certificate chains
    	TrustManager[] trustAllCerts = new TrustManager[] { 
    	    new X509TrustManager() {     
    	        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
    	            return new X509Certificate[0];
    	        } 
    	        public void checkClientTrusted( 
    	            java.security.cert.X509Certificate[] certs, String authType) {
    	            } 
    	        public void checkServerTrusted( 
    	            java.security.cert.X509Certificate[] certs, String authType) {
    	        }
    	    } 
    	};   		
    	return trustAllCerts;
	}
	
}
