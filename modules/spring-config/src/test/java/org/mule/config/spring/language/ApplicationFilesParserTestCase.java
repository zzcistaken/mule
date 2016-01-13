package org.mule.config.spring.language;

import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.junit.Test;
import org.mule.api.lang.ConfigLine;
import org.mule.tck.testmodels.fruit.Fruit;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

public class ApplicationFilesParserTestCase {

    @Test
    public void test() {
//        ApplicationConfig originalModel = new ApplicationFilesParser("app1", Arrays.asList(getClass().getClassLoader().getResource("language-config.xml").getFile())).parse();
//        ApplicationConfig updatedModel = new ApplicationFilesParser("app2", Arrays.asList(getClass().getClassLoader().getResource("language-config2.xml").getFile())).parse();



//        Diff diff = originalModel.get(0).compareTo(updatedModel.get(0));
//
//        if (diff.hasChanges())
//        {
//            List<Change> changes = diff.getChanges();
//            for (Change change : changes) {
//                System.out.println(change);
//            }
//        }
//
//        System.out.println(diff.changesSummary());

//        DiffNode diff2 = ObjectDifferBuilder.buildDefault().compare(updatedModel, originalModel);
//        diff2.visit((node, visit) -> {
//            final Object baseValue = node.canonicalGet(originalModel);
//            final Object workingValue = node.canonicalGet(updatedModel);
//
//            if (node.isChanged()) {
//                if (baseValue instanceof List) {
//                    //nothing to do with list changes
//                    return;
//                }
//                String propertyName = node.getPropertyName();
//                if (propertyName != null)
//                {
//                    System.out.println("changed property name: " + propertyName);
//                }
//            }
//            else if (node.isAdded())
//            {
//
//            }
//            else if (node.isRemoved())
//            {
//
//            }
//
//            final String message = node.getPath() + " changed from " +
//                    baseValue + " to " + workingValue;
//            System.out.println(message);
//        });
    }

    @Test
    public void runtimeBeanDefinition()
    {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("test-application-context.xml");
        classPathXmlApplicationContext.start();

        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MyObject.class).addConstructorArgReference("banana").getBeanDefinition();

        ((DefaultListableBeanFactory)classPathXmlApplicationContext.getBeanFactory()).registerBeanDefinition("myObject", beanDefinition);

        MyObject myObject = (MyObject) classPathXmlApplicationContext.getBean("myObject");

        System.out.println(myObject.getBanana());
    }

    public static class MyObject
    {
        private Fruit banana;

        public MyObject(Fruit banana) {
            this.banana = banana;
        }

        public Fruit getBanana() {
            return banana;
        }
    }


}