package com.structurizr.export.plantuml;

import com.structurizr.export.Diagram;
import com.structurizr.export.IndentingWriter;
import com.structurizr.export.Legend;
import com.structurizr.model.*;
import com.structurizr.util.StringUtils;
import com.structurizr.view.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class StructurizrPlantUMLExporter extends AbstractPlantUMLExporter {

    public static final String PLANTUML_SEQUENCE_DIAGRAM_PROPERTY = "plantuml.sequenceDiagram";

    private static final double MAX_ICON_SIZE = 50.0;

    public StructurizrPlantUMLExporter() {
        addSkinParam("shadowing", "false");
        addSkinParam("arrowFontSize", "10");
        addSkinParam("defaultTextAlignment", "center");
        addSkinParam("wrapWidth", "200");
        addSkinParam("maxMessageSize", "100");
    }

    @Override
    protected void writeHeader(View view, IndentingWriter writer) {
        super.writeHeader(view, writer);

        if (view instanceof DynamicView && renderAsSequenceDiagram(view)) {
            // do nothing
        } else {
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
        }

        writeSkinParams(writer);
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

            if (view instanceof DynamicView && renderAsSequenceDiagram(view)) {
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
        if (!renderAsSequenceDiagram(view)) {
            writer.writeLine(String.format("package \"%s\" <<enterprise>> {", enterpriseName));
            writer.indent();
            writer.writeLine("skinparam PackageBorderColor<<enterprise>> #444444");
            writer.writeLine("skinparam PackageFontColor<<enterprise>> #444444");
            writer.writeLine();
        }
    }

    @Override
    protected void endEnterpriseBoundary(View view, IndentingWriter writer) {
        if (!renderAsSequenceDiagram(view)) {
            writer.outdent();
            writer.writeLine("}");
            writer.writeLine();
        }
    }

    @Override
    protected void startGroupBoundary(View view, String group, IndentingWriter writer) {
        if (!renderAsSequenceDiagram(view)) {
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
        if (!renderAsSequenceDiagram(view)) {
            writer.outdent();
            writer.writeLine("}");
            writer.writeLine();
        }
    }

    @Override
    protected void startSoftwareSystemBoundary(View view, SoftwareSystem softwareSystem, IndentingWriter writer) {
        if (!renderAsSequenceDiagram(view)) {
            ElementStyle elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle(softwareSystem);
            String color = elementStyle.getStroke();

            writer.writeLine(String.format("package \"%s\\n%s\" <<%s>> {", softwareSystem.getName(), typeOf(view, softwareSystem, true), idOf(softwareSystem)));
            writer.indent();
            writer.writeLine(String.format("skinparam PackageBorderColor<<%s>> %s", idOf(softwareSystem), color));
            writer.writeLine(String.format("skinparam PackageFontColor<<%s>> %s", idOf(softwareSystem), color));
            writer.writeLine();
        }
    }

    @Override
    protected void endSoftwareSystemBoundary(View view, IndentingWriter writer) {
        if (!renderAsSequenceDiagram(view)) {
            writer.outdent();
            writer.writeLine("}");
            writer.writeLine();
        }
    }

    @Override
    protected void startContainerBoundary(View view, Container container, IndentingWriter writer) {
        if (!renderAsSequenceDiagram(view)) {
            ElementStyle elementStyle = view.getViewSet().getConfiguration().getStyles().findElementStyle(container);
            String color = elementStyle.getStroke();

            writer.writeLine(String.format("package \"%s\\n%s\" <<%s>> {", container.getName(), typeOf(view, container, true), idOf(container)));
            writer.indent();
            writer.writeLine(String.format("skinparam PackageBorderColor<<%s>> %s", idOf(container), color));
            writer.writeLine(String.format("skinparam PackageFontColor<<%s>> %s", idOf(container), color));
            writer.writeLine();
        }
    }

    @Override
    protected void endContainerBoundary(View view, IndentingWriter writer) {
        if (!renderAsSequenceDiagram(view)) {
            writer.outdent();
            writer.writeLine("}");
            writer.writeLine();
        }
    }

    @Override
    protected void startDeploymentNodeBoundary(DeploymentView view, DeploymentNode deploymentNode, IndentingWriter writer) {
        ElementStyle elementStyle = findElementStyle(view, deploymentNode);

        String icon = "";
        if (elementStyleHasSupportedIcon(elementStyle)) {
            double scale = calculateIconScale(elementStyle);
            icon = "\\n\\n<img:" + elementStyle.getIcon() + "{scale=" + scale + "}>";
        }

        String url = deploymentNode.getUrl();
        if (!StringUtils.isNullOrEmpty(url)) {
            url = " [[" + url + "]]";
        } else {
            url = "";
        }

        writer.writeLine(
                format("rectangle \"%s\\n<size:10>%s</size>%s\" <<%s>> as %s%s {",
                        deploymentNode.getName() + (!"1".equals(deploymentNode.getInstances()) ? " (x" + deploymentNode.getInstances() + ")" : ""),
                        typeOf(view, deploymentNode, true),
                        icon,
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
        if (renderAsSequenceDiagram(view)) {
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

            return createDiagram(view, writer.toString());
        } else {
            return super.export(view);
        }
    }

    @Override
    protected void writeElement(View view, Element element, IndentingWriter writer) {
        ElementStyle elementStyle = findElementStyle(view, element);

        if (view instanceof DynamicView && renderAsSequenceDiagram(view)) {
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
            String icon = "";

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

            if (elementStyleHasSupportedIcon(elementStyle)) {
                double scale = calculateIconScale(elementStyle);
                icon = "\\n\\n<img:" + elementStyle.getIcon() + "{scale=" + scale + "}>";
            }

            String id = idOf(element);

            writer.writeLine(format("%s \"==%s%s%s%s\" <<%s>> as %s%s",
                    shape,
                    name,
                    type,
                    icon,
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

        if (view instanceof DynamicView && renderAsSequenceDiagram(view)) {
            // do nothing - sequence diagrams don't need the order
        } else {
            if (!StringUtils.isNullOrEmpty(relationshipView.getOrder())) {
                description = relationshipView.getOrder() + ". ";
            }
        }

        description += (hasValue(relationshipView.getDescription()) ? relationshipView.getDescription() : hasValue(relationshipView.getRelationship().getDescription()) ? relationshipView.getRelationship().getDescription() : "");

        if (view instanceof DynamicView && renderAsSequenceDiagram(view)) {
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

    @Override
    protected Legend createLegend(View view) {
        IndentingWriter writer = new IndentingWriter();
        int id = 0;

        writer.writeLine("@startuml");
        writer.writeLine();

        writer.writeLine("skinparam {");
        writer.indent();
        writer.writeLine("shadowing false");
        writer.writeLine("arrowFontSize 15");
        writer.writeLine("defaultTextAlignment center");
        writer.writeLine("wrapWidth 100");
        writer.writeLine("maxMessageSize 100");
        writer.outdent();
        writer.writeLine("}");

        writer.writeLine("hide stereotype");
        writer.writeLine();

        writer.writeLine("skinparam rectangle<<_transparent>> {");
        writer.indent();
        writer.writeLine("BorderColor transparent");
        writer.writeLine("BackgroundColor transparent");
        writer.writeLine("FontColor transparent");
        writer.outdent();
        writer.writeLine("}");
        writer.writeLine();

        Map<String,ElementStyle> elementStyles = new HashMap<>();
        List<Element> elements = view.getElements().stream().map(ElementView::getElement).collect(Collectors.toList());
        for (Element element : elements) {
            ElementStyle elementStyle = findElementStyle(view, element);

            if (element instanceof DeploymentNode) {
                // deployment node backgrounds are always white
                elementStyle.setBackground("#ffffff");
            }

            if (!StringUtils.isNullOrEmpty(elementStyle.getTag()) ) {
                elementStyles.put(elementStyle.getTag(), elementStyle);
            };
        }

        List<ElementStyle> sortedElementStyles = elementStyles.values().stream().sorted(Comparator.comparing(ElementStyle::getTag)).collect(Collectors.toList());;
        for (ElementStyle elementStyle : sortedElementStyles) {
            id++;
            Shape shape = elementStyle.getShape();
            String type = plantUMLShapeOf(elementStyle.getShape());
            if ("actor".equals(type)) {
                type = "rectangle"; // the actor shape is not supported in this implementation
            }

            String background = elementStyle.getBackground();
            String stroke = elementStyle.getStroke();
            String color = elementStyle.getColor();

            if (view instanceof DynamicView && renderAsSequenceDiagram(view)) {
                type = "sequenceParticipant";
            }

            writer.writeLine(format("skinparam %s<<%s>> {", type, id));
            writer.indent();
            writer.writeLine(String.format("BackgroundColor %s", background));
            writer.writeLine(String.format("FontColor %s", color));
            writer.writeLine(String.format("BorderColor %s", stroke));

            if (shape == Shape.RoundedBox) {
                writer.writeLine("roundCorner 20");
            }
            writer.outdent();
            writer.writeLine("}");

            String description = elementStyle.getTag();
            if (description.startsWith("Element,")) {
                description = description.substring("Element,".length());
            }
            description = description.replaceAll(",", ", ");

            String icon = "";
            if (elementStyleHasSupportedIcon(elementStyle)) {
                double scale = calculateIconScale(elementStyle);
                icon = "\\n\\n<img:" + elementStyle.getIcon() + "{scale=" + scale + "}>";
            }

            writer.writeLine(format("%s \"==%s%s\" <<%s>>",
                    type,
                    description,
                    icon,
                    id)
            );
            writer.writeLine();
        }

        Map<String,RelationshipStyle> relationshipStyles = new HashMap<>();
        List<Relationship> relationships = view.getRelationships().stream().map(RelationshipView::getRelationship).collect(Collectors.toList());
        for (Relationship relationship : relationships) {
            RelationshipStyle relationshipStyle = findRelationshipStyle(view, relationship);

            if (!StringUtils.isNullOrEmpty(relationshipStyle.getTag())) {
                relationshipStyles.put(relationshipStyle.getTag(), relationshipStyle);
            }
        }

        List<RelationshipStyle> sortedRelationshipStyles = relationshipStyles.values().stream().sorted(Comparator.comparing(RelationshipStyle::getTag)).collect(Collectors.toList());;
        for (RelationshipStyle relationshipStyle : sortedRelationshipStyles) {
            id++;

            String description = relationshipStyle.getTag();
            if (description.startsWith("Relationship,")) {
                description = description.substring("Relationship,".length());
            }
            description = description.replaceAll(",", ", ");

            writer.writeLine(format("rectangle \".\" <<_transparent>> as %s", id));

            boolean solid = relationshipStyle.getStyle() == LineStyle.Solid || false == relationshipStyle.getDashed();

            String arrowStart = solid ? "-" : ".";
            String arrowEnd = solid ? "->" : ".>";
            String buf = relationshipStyle.getColor();

            if (relationshipStyle.getThickness() != null) {
                buf += ",thickness=" + relationshipStyle.getThickness();
            }

            // 1 .[#rrggbb,thickness=n].> 2 : "..."
            writer.writeLine(format("%s %s[%s]%s %s : \"<color:%s>%s\"",
                    id,
                    arrowStart,
                    buf,
                    arrowEnd,
                    id,
                    relationshipStyle.getColor(),
                    description)
            );

            writer.writeLine();
        }

        writer.writeLine();

        writer.writeLine("@enduml");

        return new Legend(writer.toString());
    }

    private boolean elementStyleHasSupportedIcon(ElementStyle elementStyle) {
        return !StringUtils.isNullOrEmpty(elementStyle.getIcon()) && elementStyle.getIcon().startsWith("http");
    }

    private double calculateIconScale(ElementStyle elementStyle) {
        String icon = elementStyle.getIcon();
        double scale = 0.5;

        try {
            URL url = new URL(icon);
            BufferedImage bi = ImageIO.read(url);

            int width = bi.getWidth();
            int height = bi.getHeight();

            scale = MAX_ICON_SIZE / Math.max(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scale;
    }

    protected boolean renderAsSequenceDiagram(View view) {
        return view instanceof DynamicView && "true".equalsIgnoreCase(getViewOrViewSetProperty(view, PLANTUML_SEQUENCE_DIAGRAM_PROPERTY, "false"));
    }

}