package org.mule.config.spring.language;

import org.mule.api.lang.ConfigLine;

import java.util.*;

public class ConfigParserRegistry {

    private Map<String, ConfigLineParser> parsers = new HashMap<>();

    public void registerParser(String elementName, ConfigLineParser xmlFlowParser) {
        parsers.put(elementName, xmlFlowParser);
    }

    public List<MuleBeanDefinitionHolder> parse(ConfigLine configLine) {
        List<MuleBeanDefinitionHolder> createdObjects = new ArrayList<>();

        //main is mule, no need to parse it
        List<ConfigLine> children = configLine.getChilds();
        parseChildren(createdObjects, children);
        return createdObjects;
    }

    private List<MuleBeanDefinitionHolder> parseChildren(List<MuleBeanDefinitionHolder> allMuleBeanDefinitionHolders, List<ConfigLine> childs) {
        List<MuleBeanDefinitionHolder> childrenMuleBeanDefinitionHolders = new ArrayList<>();
        for (ConfigLine child : childs) {
            //TODO change to use namespace as well
            String childIdentifier = child.getOperation();
            ConfigLineParser parser = parsers.get(childIdentifier);
            MuleBeanDefinitionHolder childMuleBeanDefinitionHolder;
            if (parser == null)
            {
                System.out.println("No parser found for " + childIdentifier);
                continue;
                //throw new IllegalArgumentException("No parser found for " + childIdentifier);
            }
            else
            {
                //TODO For now let's just pass the map but the parser should be agnostic of the map
                childMuleBeanDefinitionHolder = parser.parse(child);
                allMuleBeanDefinitionHolders.add(childMuleBeanDefinitionHolder);
                childrenMuleBeanDefinitionHolders.add(childMuleBeanDefinitionHolder);
            }
            List<MuleBeanDefinitionHolder> childChildrenMuleBeanDefinitionParsers = parseChildren(allMuleBeanDefinitionHolders, child.getChilds());
            if (childMuleBeanDefinitionHolder != null)
            {
                for (MuleBeanDefinitionHolder childChildMuleBeanDefinitionParser : childChildrenMuleBeanDefinitionParsers) {
                    childMuleBeanDefinitionHolder.getBeanDefinition().getPropertyValues().addPropertyValue(childChildMuleBeanDefinitionParser.getParentProperty(), childChildMuleBeanDefinitionParser.getBeanDefinition());
                }
            }
        }
        return childrenMuleBeanDefinitionHolders;
    }
}
