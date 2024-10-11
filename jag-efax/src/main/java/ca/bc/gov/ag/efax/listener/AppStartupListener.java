package ca.bc.gov.ag.efax.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ca.bc.gov.ag.efax.config.AppProperties;

@Component
public class AppStartupListener implements ApplicationListener<ApplicationReadyEvent> {
	
	private static final Logger logger = LoggerFactory.getLogger(AppStartupListener.class);
	
	private AppProperties props; 
	
	public AppStartupListener(AppProperties props) {
		this.props = props; 
	}
	
	/**
	 * This event is executed as late as conceivably possible to indicate that the
	 * application is ready to service requests.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		
		logger.info("** EFax service has started. Version: " + props.getAppVersion());
		return;
	}
}