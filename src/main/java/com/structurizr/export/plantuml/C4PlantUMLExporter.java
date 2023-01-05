package com.structurizr.export.plantuml;

import com.structurizr.export.Diagram;
import com.structurizr.export.IndentingWriter;
import com.structurizr.model.*;
import com.structurizr.util.StringUtils;
import com.structurizr.view.*;

import java.util.*;

import static java.lang.String.format;

public class C4PlantUMLExporter extends AbstractPlantUMLExporter {

    public static final String C4PLANTUML_LEGEND_PROPERTY = "c4plantuml.legend";
    public static final String C4PLANTUML_STEREOTYPES_PROPERTY = "c4plantuml.stereotypes";
    public static final String C4PLANTUML_TAGS_PROPERTY = "c4plantuml.tags";
    public static final String C4PLANTUML_STANDARD_LIBRARY_PROPERTY = "c4plantuml.stdlib";

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

    public static final String C4PLANTUML_SPRITE = "c4plantuml.sprite";
    public static final String C4PLANTUML_SHADOW = "c4plantuml.shadow";

    private int groupId = 0;

    public C4PlantUMLExporter() {
    }

    @Override
    protected void writeHeader(View view, IndentingWriter writer) {
        super.writeHeader(view, writer);

        writeSkinParams(writer);

        if (view.getAutomaticLayout() != null) {
            switch (view.getAutomaticLayout().getRankDirection()) {
                case LeftRight:
                    writer.writeLine("left to right direction");
                    break;
                default:
                    writer.writeLine("top to bottom direction");
                    break;
            }
        } else {
            writer.writeLine("top to bottom direction");
        }

        writer.writeLine();

        if (usePlantUMLStandardLibrary(view)) {
            writer.writeLine("!include <C4/C4>");
            writer.writeLine("!include <C4/C4_Context>");
        } else {
            writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml");
            writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml");
        }

        if (view.getElements().stream().map(ElementView::getElement).anyMatch(e -> e instanceof Container || e instanceof ContainerInstance)) {
            if (usePlantUMLStandardLibrary(view)) {
                writer.writeLine("!include <C4/C4_Container>");
            } else {
                writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml");
            }
        }

        if (view.getElements().stream().map(ElementView::getElement).anyMatch(e -> e instanceof Component)) {
            if (usePlantUMLStandardLibrary(view)) {
                writer.writeLine("!include <C4/C4_Component>");
            } else {
                writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml");
            }
        }

        if (view instanceof DeploymentView) {
            if (usePlantUMLStandardLibrary(view)) {
                writer.writeLine("!include <C4/C4_Deployment>");
            } else {
                writer.writeLine("!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Deployment.puml");
            }
        }

        writeIncludes(view, writer);

        if (includeTags(view)) {
            Map<String,ElementStyle> elementStyles = new HashMap<>();
            Map<String,RelationshipStyle> relationshipStyles = new HashMap<>();
            Map<String,ElementStyle> boundaryStyles = new HashMap<>();

            // elements
            for (ElementView elementView : view.getElements()) {
                Element element = elementView.getElement();
                ElementStyle elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle(element);

                elementStyles.put(elementStyle.getTag(), elementStyle);
            }

            // relationships
            for (RelationshipView relationshipView : view.getRelationships()) {
                Relationship relationship = relationshipView.getRelationship();
                RelationshipStyle relationshipStyle = view.getViewSet().getConfiguration().getStyles().findRelationshipStyle(relationship);

                relationshipStyles.put(relationshipStyle.getTag(), relationshipStyle);
            }

            // boundaries
            List<Element> boundaryElements = new ArrayList<>();
            if (view instanceof ContainerView) {
                boundaryElements.addAll(getBoundarySoftwareSystems(view));
            } else if (view instanceof ComponentView) {
                boundaryElements.addAll(getBoundaryContainers(view));
            } else if (view instanceof DynamicView) {
                DynamicView dynamicView = (DynamicView)view;
                if (dynamicView.getElement() instanceof SoftwareSystem) {
                    boundaryElements.addAll(getBoundarySoftwareSystems(view));
                } else if (dynamicView.getElement() instanceof Container) {
                    boundaryElements.addAll(getBoundaryContainers(view));
                }
            }

            for (Element boundaryElement : boundaryElements) {
                ElementStyle elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle(boundaryElement);
                boundaryStyles.put(elementStyle.getTag(), elementStyle);
            }

            if (!elementStyles.isEmpty()) {
                writer.writeLine();

                for (String tagList : elementStyles.keySet()) {
                    ElementStyle elementStyle = elementStyles.get(tagList);
                    tagList = tagList.replaceFirst("Element,", "");

                    writer.writeLine(String.format("AddElementTag(\"%s\", $bgColor=\"%s\", $borderColor=\"%s\", $fontColor=\"%s\", $sprite=\"%s\", $shadowing=\"%s\")",
                            tagList,
                            elementStyle.getBackground(),
                            elementStyle.getStroke(),
                            elementStyle.getColor(),
                            elementStyle.getProperties().getOrDefault(C4PLANTUML_SPRITE, ""),
                            elementStyle.getProperties().getOrDefault(C4PLANTUML_SHADOW, "")
                    ));
                }
            }

            if (!relationshipStyles.isEmpty()) {
                writer.writeLine();

                for (String tagList : relationshipStyles.keySet()) {
                    RelationshipStyle relationshipStyle = relationshipStyles.get(tagList);
                    tagList = tagList.replaceFirst("Relationship,", "");

                    String lineStyle = "\"\"";
                    if (relationshipStyle.getStyle() == LineStyle.Dashed) {
                        lineStyle = "DashedLine()";
                    } else if (relationshipStyle.getStyle() == LineStyle.Dotted) {
                        lineStyle = "DottedLine()";
                    }

                    writer.writeLine(String.format("AddRelTag(\"%s\", $textColor=\"%s\", $lineColor=\"%s\", $lineStyle = %s)",
                            tagList,
                            relationshipStyle.getColor(),
                            relationshipStyle.getColor(),
                            lineStyle
                    ));
                }
            }

            if (!boundaryStyles.isEmpty()) {
                writer.writeLine();

                for (String tagList : boundaryStyles.keySet()) {
                    ElementStyle elementStyle = boundaryStyles.get(tagList);
                    tagList = tagList.replaceFirst("Element,", "");

                    writer.writeLine(String.format("AddBoundaryTag(\"%s\", $bgColor=\"%s\", $borderColor=\"%s\", $fontColor=\"%s\", $shadowing=\"%s\")",
                            tagList,
                            "#ffffff",
                            elementStyle.getStroke(),
                            elementStyle.getStroke(),
                            elementStyle.getProperties().getOrDefault(C4PLANTUML_SHADOW, "")
                    ));
                }
            }
        }

        writer.writeLine();
    }

    @Override
    protected void writeFooter(View view, IndentingWriter writer) {
        if (includeLegend(view)) {
            writer.writeLine();
            writer.writeLine("SHOW_LEGEND(" + !(includeStereotypes(view)) + ")");
        } else {
            writer.writeLine();
            writer.writeLine((includeStereotypes(view) ? "show" : "hide") + " stereotypes");
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
        writer.writeLine(String.format("System_Boundary(\"%s_boundary\", \"%s\", $tags=\"%s\") {", idOf(softwareSystem), softwareSystem.getName(), tagsOf(view, softwareSystem)));
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
        writer.writeLine(String.format("Container_Boundary(\"%s_boundary\", \"%s\", $tags=\"%s\") {", idOf(container), container.getName(), tagsOf(view,container)));
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
                            deploymentNode.getName() + (!"1".equals(deploymentNode.getInstances()) ? " (x" + deploymentNode.getInstances() + ")" : ""),
                            tagsOf(view, deploymentNode),
                            url
                    )
            );
        } else {
            writer.writeLine(
                    format("Deployment_Node(%s, \"%s\", \"%s\", $tags=\"%s\")%s {",
                            idOf(deploymentNode),
                            deploymentNode.getName() + (!"1".equals(deploymentNode.getInstances()) ? " (x" + deploymentNode.getInstances() + ")" : ""),
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
    protected void writeElement(View view, Element element, IndentingWriter writer) {
        if (element instanceof CustomElement) {
            return;
        }

        Element elementToWrite = element;
        ElementStyle elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle(element);
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
            return view.getViewSet().getConfiguration().getStyles().findElementStyle(element).getTag().replaceFirst("Element,", "");
        } else {
            return "";
        }
    }

    private String tagsOf(View view, Relationship relationship) {
        if (includeTags(view)) {
            return view.getViewSet().getConfiguration().getStyles().findRelationshipStyle(relationship).getTag().replaceFirst("Relationship,", "");
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

        if (Boolean.TRUE.toString().equalsIgnoreCase(view.getViewSet().getConfiguration().getProperties().getOrDefault(C4PLANTUML_RELATIONSHIP_PROPERTIES_PROPERTY, Boolean.FALSE.toString()))) {
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
        Map<String, String> properties = element.getProperties();
        if (!properties.isEmpty()) {
            writer.writeLine("WithoutPropertyHeader()");
            properties.keySet().stream().sorted().forEach(key ->
                    writer.writeLine(String.format("AddProperty(\"%s\",\"%s\")", key, properties.get(key)))
            );
        }
    }

    @Override
    protected boolean isAnimationSupported(View view) {
        return !(view instanceof DynamicView) && super.isAnimationSupported(view);
    }

    protected boolean includeLegend(View view) {
        return "true".equalsIgnoreCase(getViewOrViewSetProperty(view, C4PLANTUML_LEGEND_PROPERTY, "true"));
    }

    protected boolean includeStereotypes(View view) {
        return "true".equalsIgnoreCase(getViewOrViewSetProperty(view, C4PLANTUML_STEREOTYPES_PROPERTY, "false"));
    }

    protected boolean includeTags(View view) {
        return "true".equalsIgnoreCase(getViewOrViewSetProperty(view, C4PLANTUML_TAGS_PROPERTY, "false"));
    }

    protected boolean usePlantUMLStandardLibrary(View view) {
        return "true".equalsIgnoreCase(getViewOrViewSetProperty(view, C4PLANTUML_STANDARD_LIBRARY_PROPERTY, "false"));
    }

}