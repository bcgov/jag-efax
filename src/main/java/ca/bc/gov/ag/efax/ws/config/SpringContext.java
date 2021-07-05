package ca.bc.gov.ag.efax.ws.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * A utility class useful to retrieve beans from non-spring managed beans (ie,
 * static utility classes or exceptions).
 * 
 */
@Component
public class SpringContext implements ApplicationContextAware {

	private static ApplicationContext context;

	/**
	 * Returns the Spring managed bean instance of the given class type if it
	 * exists. Returns null otherwise.
	 * 
	 * @param beanClass
	 * @return
	 */
	public static <T extends Object> T getBean(Class<T> beanClass) {
		return context.getBean(beanClass);
	}
	
	/**
	 * Returns the ApplicationContext for spring instance.
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
	    return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// store ApplicationContext reference to access required beans later on
		SpringContext.context = context;
	}
}
