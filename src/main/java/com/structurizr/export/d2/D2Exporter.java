package com.structurizr.export.d2;

import com.structurizr.export.AbstractDiagramExporter;
import com.structurizr.export.Diagram;
import com.structurizr.export.IndentingWriter;
import com.structurizr.model.*;
import com.structurizr.view.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class D2Exporter extends AbstractDiagramExporter {

    @Override
    protected void writeHeader(View view, IndentingWriter writer) {
        if (hasValue(view.getTitle())) {
            writer.writeLine(String.format("desc: %s", view.getTitle()));
        }
    }

    @Override
    protected void writeFooter(View view, IndentingWriter writer) {
    }

    @Override
    protected void startEnterpriseBoundary(View view, String enterpriseName, IndentingWriter writer) {
        writer.writeLine(String.format("%s: {", enterpriseId(enterpriseName)));
        writer.indent();
        writer.writeLine(String.format("label: \"%s\"", enterpriseName));
        writeGroupStyle(writer);
    }

    private static void writeGroupStyle(IndentingWriter writer) {
        writer.writeLine("style: {");
        writer.indent();
        writer.writeLine("fill: white");
        writer.writeLine("stroke: black");
        writer.outdent();
        writer.writeLine("}");
    }

    @Override
    protected void endEnterpriseBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
    }

    @Override
    protected void startGroupBoundary(View view, String group, IndentingWriter writer) {
        writer.writeLine(String.format("%s: {", groupId(group)));
        writer.indent();
        writer.writeLine(String.format("label: \"%s\"", group));
        writeGroupStyle(writer);
    }

    @Override
    protected void endGroupBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
    }

    @Override
    protected void startSoftwareSystemBoundary(View view, SoftwareSystem softwareSystem, IndentingWriter writer) {
        writer.writeLine(String.format("%s: {", idWithPrefix(softwareSystem.getId())));
        writer.indent();
        writer.writeLine(getLabel(view, softwareSystem));
        writeStyle(view, softwareSystem, writer);
    }

    @Override
    protected void endSoftwareSystemBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
    }

    @Override
    protected void startContainerBoundary(View view, Container container, IndentingWriter writer) {
        writer.writeLine(String.format("%s: {", idWithPrefix(container.getId())));
        writer.indent();
        writer.writeLine(getLabel(view, container));
        writer.writeLine(String.format("shape: %s", d2ShapeOf(view, container)));
        writeStyle(view, container, writer);
    }

    @Override
    protected void endContainerBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
    }

    @Override
    protected void startDeploymentNodeBoundary(DeploymentView view, DeploymentNode deploymentNode, IndentingWriter writer) {
        writer.writeLine(String.format("%s: {", idWithPrefix(deploymentNode.getId())));
        writer.indent();
        writer.writeLine(getLabel(view, deploymentNode));
        writer.writeLine(String.format("shape: %s", d2ShapeOf(view, deploymentNode)));
        writeStyle(view, deploymentNode, writer);
    }

    @Override
    protected void endDeploymentNodeBoundary(View view, IndentingWriter writer) {
        writer.outdent();
        writer.writeLine("}");
    }

    @Override
    protected void writeElement(View view, Element element, IndentingWriter writer) {
        String idWithPrefix = idWithPrefix(element.getId());
        writer.writeLine(String.format("%s: {", idWithPrefix));
        writer.indent();
        writer.writeLine(getLabel(view, element));
        writer.writeLine(String.format("shape: %s", d2ShapeOf(view, element)));
        writeStyle(view, element, writer);
        writer.outdent();
        writer.writeLine("}");
    }

    private void writeStyle(View view, Element element, IndentingWriter writer) {
        ElementStyle elementStyle = findElementStyle(view, element);
        writer.writeLine("style: {");
        writer.indent();
        writer.writeLine(String.format("fill: \"%s\"", elementStyle.getBackground()));
        writer.writeLine(String.format("stroke: \"%s\"", elementStyle.getStroke()));
        if (Objects.nonNull(elementStyle.getStrokeWidth())) {
            writer.writeLine(String.format("stroke-width: %s", elementStyle.getStrokeWidth()));
        }
        if (elementStyle.getBorder() == Border.Dashed) {
            writer.writeLine("stroke-dash: 5");
        } else if (elementStyle.getBorder() == Border.Dotted) {
            writer.writeLine("stroke-dash: 2");
        }
        writer.writeLine(String.format("multiple: %s", element instanceof DeploymentNode && ((DeploymentNode) element).getInstances() > 1));
        writer.writeLine(String.format("font-color: \"%s\"", elementStyle.getColor()));
        writer.writeLine(String.format("font-size: %s", elementStyle.getFontSize()));
        writer.outdent();
        writer.writeLine("}");
    }

    private static boolean isBackgroundLight(String background) {
        int color = Integer.parseInt(background.substring(1), 16);
        int r = color >> 16;
        int g = color >> 8 & 255;
        int b = color & 255;
        // http://alienryderflex.com/hsp.html
        double hsp = Math.sqrt(0.299 * (r * r) + 0.587 * (g * g) + 0.114 * (b * b));
        return hsp > 127.5;
    }

    @Override
    protected void writeRelationship(View view, RelationshipView relationshipView, IndentingWriter writer) {
        Relationship relationship = relationshipView.getRelationship();
        boolean isResponse = Optional.ofNullable(relationshipView.isResponse()).orElse(false);

        writer.writeLine(String.format("%s %s %s {", getAbsolutePath(view, relationship.getSource()), isResponse ? "<-" : "->", getAbsolutePath(view, relationship.getDestination())));
        writer.indent();
        writer.writeLine(getLabel(view, relationshipView));
        writeStyle(view, relationship, writer);
        writer.outdent();
        writer.writeLine("}");
    }

    private String typeOf(View view, Relationship relationship) {
        if (hasValue(relationship.getTechnology())) {
            return wrapMetadata(view, relationship.getTechnology());
        }
        return "";
    }

    private void writeStyle(View view, Relationship relationship, IndentingWriter writer) {
        RelationshipStyle relationshipStyle = findRelationshipStyle(view, relationship);
        writer.writeLine("style: {");
        writer.indent();
        writer.writeLine(String.format("stroke: \"%s\"", relationshipStyle.getColor()));
        Optional.ofNullable(relationshipStyle.getStyle())
                .ifPresent(lineStyle -> {
                    if (lineStyle == LineStyle.Dashed) {
                        writer.writeLine("stroke-dash: 5");
                    } else if (lineStyle == LineStyle.Dotted) {
                        writer.writeLine("stroke-dash: 2");
                    }
                });
        writer.writeLine(String.format("stroke-width: %s", relationshipStyle.getThickness()));
        writer.outdent();
        writer.writeLine("}");
    }

    @Override
    protected Diagram createDiagram(View view, String definition) {
        return new D2Diagram(view, definition);
    }


    String d2ShapeOf(View view, Element element) {
        return d2ShapeOf(findElementStyle(view, element).getShape());
    }

    String d2ShapeOf(Shape shape) {
        switch (shape) {
            case Person:
            case Robot:
                return "person";
            case Cylinder:
                return "cylinder";
            case Folder:
                return "package";
            case Ellipse:
                return "oval";
            case Circle:
                return "circle";
            case Hexagon:
                return "hexagon";
            case Pipe:
                return "queue";
            case Diamond:
                return "diamond";
            default:
                return "rectangle";
        }
    }

    private String getAbsolutePath(View view, Element element) {
        Deque<Element> pathFromParent = new LinkedList<>();
        do {
            pathFromParent.addFirst(element);
        } while (
                (element = element.getParent()) != null
                        && (
                        view.isElementInView(element)
                                || (view instanceof ComponentView && ((ComponentView) view).getContainer().equals(element))
                                || (view instanceof ContainerView && view.getSoftwareSystem().equals(element))
                                || (view instanceof DynamicView) && ((DynamicView) view).getElement().equals(element))
        );
        String enterprisePrefix = getEnterprisePrefix(view, pathFromParent.peek());
        return pathFromParent.stream().map(it -> view.isElementInView(it) ? idWithGroupAndPrefix(it) : idWithPrefix(it.getId())).collect(Collectors.joining(".", enterprisePrefix, ""));
    }

    private String getEnterprisePrefix(View view, Element parent) {
        boolean enterpriseBoundaryVisible =
                (view instanceof SystemLandscapeView && ((SystemLandscapeView) view).isEnterpriseBoundaryVisible())
                        || (view instanceof SystemContextView && ((SystemContextView) view).isEnterpriseBoundaryVisible());
        if (enterpriseBoundaryVisible
                && (
                parent instanceof Person && ((Person) parent).getLocation() == Location.Internal
                        || parent instanceof SoftwareSystem && ((SoftwareSystem) parent).getLocation() == Location.Internal
        )) {
            return String.format("%s.", enterpriseId(view.getModel().getEnterprise().getName()));
        }
        return "";
    }

    private String idWithGroupAndPrefix(Element element) {
        String idWithPrefix = idWithPrefix(element.getId());
        if (element instanceof GroupableElement && hasValue(((GroupableElement) element).getGroup())) {
            return String.format("%s.%s", groupId(((GroupableElement) element).getGroup()), idWithPrefix);
        } else {
            return idWithPrefix;
        }
    }

    private String idWithPrefix(String id) {
        return String.format("container_%s", id);
    }

    private String groupId(String groupName) {
        return String.format("\"group_%s\"", groupName);
    }

    private String enterpriseId(String enterpriseName) {
        return String.format("\"enterprise_%s\"", enterpriseName);
    }

    private String getLabel(View view, Element element) {
        String typeOf = typeOf(view, element, true);
        if (hasValue(typeOf)) {
            return String.format("label: \"%s\n%s\"", element.getName(), typeOf);
        }
        return String.format("label: \"%s\"", element.getName());
    }

    private String getLabel(View view, RelationshipView relationshipView) {
        if (hasValue(relationshipView.getDescription())) {
            if (hasValue(relationshipView.getOrder())) {
                return String.format("label: \"%s – %s\"", relationshipView.getOrder(), relationshipView.getDescription());
            }
            return String.format("label: \"%s\"", relationshipView.getDescription());
        }
        Relationship relationship = relationshipView.getRelationship();
        String typeOf = typeOf(view, relationship);
        if (hasValue(typeOf)) {
            return String.format("label: \"%s\n%s\"", relationship.getDescription(), typeOf);
        }
        return String.format("label: \"%s\"", relationship.getDescription());
    }
}
