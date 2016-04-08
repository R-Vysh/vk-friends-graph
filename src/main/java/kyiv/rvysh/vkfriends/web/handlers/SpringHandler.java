package kyiv.rvysh.vkfriends.web.handlers;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class SpringHandler extends ServletContextHandler implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void startContext() throws Exception {
        XmlWebApplicationContext webContext = new XmlWebApplicationContext();
        webContext.setParent(applicationContext);
        webContext.setConfigLocation("");
        webContext.refresh();
        getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webContext);
        super.startContext();
    }
}
