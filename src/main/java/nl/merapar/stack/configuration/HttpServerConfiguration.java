package nl.merapar.stack.configuration;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * This configuration is only usefull when you enable HTTPS on the rest endpoints by setting 'server.ssl.enabled=true' in application.properties.
 * the 'server.port' property will then configure the HTTPS port instead of the HTTP port. In case you want to enable your 
 * rest endpoints for HTTP also when HTTPS is switched on specify 'server.http.port' in your application.properties with 8080
 * as value to enable HTTP calls on that port.
 *
 */
@Configuration
public class HttpServerConfiguration {

  /**
   * add a port for HTTP communication
   * 	
   * @param httpPort
   * @return
   */
  @Bean
  public ServletWebServerFactory servletContainer(@Value("${server.http.port:0}") Integer httpPort) {
      
	  TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
	  if (httpPort != 0) {
		  Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
	      connector.setPort(httpPort);

	      tomcat.addAdditionalTomcatConnectors(connector);
      }
      
      return tomcat;
  }
}