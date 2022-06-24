package com.structurizr.export.plantuml;

import com.structurizr.export.Diagram;
import com.structurizr.export.IndentingWriter;
import com.structurizr.model.*;
import com.structurizr.util.StringUtils;
import com.structurizr.view.*;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class StructurizrPlantUMLExporter extends AbstractPlantUMLExporter {

    public StructurizrPlantUMLExporter() {
        addSkinParam("shadowing", "false");
        addSkinParam("arrowFontSize", "10");
        addSkinParam("defaultTextAlignment", "center");
        addSkinParam("wrapWidth", "200");
        addSkinParam("maxMessageSize", "100");
    }

    @Override
    protected boolean isAnimationSupported(View view) {
        return true;
    }

    @Override
    protected void writeHeader(View view, IndentingWriter writer) {
        super.writeHeader(view, writer);

        writeIncludes(view, writer);

        writer.writeLine();
        writer.writeLine("hide stereotype");
        writer.writeLine();

        List<Element> elements = view.getElements().stream().map(ElementView::getElement).sorted(Comparator.comparing(Element::getName)).collect(Collectors.toList());
        for (Element element : elements) {
            String id = idOf(element);

            String type = plantUMLShapeOf(view, element);
            if ("actor".equals(type)) {
                type = "rectangle"; // the actor shape is not supported in this implementation
            }

            ElementStyle elementStyle = findElementStyle(view, element);

            String background = elementStyle.getBackground();
            String stroke = elementStyle.getStroke();
            String color = elementStyle.getColor();
            Shape shape = elementStyle.getShape();

            if (view instanceof DynamicView && useSequenceDiagrams(view)) {
                type = "sequenceParticipant";
            }

            writer.writeLine(format("skinparam %s<<%s>> {", type, id));
            writer.indent();
            if (element instanceof DeploymentNode) {
                writer.writeLine("BackgroundColor #ffffff");
            } else {
                writer.writeLine(String.format("BackgroundColor %s", background));
            }
            writer.writeLine(String.format("FontColor %s", color));
            writer.writeLine(String.format("BorderColor %s", stroke));

            if (shape == Shape.RoundedBox) {
                writer.writeLine("roundCorner 20");
            }
            writer.outdent();
            writer.writeLine("}");
        }

        writer.writeLine();
    }

    @Override
    protected void startEnterpriseBoundary(View view, String enterpriseName, IndentingWriter writer) {
        if (!useSequenceDiagrams(view)) {
            writer.writeLine(String.format("package \"%s\" <<enterprise>> {", enterpriseName));
            writer.indent();
            writer.writeLine("skinparam PackageBorderColor<<enterprise>> #444444");
            writer.writeLine("skinparam PackageFontColor<<enterprise>> #444444");
            writer.writeLine();
        }
    }

    @Override
    protected void endEnterpriseBoundary(View view, IndentingWriter writer) {
        if (!useSequenceDiagrams(view)) {
            writer.outdent();
            writer.writeLine("}");
            writer.writeLine();
        }
    }

    @Override
    protected void startGroupBoundary(View view, String group, IndentingWriter writer) {
        if (!useSequenceDiagrams(view)) {
            String groupId;
            String color = "#cccccc";

            // is there a style for the group?
            ElementStyle elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle("Group:" + group);
            groupId = "group:" + group;

            if (elementStyle == null || StringUtils.isNullOrEmpty(elementStyle.getColor())) {
                // no, so is there a default group style?
                elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle("Group");
                groupId = "group";
            }

            if (elementStyle != null && !StringUtils.isNullOrEmpty(elementStyle.getColor())) {
                color = elementStyle.getColor();
            }

            writer.writeLine(String.format("package \"%s\\n[Group]\" <<%s>> {", group, groupId));
            writer.indent();
            writer.writeLine(String.format("skinparam PackageBorderColor<<%s>> %s", groupId, color));
            writer.writeLine(String.format("skinparam PackageFontColor<<%s>> %s", groupId, color));
            writer.writeLine();
        }
    }

    @Override
    protected void endGroupBoundary(View view, IndentingWriter writer) {
        if (!useSequenceDiagrams(view)) {
            writer.outdent();
            writer.writeLine("}");
            writer.writeLine();
        }
    }

    @Override
    protected void startSoftwareSystemBoundary(View view, SoftwareSystem softwareSystem, IndentingWriter writer) {
        if (!useSequenceDiagrams(view)) {
            String color;
            if (softwareSystem.equals(view.getSoftwareSystem())) {
                color = "#444444";
            } else {
                color = "#cccccc";
            }

            writer.writeLine(String.format("package \"%s\\n%s\" <<%s>> {", softwareSystem.getName(), typeOf(view, softwareSystem, true), idOf(softwareSystem)));
            writer.indent();
            writer.writeLine(String.format("skinparam PackageBorderColor<<%s>> %s", idOf(softwareSystem), color));
            writer.writeLine(String.format("skinparam PackageFontColor<<%s>> %s", idOf(softwareSystem), color));
            writer.writeLine();
        }
    }

    @Override
    protected void endSoftwareSystemBoundary(View view, IndentingWriter writer) {
        if (!useSequenceDiagrams(view)) {
            writer.outdent();
            writer.writeLine("}");
            writer.writeLine();
        }
    }

    @Override
    protected void startContainerBoundary(View view, Container container, IndentingWriter writer) {
        if (!useSequenceDiagrams(view)) {
            String color = "#444444";
            if (view instanceof ComponentView) {
                if (container.equals(((ComponentView) view).getContainer())) {
                    color = "#444444";
                } else {
                    color = "#cccccc";
                }
            } else if (view instanceof DynamicView) {
                if (container.equals(((DynamicView) view).getElement())) {
                    color = "#444444";
                } else {
                    color = "#cccccc";
                }
            }

            writer.writeLine(String.format("package \"%s\\n%s\" <<%s>> {", container.getName(), typeOf(view, container, true), idOf(container)));
            writer.indent();
            writer.writeLine(String.format("skinparam PackageBorderColor<<%s>> %s", idOf(container), color));
            writer.writeLine(String.format("skinparam PackageFontColor<<%s>> %s", idOf(container), color));
            writer.writeLine();
        }
    }

    @Override
    protected void endContainerBoundary(View view, IndentingWriter writer) {
        if (!useSequenceDiagrams(view)) {
            writer.outdent();
            writer.writeLine("}");
            writer.writeLine();
        }
    }

    @Override
    protected void startDeploymentNodeBoundary(DeploymentView view, DeploymentNode deploymentNode, IndentingWriter writer) {
        String url = deploymentNode.getUrl();
        if (!StringUtils.isNullOrEmpty(url)) {
            url = " [[" + url + "]]";
        } else {
            url = "";
        }

        Set<String> tags = deploymentNode.getTagsAsSet();
        Styles styles = view.getViewSet().getConfiguration().getStyles();
        String icon = null;
        for (String tag : tags) {
            ElementStyle elementStyle = styles.findElementStyle(tag);
            if (elementStyle != null) {
                // note: we should probably introduce a scale attribute to configure the image scale
                if (elementStyle.getIcon() != null) {
                    icon = format("<img:%s>", elementStyle.getIcon());
                }
            }
        }
        String name = deploymentNode.getName() + (deploymentNode.getInstances() > 1 ? " (x" + deploymentNode.getInstances() + ")" : "");
        writer.writeLine(
                format("node \"%s\\n%s\" <<%s>> as %s%s {",
                        Stream.of(icon, name).filter(Objects::nonNull).collect(Collectors.joining(" ")),
                        typeOf(view, deploymentNode, true),
                        idOf(deploymentNode),
                        idOf(deploymentNode),
                        url
                )
        );
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
    public Diagram export(DynamicView view) {
        if (useSequenceDiagrams(view)) {
            IndentingWriter writer = new IndentingWriter();
            writeHeader(view, writer);

            Set<Element> elements = new LinkedHashSet<>();
            for (RelationshipView relationshipView : view.getRelationships()) {
                elements.add(relationshipView.getRelationship().getSource());
                elements.add(relationshipView.getRelationship().getDestination());
            }

            for (Element element : elements) {
                writeElement(view, element, writer);
            }

            writeRelationships(view, writer);
            writeFooter(view, writer);

            return new Diagram(view, writer.toString());
        } else {
            return super.export(view);
        }
    }

    @Override
    protected void writeElement(View view, Element element, IndentingWriter writer) {
        ElementStyle elementStyle = findElementStyle(view, element);

        if (view instanceof DynamicView && useSequenceDiagrams(view)) {
            writer.writeLine(String.format("%s \"%s\\n<size:10>%s</size>\" as %s <<%s>> %s",
                    plantumlSequenceType(view, element),
                    element.getName(),
                    typeOf(view, element, true),
                    idOf(element),
                    idOf(element),
                    elementStyle.getBackground()));
        } else {
            String shape = plantUMLShapeOf(view, element);
            if ("actor".equals(shape)) {
                shape = "rectangle";
            }
            String name = element.getName();
            String description = element.getDescription();
            String type = typeOf(view, element, true);

            String url = element.getUrl();
            if (!StringUtils.isNullOrEmpty(url)) {
                url = " [[" + url + "]]";
            } else {
                url = "";
            }

            if (element instanceof StaticStructureElementInstance) {
                StaticStructureElementInstance elementInstance = (StaticStructureElementInstance) element;
                name = elementInstance.getElement().getName();
                description = elementInstance.getElement().getDescription();
                type = typeOf(view, elementInstance.getElement(), true);
                shape = plantUMLShapeOf(view, elementInstance.getElement());

                if (StringUtils.isNullOrEmpty(url)) {
                    url = element.getUrl();
                    if (!StringUtils.isNullOrEmpty(url)) {
                        url = " [[" + url + "]]";
                    } else {
                        url = "";
                    }
                }
            }

            if (StringUtils.isNullOrEmpty(description) || false == elementStyle.getDescription()) {
                description = "";
            } else {
                description = "\\n\\n" + description;
            }

            if (StringUtils.isNullOrEmpty(type) || false == elementStyle.getMetadata()) {
                type = "";
            } else {
                type = String.format("\\n<size:10>%s</size>", type);
            }

            String id = idOf(element);

            writer.writeLine(format("%s \"==%s%s%s\" <<%s>> as %s%s",
                    shape,
                    name,
                    type,
                    description,
                    id,
                    id,
                    url)
            );

            if (!isVisible(view, element)) {
                writer.writeLine("hide " + id);
            }
        }
    }

    @Override
    protected void writeRelationship(View view, RelationshipView relationshipView, IndentingWriter writer) {
        Relationship relationship = relationshipView.getRelationship();
        RelationshipStyle style = findRelationshipStyle(view, relationship);

        String description = "";
        String technology = relationship.getTechnology();

        if (view instanceof DynamicView && useSequenceDiagrams(view)) {
            // do nothing - sequence diagrams don't need the order
        } else {
            if (!StringUtils.isNullOrEmpty(relationshipView.getOrder())) {
                description = relationshipView.getOrder() + ". ";
            }
        }

        description += (hasValue(relationshipView.getDescription()) ? relationshipView.getDescription() : hasValue(relationshipView.getRelationship().getDescription()) ? relationshipView.getRelationship().getDescription() : "");

        if (view instanceof DynamicView && useSequenceDiagrams(view)) {
            String arrowStart = "-";
            String arrowEnd = ">";

            if (relationshipView.isResponse() != null && relationshipView.isResponse() == true) {
                arrowStart = "<-";
                arrowEnd = "-";
            }

            writer.writeLine(
                    String.format("%s %s[%s]%s %s : %s",
                            idOf(relationship.getSource()),
                            arrowStart,
                            style.getColor(),
                            arrowEnd,
                            idOf(relationship.getDestination()),
                            description));
        } else {
            boolean solid = style.getStyle() == LineStyle.Solid || false == style.getDashed();

            String arrowStart;
            String arrowEnd;
            String relationshipStyle = style.getColor();

            if (style.getThickness() != null) {
                relationshipStyle += ",thickness=" + style.getThickness();
            }

            if (relationshipView.isResponse() != null && relationshipView.isResponse()) {
                arrowStart = solid ? "<-" : "<.";
                arrowEnd = solid ? "-" : ".";
            } else {
                arrowStart = solid ? "-" : ".";
                arrowEnd = solid ? "->" : ".>";
            }

            if (!isVisible(view, relationshipView)) {
                relationshipStyle = "hidden";
            }

            // 1 .[#rrggbb,thickness=n].> 2 : "...\n<size:8>...</size>
            writer.writeLine(format("%s %s[%s]%s %s : \"<color:%s>%s%s\"",
                    idOf(relationship.getSource()),
                    arrowStart,
                    relationshipStyle,
                    arrowEnd,
                    idOf(relationship.getDestination()),
                    style.getColor(),
                    description,
                    (StringUtils.isNullOrEmpty(technology) ? "" : "\\n<color:" + style.getColor() + "><size:8>[" + technology + "]</size>")
            ));
        }
    }

}