package language;

import org.junit.Test;
import org.mule.api.MuleContext;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.tck.testmodels.fruit.Banana;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import javax.inject.Inject;

/**
 * Created by pablolagreca on 1/8/16.
 */
public class LanguageParserTestCase  {

    @Test
    public void test() throws Exception {
        MuleContext context = new DefaultMuleContextFactory().createMuleContext();

        SpringXmlConfigurationBuilder builder = new SpringXmlConfigurationBuilder("language-config.xml");
        builder.configure(context);

        context.start();

        Object service = context.getRegistry().get("service");
    }

    @Test
    public void testInject() throws Exception {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("test-application-context.xml");
        classPathXmlApplicationContext.refresh();
        classPathXmlApplicationContext.getBean("testBean");
    }

    public static class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            beanFactory.registerSingleton("testBean", new MyObject());
        }
    }

}
