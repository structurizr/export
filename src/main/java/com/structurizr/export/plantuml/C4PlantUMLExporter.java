package com.structurizr.export.plantuml;

import com.structurizr.export.Diagram;
import com.structurizr.export.IndentingWriter;
import com.structurizr.model.*;
import com.structurizr.util.StringUtils;
import com.structurizr.view.*;

import java.util.Map;

import static java.lang.String.format;

public class C4PlantUMLExporter extends AbstractPlantUMLExporter {

    public static final String C4PLANTUML_TAGS_PROPERTY = "c4plantuml.tags";

    /**
     * <p>Set this property to <code>true</code> by calling {@link Configuration#addProperty(String, String)} in your
     * {@link ViewSet} in order to have all {@link ModelItem#getProperties()} for {@link Component}s
     * being printed in the PlantUML diagrams.</p>
     *
     * <p>The default value is <code>false</code>.</p>
     *
     * @see ViewSet#getConfiguration()
     * @see Configuration#getProperties()
     */
    public static final String C4PLANTUML_ELEMENT_PROPERTIES_PROPERTY = "c4plantuml.elementProperties";

    /**
     * <p>Set this property to <code>true</code> by calling {@link Configuration#addProperty(String, String)} in your
     * {@link ViewSet} in order to have all {@link ModelItem#getProperties()} for {@link Relationship}s being
     * printed in the PlantUML diagrams.</p>
     *
     * <p>The default value is <code>false</code>.</p>
     *
     * @see ViewSet#getConfiguration()
     * @see Configuration#getProperties()
     */
    public static final String C4PLANTUML_RELATIONSHIP_PROPERTIES_PROPERTY = "c4plantuml.relationshipProperties";

    private int groupId = 0;

    public C4PlantUMLExporter() {
    }

    @Override
    protected void writeHeader(View view, IndentingWriter writer) {
        super.writeHeader(view, writer);

        writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml");
        writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml");

        if (view.getElements().stream().map(ElementView::getElement).anyMatch(e -> e instanceof Container || e instanceof ContainerInstance)) {
            writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml");
        }

        if (view.getElements().stream().map(ElementView::getElement).anyMatch(e -> e instanceof Component)) {
            writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml");
        }

        if (view instanceof DeploymentView) {
            writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Deployment.puml");
        }

        writeIncludes(view, writer);

        writer.writeLine();
    }

    @Override
    protected void writeFooter(View view, IndentingWriter writer) {
        if ("true".equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(PLANTUML_LEGEND_PROPERTY, "true"))) {
            writer.writeLine();
            writer.writeLine("SHOW_LEGEND()");
        }

        super.writeFooter(view, writer);
    }

    @Override
    protected void startEnterpriseBoundary(View view, String enterpriseName, IndentingWriter writer) {
        writer.writeLine(String.format("Enterprise_Boundary(enterprise, \"%s\") {", enterpriseName));
        writer.indent();
    }

    @Override
    protected void endEnterpriseBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    protected void startGroupBoundary(View view, String group, IndentingWriter writer) {
        writer.writeLine(String.format("Boundary(group_%s, \"%s\") {", groupId++, group));
        writer.indent();
    }

    @Override
    protected void endGroupBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    protected void startSoftwareSystemBoundary(View view, SoftwareSystem softwareSystem, IndentingWriter writer) {
        writer.writeLine(String.format("System_Boundary(\"%s_boundary\", \"%s\") {", idOf(softwareSystem), softwareSystem.getName()));
        writer.indent();
    }

    @Override
    protected void endSoftwareSystemBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    protected void startContainerBoundary(View view, Container container, IndentingWriter writer) {
        writer.writeLine(String.format("Container_Boundary(\"%s_boundary\", \"%s\") {", idOf(container), container.getName()));
        writer.indent();
    }

    @Override
    protected void endContainerBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    protected void startDeploymentNodeBoundary(DeploymentView view, DeploymentNode deploymentNode, IndentingWriter writer) {
        String url = deploymentNode.getUrl();
        if (!StringUtils.isNullOrEmpty(url)) {
            url = "[[" + url + "]]";
        } else {
            url = "";
        }

        if (StringUtils.isNullOrEmpty(deploymentNode.getTechnology())) {
            writer.writeLine(
                    format("Deployment_Node(%s, \"%s\", $tags=\"%s\")%s {",
                            idOf(deploymentNode),
                            deploymentNode.getName() + (deploymentNode.getInstances() > 1 ? " (x" + deploymentNode.getInstances() + ")" : ""),
                            tagsOf(view, deploymentNode),
                            url
                    )
            );
        } else {
            writer.writeLine(
                    format("Deployment_Node(%s, \"%s\", \"%s\", $tags=\"%s\")%s {",
                            idOf(deploymentNode),
                            deploymentNode.getName() + (deploymentNode.getInstances() > 1 ? " (x" + deploymentNode.getInstances() + ")" : ""),
                            deploymentNode.getTechnology(),
                            tagsOf(view, deploymentNode),
                            url
                    )
            );
        }
        writer.indent();

        if (!isVisible(view, deploymentNode)) {
            writer.writeLine("hide " + idOf(deploymentNode));
        }
    }

    @Override
    protected void endDeploymentNodeBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();
    }

    @Override
    public Diagram export(CustomView view) {
        return null;
    }

    @Override
    public Diagram export(DynamicView view) {
        if (useSequenceDiagrams(view)) {
            throw new UnsupportedOperationException("Sequence diagrams are not supported by C4-PlantUML");
        } else {
            return super.export(view);
        }
    }

    @Override
    protected void writeElement(View view, Element element, IndentingWriter writer) {
        if (element instanceof CustomElement) {
            return;
        }

        Element elementToWrite = element;
        String id = idOf(element);

        String url = element.getUrl();
        if (!StringUtils.isNullOrEmpty(url)) {
            url = "[[" + url + "]]";
        } else {
            url = "";
        }

        if (Boolean.TRUE.toString().equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(C4PLANTUML_ELEMENT_PROPERTIES_PROPERTY, Boolean.FALSE.toString()))) {
            addProperties(view, writer, element);
        }

        if (element instanceof StaticStructureElementInstance) {
            StaticStructureElementInstance elementInstance = (StaticStructureElementInstance)element;
            element = elementInstance.getElement();

            if (StringUtils.isNullOrEmpty(url)) {
                url = element.getUrl();
                if (!StringUtils.isNullOrEmpty(url)) {
                    url = "[[" + url + "]]";
                } else {
                    url = "";
                }
            }
        }

        String name = element.getName();
        String description = element.getDescription();

        if (StringUtils.isNullOrEmpty(description)) {
            description = "";
        }

        if (element instanceof Person) {
            Person person = (Person)element;
            if (person.getLocation() == Location.External) {
                writer.writeLine(String.format("Person_Ext(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(view, elementToWrite), url));
            } else {
                writer.writeLine(String.format("Person(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(view, elementToWrite), url));
            }
        } else if (element instanceof SoftwareSystem) {
            SoftwareSystem softwareSystem = (SoftwareSystem)element;
            if (softwareSystem.getLocation() == Location.External) {
                writer.writeLine(String.format("System_Ext(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(view, elementToWrite), url));
            } else {
                writer.writeLine(String.format("System(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(view, elementToWrite), url));
            }
        } else if (element instanceof Container) {
            Container container = (Container)element;
            ElementStyle elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle(element);
            String shape = "";
            if (elementStyle.getShape() == Shape.Cylinder) {
                shape = "Db";
            } else if (elementStyle.getShape() == Shape.Pipe) {
                shape = "Queue";
            }

            if (StringUtils.isNullOrEmpty(container.getTechnology())) {
                writer.writeLine(String.format("Container%s(%s, \"%s\", \"%s\", $tags=\"%s\")%s", shape, id, name, description, tagsOf(view, elementToWrite), url));
            } else {
                writer.writeLine(String.format("Container%s(%s, \"%s\", \"%s\", \"%s\", $tags=\"%s\")%s", shape, id, name, container.getTechnology(), description, tagsOf(view, elementToWrite), url));
            }
        } else if (element instanceof Component) {
            Component component = (Component)element;
            if (StringUtils.isNullOrEmpty(component.getTechnology())) {
                writer.writeLine(String.format("Component(%s, \"%s\", \"%s\", $tags=\"%s\")%s", id, name, description, tagsOf(view, elementToWrite), url));
            } else {
                writer.writeLine(String.format("Component(%s, \"%s\", \"%s\", \"%s\", $tags=\"%s\")%s", id, name, component.getTechnology(), description, tagsOf(view, elementToWrite), url));
            }
        } else if (element instanceof InfrastructureNode) {
            InfrastructureNode infrastructureNode = (InfrastructureNode)element;
            if (StringUtils.isNullOrEmpty(infrastructureNode.getTechnology())) {
                writer.writeLine(format("Deployment_Node(%s, \"%s\", $descr=\"%s\", $tags=\"%s\")%s", idOf(infrastructureNode), name, description, tagsOf(view, elementToWrite), url));
            } else {
                writer.writeLine(format("Deployment_Node(%s, \"%s\", \"%s\", \"%s\", $tags=\"%s\")%s", idOf(infrastructureNode), name, infrastructureNode.getTechnology(), description, tagsOf(view, elementToWrite), url));
            }
        }

        if (!isVisible(view, elementToWrite)) {
            writer.writeLine("hide " + id);
        }
    }

    private String tagsOf(View view, Element element) {
        if (includeTags(view)) {
            String tags;
            if (element instanceof StaticStructureElementInstance) {
                tags = ((StaticStructureElementInstance) element).getElement().getTags() + "," + element.getTags();
            } else {
                tags = element.getTags();
            }

            return tags.replaceAll(",", "+");
        } else {
            return "";
        }
    }

    private String tagsOf(View view, Relationship relationship) {
        if (includeTags(view)) {
            String tags;

            if (!StringUtils.isNullOrEmpty(relationship.getLinkedRelationshipId())) {
                tags = relationship.getModel().getRelationship(relationship.getLinkedRelationshipId()).getTags();

                if (!StringUtils.isNullOrEmpty(relationship.getTags())) {
                    tags = tags + "," + relationship.getTags();
                }
            } else {
                tags = relationship.getTags();
            }

            return tags.replaceAll(",", "+");
        } else {
            return "";
        }
    }

    @Override
    protected void writeRelationship(View view, RelationshipView relationshipView, IndentingWriter writer) {
        Relationship relationship = relationshipView.getRelationship();
        Element source = relationship.getSource();
        Element destination = relationship.getDestination();

        if (source instanceof CustomElement || destination instanceof CustomElement) {
            return;
        }

        if (Boolean.TRUE.toString().equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(C4PLANTUML_ELEMENT_PROPERTIES_PROPERTY, Boolean.FALSE.toString()))) {
            addProperties(view, writer, relationship);
        }

        if (relationshipView.isResponse() != null && relationshipView.isResponse()) {
            source = relationship.getDestination();
            destination = relationship.getSource();
        }

        String description = "";

        if (!StringUtils.isNullOrEmpty(relationshipView.getOrder())) {
            description = relationshipView.getOrder() + ". ";
        }

        description += (hasValue(relationshipView.getDescription()) ? relationshipView.getDescription() : hasValue(relationshipView.getRelationship().getDescription()) ? relationshipView.getRelationship().getDescription() : "");

        if (StringUtils.isNullOrEmpty(relationship.getTechnology())) {
            writer.writeLine(format("Rel_D(%s, %s, \"%s\", $tags=\"%s\")", idOf(source), idOf(destination), description, tagsOf(view, relationship)));
        } else {
            writer.writeLine(format("Rel_D(%s, %s, \"%s\", \"%s\", $tags=\"%s\")", idOf(source), idOf(destination), description, relationship.getTechnology(), tagsOf(view, relationship)));
        }
    }

    private void addProperties(View view, IndentingWriter writer, ModelItem element) {
        if ("true".equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(C4PLANTUML_ELEMENT_PROPERTIES_PROPERTY, "false"))) {
            Map<String, String> properties = element.getProperties();
            if (!properties.isEmpty()) {
                writer.writeLine("WithoutPropertyHeader()");
                properties.keySet().stream().sorted().forEach(key ->
                        writer.writeLine(String.format("AddProperty(\"%s\",\"%s\")", key, properties.get(key)))
                );
            }
        }
    }

    @Override
    protected boolean isAnimationSupported(View view) {
        return !(view instanceof DynamicView) && super.isAnimationSupported(view);
    }

    protected boolean includeTags(View view) {
        return "true".equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(C4PLANTUML_TAGS_PROPERTY, "false"));
    }
    
}