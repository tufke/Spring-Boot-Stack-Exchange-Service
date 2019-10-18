package nl.merapar.stack.configuration;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * java properties "javax.net.ssl.trustStore" and "javax.net.ssl.trustStorePassword" do not correspond to 
 * Tomcat configuration parameters "server.ssl.trust-store" and "server.ssl.trust-store-password" 
 * from Spring boot "application.properties" According to the Tomcat documentation these
 * are only used for Client authentication of the Rest resources (i.e. for two-way SSL) and 
 * not for verifying remote certificates (one-way SSL).
 * 
 * To make them available for java to verify remote certificates when we make a HTTPS call add 
 * them as java.net.sll system properties.
 * an alternative of setting "javax.net.ssl.trustStore" and "javax.net.ssl.trustStorePassword" is 
 * by Spring boot Externalized Configuration which is done in configureSSL() below
 *
 */
@Slf4j
@Configuration
public class SSLRemoteConfiguration {

    // Add http.client.ssl.trust-store=classpath:stackservice.truststore to application.properties
    @Value("${http.client.ssl.trust-store}")
    private Resource trustStore;

    // Add http.client.ssl.trust-store-password=changeit to application.properties
    @Value("${http.client.ssl.trust-store-password}")
    private char[] trustStorePassword;

 	@Autowired
	private Environment env;

	@PostConstruct
	private void configureSSL() {
		//System.setProperty("javax.net.debug", "ssl");

		// set to TLSv1.1 or TLSv1.2
		System.setProperty("https.protocols", "TLSv1.1");

		// load the 'javax.net.ssl.trustStore' and
		// 'javax.net.ssl.trustStorePassword' from application.properties
		try {
			if(trustStore != null && trustStorePassword != null) {
				System.setProperty("javax.net.ssl.trustStore", trustStore.getURL().getPath());
				System.setProperty("javax.net.ssl.trustStorePassword", env.getProperty("http.client.ssl.trust-store-password"));
			}
		}
		catch(IOException e) {
			log.error("Configured truststore can not be found : " + trustStore.getFilename());
		}
	}
	
}
