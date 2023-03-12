package com.structurizr.export.plantuml;

import com.structurizr.Workspace;
import com.structurizr.export.AbstractExporterTests;
import com.structurizr.export.Diagram;
import com.structurizr.model.*;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class StructurizrPlantUMLDiagramExporterTests extends AbstractExporterTests {

    @Test
    public void test_BigBankPlcExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-36141-workspace.json"));
        workspace.getViews().getConfiguration().addProperty(StructurizrPlantUMLExporter.PLANTUML_ANIMATION_PROPERTY, "true");

        StructurizrPlantUMLExporter exporter = new StructurizrPlantUMLExporter();

        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(7, diagrams.size());

        Diagram diagram = diagrams.stream().filter(d -> d.getKey().equals("SystemLandscape")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/36141-SystemLandscape.puml"));
        assertEquals(expected, diagram.getDefinition());
        assertEquals(3, diagram.getFrames().size());

        //assertEquals("", diagram.getLegend().getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("SystemContext")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/36141-SystemContext.puml"));
        assertEquals(expected, diagram.getDefinition());
        assertEquals(4, diagram.getFrames().size());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("Containers")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/36141-Containers.puml"));
        assertEquals(expected, diagram.getDefinition());
        assertEquals(6, diagram.getFrames().size());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("Components")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/36141-Components.puml"));
        assertEquals(expected, diagram.getDefinition());
        assertEquals(4, diagram.getFrames().size());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("SignIn")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/36141-SignIn.puml"));
        assertEquals(expected, diagram.getDefinition());
        assertEquals(6, diagram.getFrames().size());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("DevelopmentDeployment")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/36141-DevelopmentDeployment.puml"));
        assertEquals(expected, diagram.getDefinition());
        assertEquals(4, diagram.getFrames().size());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("LiveDeployment")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/36141-LiveDeployment.puml"));
        assertEquals(expected, diagram.getDefinition());
        assertEquals(6, diagram.getFrames().size());

        // and the sequence diagram version
        workspace.getViews().getConfiguration().addProperty(exporter.PLANTUML_SEQUENCE_DIAGRAM_PROPERTY, "true");
        diagrams = exporter.export(workspace);
        diagram = diagrams.stream().filter(d -> d.getKey().equals("SignIn")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/36141-SignIn-sequence.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_AmazonWebServicesExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-54915-workspace.json"));
        ThemeUtils.loadThemes(workspace);
        workspace.getViews().getDeploymentViews().iterator().next().enableAutomaticLayout(AutomaticLayout.RankDirection.LeftRight, 300, 300);

        StructurizrPlantUMLExporter exporter = new StructurizrPlantUMLExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(1, diagrams.size());

        Diagram diagram = diagrams.stream().findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/54915-AmazonWebServicesDeployment.puml"));
        assertEquals(expected, diagram.getDefinition());

        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/54915-AmazonWebServicesDeployment-Legend.puml"));
        assertEquals(expected, diagram.getLegend().getDefinition());
    }

    @Test
    public void test_GroupsExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/groups.json"));
        ThemeUtils.loadThemes(workspace);

        StructurizrPlantUMLExporter exporter = new StructurizrPlantUMLExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(3, diagrams.size());

        Diagram diagram = diagrams.stream().filter(md -> md.getKey().equals("SystemLandscape")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/groups-SystemLandscape.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("Containers")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/groups-Containers.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("Components")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/groups-Components.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_NestedGroupsExample() throws Exception {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addProperty("structurizr.groupSeparator", "/");

        SoftwareSystem a = workspace.getModel().addSoftwareSystem("Team 1");
        a.setGroup("Organisation 1/Department 1/Team 1");

        SoftwareSystem b = workspace.getModel().addSoftwareSystem("Team 2");
        b.setGroup("Organisation 1/Department 1/Team 2");

        SoftwareSystem c = workspace.getModel().addSoftwareSystem("Organisation 1");
        c.setGroup("Organisation 1");

        SoftwareSystem d = workspace.getModel().addSoftwareSystem("Organisation 2");
        d.setGroup("Organisation 2");

        SoftwareSystem e = workspace.getModel().addSoftwareSystem("Department 1");
        e.setGroup("Organisation 1/Department 1");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("SystemLandscape", "Description");
        view.addAllElements();

        StructurizrPlantUMLExporter exporter = new StructurizrPlantUMLExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);

        Diagram diagram = diagrams.stream().filter(md -> md.getKey().equals("SystemLandscape")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/nested-groups.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_renderGroupStyles() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addPerson("User 1").setGroup("Group 1");
        workspace.getModel().addPerson("User 2").setGroup("Group 2");
        workspace.getModel().addPerson("User 3").setGroup("Group 3");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "");
        view.addDefaultElements();

        workspace.getViews().getConfiguration().getStyles().addElementStyle("Group:Group 1").color("#111111");
        workspace.getViews().getConfiguration().getStyles().addElementStyle("Group:Group 2").color("#222222");

        StructurizrPlantUMLExporter exporter = new StructurizrPlantUMLExporter();
        Diagram diagram = exporter.export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<User1>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "skinparam rectangle<<User2>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "skinparam rectangle<<User3>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"Group 1\" <<group:Group 1>> {\n" +
                "  skinparam RectangleBorderColor<<group:Group 1>> #111111\n" +
                "  skinparam RectangleFontColor<<group:Group 1>> #111111\n" +
                "\n" +
                "  rectangle \"==User 1\\n<size:10>[Person]</size>\" <<User1>> as User1\n" +
                "}\n" +
                "\n" +
                "rectangle \"Group 2\" <<group:Group 2>> {\n" +
                "  skinparam RectangleBorderColor<<group:Group 2>> #222222\n" +
                "  skinparam RectangleFontColor<<group:Group 2>> #222222\n" +
                "\n" +
                "  rectangle \"==User 2\\n<size:10>[Person]</size>\" <<User2>> as User2\n" +
                "}\n" +
                "\n" +
                "rectangle \"Group 3\" <<group>> {\n" +
                "  skinparam RectangleBorderColor<<group>> #cccccc\n" +
                "  skinparam RectangleFontColor<<group>> #cccccc\n" +
                "\n" +
                "  rectangle \"==User 3\\n<size:10>[Person]</size>\" <<User3>> as User3\n" +
                "}\n" +
                "\n" +
                "\n" +
                "@enduml", diagram.getDefinition());

        workspace.getViews().getConfiguration().getStyles().addElementStyle("Group").color("#aabbcc");

        diagram = exporter.export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<User1>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "skinparam rectangle<<User2>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "skinparam rectangle<<User3>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"Group 1\" <<group:Group 1>> {\n" +
                "  skinparam RectangleBorderColor<<group:Group 1>> #111111\n" +
                "  skinparam RectangleFontColor<<group:Group 1>> #111111\n" +
                "\n" +
                "  rectangle \"==User 1\\n<size:10>[Person]</size>\" <<User1>> as User1\n" +
                "}\n" +
                "\n" +
                "rectangle \"Group 2\" <<group:Group 2>> {\n" +
                "  skinparam RectangleBorderColor<<group:Group 2>> #222222\n" +
                "  skinparam RectangleFontColor<<group:Group 2>> #222222\n" +
                "\n" +
                "  rectangle \"==User 2\\n<size:10>[Person]</size>\" <<User2>> as User2\n" +
                "}\n" +
                "\n" +
                "rectangle \"Group 3\" <<group>> {\n" +
                "  skinparam RectangleBorderColor<<group>> #aabbcc\n" +
                "  skinparam RectangleFontColor<<group>> #aabbcc\n" +
                "\n" +
                "  rectangle \"==User 3\\n<size:10>[Person]</size>\" <<User3>> as User3\n" +
                "}\n" +
                "\n" +
                "\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderContainerDiagramWithExternalContainers() {
        Workspace workspace = new Workspace("Name", "Description");
        SoftwareSystem softwareSystem1 = workspace.getModel().addSoftwareSystem("Software System 1");
        Container container1 = softwareSystem1.addContainer("Container 1");
        SoftwareSystem softwareSystem2 = workspace.getModel().addSoftwareSystem("Software System 2");
        Container container2 = softwareSystem2.addContainer("Container 2");

        container1.uses(container2, "Uses");

        ContainerView containerView = workspace.getViews().createContainerView(softwareSystem1, "Containers", "");
        containerView.add(container1);
        containerView.add(container2);

        Diagram diagram = new StructurizrPlantUMLExporter().export(containerView);
        assertEquals("@startuml\n" +
                "set separator none\n" +
                "title Software System 1 - Containers\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<SoftwareSystem1.Container1>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "skinparam rectangle<<SoftwareSystem2.Container2>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"Software System 1\\n<size:10>[Software System]</size>\" <<SoftwareSystem1>> {\n" +
                "  skinparam RectangleBorderColor<<SoftwareSystem1>> #9a9a9a\n" +
                "  skinparam RectangleFontColor<<SoftwareSystem1>> #9a9a9a\n" +
                "\n" +
                "  rectangle \"==Container 1\\n<size:10>[Container]</size>\" <<SoftwareSystem1.Container1>> as SoftwareSystem1.Container1\n" +
                "}\n" +
                "\n" +
                "rectangle \"Software System 2\\n<size:10>[Software System]</size>\" <<SoftwareSystem2>> {\n" +
                "  skinparam RectangleBorderColor<<SoftwareSystem2>> #9a9a9a\n" +
                "  skinparam RectangleFontColor<<SoftwareSystem2>> #9a9a9a\n" +
                "\n" +
                "  rectangle \"==Container 2\\n<size:10>[Container]</size>\" <<SoftwareSystem2.Container2>> as SoftwareSystem2.Container2\n" +
                "}\n" +
                "\n" +
                "SoftwareSystem1.Container1 .[#707070,thickness=2].> SoftwareSystem2.Container2 : \"<color:#707070>Uses\"\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderComponentDiagramWithExternalComponents() throws Exception {
        Workspace workspace = new Workspace("Name", "Description");

        SoftwareSystem softwareSystem1 = workspace.getModel().addSoftwareSystem("Software System 1");
        Container container1 = softwareSystem1.addContainer("Container 1");
        Component component1 = container1.addComponent("Component 1");
        Component component2 = container1.addComponent("Component 2");

        SoftwareSystem softwareSystem2 = workspace.getModel().addSoftwareSystem("Software System 2");
        Container container2 = softwareSystem2.addContainer("Container 2");
        Component component3 = container2.addComponent("Component 3");

        component1.uses(component2, "Uses");
        component2.uses(component3, "Uses");

        ComponentView componentView = workspace.getViews().createComponentView(container1, "Components", "");
        componentView.add(component1);
        componentView.add(component2);
        componentView.add(component3);

        Diagram diagram = new StructurizrPlantUMLExporter().export(componentView);
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/component-view-with-external-components-1.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_renderComponentDiagramWithExternalComponentsAndSoftwareSystemBoundariesIncluded() throws Exception {
        Workspace workspace = new Workspace("Name", "Description");

        SoftwareSystem softwareSystem1 = workspace.getModel().addSoftwareSystem("Software System 1");
        Container container1 = softwareSystem1.addContainer("Container 1");
        Component component1 = container1.addComponent("Component 1");
        Component component2 = container1.addComponent("Component 2");

        SoftwareSystem softwareSystem2 = workspace.getModel().addSoftwareSystem("Software System 2");
        Container container2 = softwareSystem2.addContainer("Container 2");
        Component component3 = container2.addComponent("Component 3");

        component1.uses(component2, "Uses");
        component2.uses(component3, "Uses");

        ComponentView componentView = workspace.getViews().createComponentView(container1, "Components", "");
        componentView.add(component1);
        componentView.add(component2);
        componentView.add(component3);
        componentView.addProperty("structurizr.softwareSystemBoundaries", "true");

        Diagram diagram = new StructurizrPlantUMLExporter().export(componentView);
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/structurizr/component-view-with-external-components-2.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_renderDynamicDiagramWithExternalContainers() {
        Workspace workspace = new Workspace("Name", "Description");
        SoftwareSystem softwareSystem1 = workspace.getModel().addSoftwareSystem("Software System 1");
        Container container1 = softwareSystem1.addContainer("Container 1");
        SoftwareSystem softwareSystem2 = workspace.getModel().addSoftwareSystem("Software System 2");
        Container container2 = softwareSystem2.addContainer("Container 2");

        container1.uses(container2, "Uses");

        DynamicView dynamicView = workspace.getViews().createDynamicView(softwareSystem1, "Dynamic", "");
        dynamicView.add(container1, container2);

        Diagram diagram = new StructurizrPlantUMLExporter().export(dynamicView);
        assertEquals("@startuml\n" +
                "set separator none\n" +
                "title Software System 1 - Dynamic\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<SoftwareSystem1.Container1>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "skinparam rectangle<<SoftwareSystem2.Container2>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"Software System 1\\n<size:10>[Software System]</size>\" <<SoftwareSystem1>> {\n" +
                "  skinparam RectangleBorderColor<<SoftwareSystem1>> #9a9a9a\n" +
                "  skinparam RectangleFontColor<<SoftwareSystem1>> #9a9a9a\n" +
                "\n" +
                "  rectangle \"==Container 1\\n<size:10>[Container]</size>\" <<SoftwareSystem1.Container1>> as SoftwareSystem1.Container1\n" +
                "}\n" +
                "\n" +
                "rectangle \"Software System 2\\n<size:10>[Software System]</size>\" <<SoftwareSystem2>> {\n" +
                "  skinparam RectangleBorderColor<<SoftwareSystem2>> #9a9a9a\n" +
                "  skinparam RectangleFontColor<<SoftwareSystem2>> #9a9a9a\n" +
                "\n" +
                "  rectangle \"==Container 2\\n<size:10>[Container]</size>\" <<SoftwareSystem2.Container2>> as SoftwareSystem2.Container2\n" +
                "}\n" +
                "\n" +
                "SoftwareSystem1.Container1 .[#707070,thickness=2].> SoftwareSystem2.Container2 : \"<color:#707070>1. Uses\"\n" +
                "@enduml", diagram.getDefinition());
    }


    @Test
    public void test_renderDynamicDiagramWithExternalComponents() {
        Workspace workspace = new Workspace("Name", "Description");
        SoftwareSystem softwareSystem1 = workspace.getModel().addSoftwareSystem("Software System 1");
        Container container1 = softwareSystem1.addContainer("Container 1");
        Component component1 = container1.addComponent("Component 1");
        Container container2 = softwareSystem1.addContainer("Container 2");
        Component component2 = container2.addComponent("Component 2");

        component1.uses(component2, "Uses");

        DynamicView dynamicView = workspace.getViews().createDynamicView(container1, "Dynamic", "");
        dynamicView.add(component1, component2);

        Diagram diagram = new StructurizrPlantUMLExporter().export(dynamicView);
        assertEquals("@startuml\n" +
                "set separator none\n" +
                "title Container 1 - Dynamic\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<SoftwareSystem1.Container1.Component1>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "skinparam rectangle<<SoftwareSystem1.Container2.Component2>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"Container 1\\n<size:10>[Container]</size>\" <<SoftwareSystem1.Container1>> {\n" +
                "  skinparam RectangleBorderColor<<SoftwareSystem1.Container1>> #9a9a9a\n" +
                "  skinparam RectangleFontColor<<SoftwareSystem1.Container1>> #9a9a9a\n" +
                "\n" +
                "  rectangle \"==Component 1\\n<size:10>[Component]</size>\" <<SoftwareSystem1.Container1.Component1>> as SoftwareSystem1.Container1.Component1\n" +
                "}\n" +
                "\n" +
                "rectangle \"Container 2\\n<size:10>[Container]</size>\" <<SoftwareSystem1.Container2>> {\n" +
                "  skinparam RectangleBorderColor<<SoftwareSystem1.Container2>> #9a9a9a\n" +
                "  skinparam RectangleFontColor<<SoftwareSystem1.Container2>> #9a9a9a\n" +
                "\n" +
                "  rectangle \"==Component 2\\n<size:10>[Component]</size>\" <<SoftwareSystem1.Container2.Component2>> as SoftwareSystem1.Container2.Component2\n" +
                "}\n" +
                "\n" +
                "SoftwareSystem1.Container1.Component1 .[#707070,thickness=2].> SoftwareSystem1.Container2.Component2 : \"<color:#707070>1. Uses\"\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderDiagramWithElementUrls() {
        Workspace workspace = new Workspace("Name", "Description");
        SoftwareSystem softwareSystem = workspace.getModel().addSoftwareSystem("Software System");
        softwareSystem.setUrl("https://structurizr.com");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addDefaultElements();

        Diagram diagram = new StructurizrPlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<SoftwareSystem>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"==Software System\\n<size:10>[Software System]</size>\" <<SoftwareSystem>> as SoftwareSystem [[https://structurizr.com]]\n" +
                "\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderDiagramWithIncludes() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addSoftwareSystem("Software System");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addDefaultElements();

        view.getViewSet().getConfiguration().addProperty(StructurizrPlantUMLExporter.PLANTUML_INCLUDES_PROPERTY, "styles.puml");

        Diagram diagram = new StructurizrPlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "!include styles.puml\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<SoftwareSystem>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"==Software System\\n<size:10>[Software System]</size>\" <<SoftwareSystem>> as SoftwareSystem\n" +
                "\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderDiagramWithNewLineCharacterInElementName() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addSoftwareSystem("Software\nSystem");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addDefaultElements();

        Diagram diagram = new StructurizrPlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<SoftwareSystem>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"==Software\\nSystem\\n<size:10>[Software System]</size>\" <<SoftwareSystem>> as SoftwareSystem\n" +
                "\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderCustomView() {
        Workspace workspace = new Workspace("Name", "Description");
        Model model = workspace.getModel();

        CustomElement a = model.addCustomElement("A");
        CustomElement b = model.addCustomElement("B", "Custom", "Description");
        a.uses(b, "Uses");

        CustomView view = workspace.getViews().createCustomView("key", "Title", "Description");
        view.addDefaultElements();

        Diagram diagram = new StructurizrPlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title Title\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<1>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "skinparam rectangle<<2>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"==A\" <<1>> as 1\n" +
                "rectangle \"==B\\n<size:10>[Custom]</size>\\n\\nDescription\" <<2>> as 2\n" +
                "\n" +
                "1 .[#707070,thickness=2].> 2 : \"<color:#707070>Uses\"\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    void renderWorkspaceWithUnicodeElementName() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addPerson("Пользователь");
        workspace.getViews().createSystemLandscapeView("key", "Description").addDefaultElements();

        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<Пользователь>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"==Пользователь\\n<size:10>[Person]</size>\" <<Пользователь>> as Пользователь\n" +
                "\n" +
                "@enduml", new StructurizrPlantUMLExporter().export(workspace).stream().findFirst().get().getDefinition());
    }

    @Test
    public void testLegend() {
        Workspace workspace = new Workspace("Name", "Description");
        Model model = workspace.getModel();

        CustomElement a = model.addCustomElement("A");
        a.addTags("Tag 1");
        CustomElement b = model.addCustomElement("B");
        b.addTags("Tag 2");
        a.uses(b, "...").addTags("Tag 3");
        b.uses(a, "...").addTags("Tag 4");

        CustomView view = workspace.getViews().createCustomView("key", "Title", "Description");
        view.addDefaultElements();

        Diagram diagram = new StructurizrPlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 15\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 100\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<_transparent>> {\n" +
                "  BorderColor transparent\n" +
                "  BackgroundColor transparent\n" +
                "  FontColor transparent\n" +
                "}\n" +
                "\n" +
                "skinparam rectangle<<1>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "rectangle \"==Element\" <<1>>\n" +
                "\n" +
                "rectangle \".\" <<_transparent>> as 2\n" +
                "2 .[#707070,thickness=2].> 2 : \"<color:#707070>Relationship\"\n" +
                "\n" +
                "\n" +
                "@enduml", diagram.getLegend().getDefinition());

        workspace.getViews().getConfiguration().getStyles().addElementStyle("Tag 1").background("#ff0000").color("#ffffff").shape(Shape.RoundedBox);
        workspace.getViews().getConfiguration().getStyles().addElementStyle("Tag 2").background("#00ff00").color("#ffffff").shape(Shape.Hexagon);
        workspace.getViews().getConfiguration().getStyles().addRelationshipStyle("Tag 3").color("#0000ff");
        workspace.getViews().getConfiguration().getStyles().addRelationshipStyle("Tag 4").color("#ff00ff").thickness(3).style(LineStyle.Solid);

        diagram = new StructurizrPlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 15\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 100\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<_transparent>> {\n" +
                "  BorderColor transparent\n" +
                "  BackgroundColor transparent\n" +
                "  FontColor transparent\n" +
                "}\n" +
                "\n" +
                "skinparam rectangle<<1>> {\n" +
                "  BackgroundColor #ff0000\n" +
                "  FontColor #ffffff\n" +
                "  BorderColor #b20000\n" +
                "  roundCorner 20\n" +
                "}\n" +
                "rectangle \"==Tag 1\" <<1>>\n" +
                "\n" +
                "skinparam hexagon<<2>> {\n" +
                "  BackgroundColor #00ff00\n" +
                "  FontColor #ffffff\n" +
                "  BorderColor #00b200\n" +
                "}\n" +
                "hexagon \"==Tag 2\" <<2>>\n" +
                "\n" +
                "rectangle \".\" <<_transparent>> as 3\n" +
                "3 .[#0000ff,thickness=2].> 3 : \"<color:#0000ff>Tag 3\"\n" +
                "\n" +
                "rectangle \".\" <<_transparent>> as 4\n" +
                "4 -[#ff00ff,thickness=3]-> 4 : \"<color:#ff00ff>Tag 4\"\n" +
                "\n" +
                "\n" +
                "@enduml", diagram.getLegend().getDefinition());
    }

    @Test
    public void staticDiagramsAreUnchangedWhenSequenceDiagramsAreEnabled() {
        Workspace workspace = new Workspace("Name", "Description");
        Model model = workspace.getModel();

        model.addSoftwareSystem("Software System").setGroup("Group");
        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addAllElements();

        StructurizrPlantUMLExporter exporter = new StructurizrPlantUMLExporter();
        Diagram diagram;
        String expected = "@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<SoftwareSystem>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"Group\" <<group>> {\n" +
                "  skinparam RectangleBorderColor<<group>> #cccccc\n" +
                "  skinparam RectangleFontColor<<group>> #cccccc\n" +
                "\n" +
                "  rectangle \"==Software System\\n<size:10>[Software System]</size>\" <<SoftwareSystem>> as SoftwareSystem\n" +
                "}\n" +
                "\n" +
                "\n" +
                "@enduml";

        diagram = exporter.export(view);
        assertEquals(expected, diagram.getDefinition());

        workspace.getViews().getConfiguration().addProperty(StructurizrPlantUMLExporter.PLANTUML_SEQUENCE_DIAGRAM_PROPERTY, "true");

        diagram = exporter.export(view);
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void testFont() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addPerson("User");
        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addAllElements();
        workspace.getViews().getConfiguration().getBranding().setFont(new Font("Courier"));

        Diagram diagram = new StructurizrPlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 10\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 200\n" +
                "  maxMessageSize 100\n" +
                "  defaultFontName \"Courier\"\n" +
                "}\n" +
                "\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<User>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "\n" +
                "rectangle \"==User\\n<size:10>[Person]</size>\" <<User>> as User\n" +
                "\n" +
                "@enduml", diagram.getDefinition().toString());

        assertEquals("@startuml\nset separator none\n" +
                "\n" +
                "skinparam {\n" +
                "  shadowing false\n" +
                "  arrowFontSize 15\n" +
                "  defaultTextAlignment center\n" +
                "  wrapWidth 100\n" +
                "  maxMessageSize 100\n" +
                "  defaultFontName \"Courier\"\n" +
                "}\n" +
                "hide stereotype\n" +
                "\n" +
                "skinparam rectangle<<_transparent>> {\n" +
                "  BorderColor transparent\n" +
                "  BackgroundColor transparent\n" +
                "  FontColor transparent\n" +
                "}\n" +
                "\n" +
                "skinparam rectangle<<1>> {\n" +
                "  BackgroundColor #dddddd\n" +
                "  FontColor #000000\n" +
                "  BorderColor #9a9a9a\n" +
                "}\n" +
                "rectangle \"==Element\" <<1>>\n" +
                "\n" +
                "\n" +
                "@enduml", diagram.getLegend().getDefinition());
    }

}