package org.mule.config.spring.language;

import org.mule.api.lang.ConfigFile;
import org.mule.api.lang.ConfigLine;
import org.mule.api.lang.ConfigLineProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApplicationFilesParser {

    private final List<String> applicationResources;
    private final ApplicationConfig.Builder applicationBuilder;

    public ApplicationFilesParser(String appName, List<String> applicationResources)
    {
        this.applicationBuilder = new ApplicationConfig.Builder().setAppName(appName);
        this.applicationResources = applicationResources.stream().filter(resource -> {
            return resource.endsWith("language-config.xml") || resource.endsWith("language-config2.xml");
        }).collect(Collectors.toList());
    }

    public ApplicationConfig parse() {
        for (String applicationResource : applicationResources) {
            try (FileInputStream xmlInputStream = new FileInputStream(applicationResource)) {
                List<ConfigLine> configLines = new ArrayList<>();
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document xmlDocument = documentBuilder.parse(xmlInputStream);

                configLineFromElement(xmlDocument.getDocumentElement(), () -> {
                    return null;
                }).ifPresent( configLine ->
                {
                    configLines.add(configLine);
                });
                applicationBuilder.addConfigFile(new ConfigFile(applicationResource, configLines));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return applicationBuilder.build();
    }

    private Optional<ConfigLine> configLineFromElement(Node node, ConfigLineProvider parentProvider) {
        if (node.getNodeName().equals("#text") || node.getNodeName().equals("#comment"))
        {
            return Optional.empty();
        }
        ConfigLine.ConfigLineBuilder configLineBuilder = new ConfigLine.ConfigLineBuilder()
                .setOperation(node.getNodeName()).setParent(parentProvider);
        NamedNodeMap attributes = node.getAttributes();
        if (node.hasAttributes())
        {
            for (int i = 0; i < attributes.getLength(); i++)
            {
                Node attribute = attributes.item(i);
                configLineBuilder.addAttribute(attribute.getNodeName(), attribute.getNodeValue());
            }
        }
        if (node.hasChildNodes())
        {
            NodeList childs = node.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++)
            {
                Node child = childs.item(i);
                configLineFromElement(child, () -> {
                    return configLineBuilder.build();
                }).ifPresent(configLine -> {
                    configLineBuilder.addChild(configLine);
                });
            }
        }
        return Optional.of(configLineBuilder.build());
    }

}
