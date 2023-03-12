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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class C4PlantUMLDiagramExporterTests extends AbstractExporterTests {

    @Test
    public void test_BigBankPlcExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-36141-workspace.json"));
        workspace.getViews().getConfiguration().addProperty(C4PlantUMLExporter.C4PLANTUML_TAGS_PROPERTY, "true");

        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(7, diagrams.size());

        Diagram diagram = diagrams.stream().filter(d -> d.getKey().equals("SystemLandscape")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/36141-SystemLandscape.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("SystemContext")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/36141-SystemContext.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("Containers")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/36141-Containers.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("Components")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/36141-Components.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(d -> d.getKey().equals("SignIn")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/36141-SignIn.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("DevelopmentDeployment")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/36141-DevelopmentDeployment.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("LiveDeployment")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/36141-LiveDeployment.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_AmazonWebServicesExampleWithoutTags() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-54915-workspace.json"));
        ThemeUtils.loadThemes(workspace);
        workspace.getViews().getDeploymentViews().iterator().next().enableAutomaticLayout(AutomaticLayout.RankDirection.LeftRight, 300, 300);
        workspace.getViews().getViews().forEach(v -> v.addProperty(C4PlantUMLExporter.C4PLANTUML_TAGS_PROPERTY, "false"));

        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(1, diagrams.size());

        Diagram diagram = diagrams.stream().findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/54915-AmazonWebServicesDeployment-WithoutTags.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_AmazonWebServicesExampleWithTags() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-54915-workspace.json"));
        ThemeUtils.loadThemes(workspace);
        workspace.getViews().getDeploymentViews().iterator().next().enableAutomaticLayout(AutomaticLayout.RankDirection.LeftRight, 300, 300);
        workspace.getViews().getConfiguration().addProperty(C4PlantUMLExporter.C4PLANTUML_TAGS_PROPERTY, "true");

        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(1, diagrams.size());

        Diagram diagram = diagrams.stream().findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/54915-AmazonWebServicesDeployment-WithTags.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_GroupsExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/groups.json"));
        ThemeUtils.loadThemes(workspace);

        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(3, diagrams.size());

        Diagram diagram = diagrams.stream().filter(md -> md.getKey().equals("SystemLandscape")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/groups-SystemLandscape.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("Containers")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/groups-Containers.puml"));
        assertEquals(expected, diagram.getDefinition());

        diagram = diagrams.stream().filter(md -> md.getKey().equals("Components")).findFirst().get();
        expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/groups-Components.puml"));
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

        C4PlantUMLExporter exporter = new C4PlantUMLExporter();
        Collection<Diagram> diagrams = exporter.export(workspace);

        Diagram diagram = diagrams.stream().filter(md -> md.getKey().equals("SystemLandscape")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/nested-groups.puml"));
        assertEquals(expected, diagram.getDefinition());
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

        containerView.setExternalSoftwareSystemBoundariesVisible(true);
        Diagram diagram = new C4PlantUMLExporter().export(containerView);
        assertEquals("@startuml\nset separator none\n" +
                "title Software System 1 - Containers\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml\n" +
                "\n" +
                "System_Boundary(\"SoftwareSystem1_boundary\", \"Software System 1\", $tags=\"\") {\n" +
                "  Container(SoftwareSystem1.Container1, \"Container 1\", \"\", $tags=\"\")\n" +
                "}\n" +
                "\n" +
                "System_Boundary(\"SoftwareSystem2_boundary\", \"Software System 2\", $tags=\"\") {\n" +
                "  Container(SoftwareSystem2.Container2, \"Container 2\", \"\", $tags=\"\")\n" +
                "}\n" +
                "\n" +
                "Rel_D(SoftwareSystem1.Container1, SoftwareSystem2.Container2, \"Uses\", $tags=\"\")\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());


        containerView.setExternalSoftwareSystemBoundariesVisible(false);
        diagram = new C4PlantUMLExporter().export(containerView);
        assertEquals("@startuml\nset separator none\n" +
                "title Software System 1 - Containers\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml\n" +
                "\n" +
                "System_Boundary(\"SoftwareSystem1_boundary\", \"Software System 1\", $tags=\"\") {\n" +
                "  Container(SoftwareSystem1.Container1, \"Container 1\", \"\", $tags=\"\")\n" +
                "}\n" +
                "\n" +
                "Container(SoftwareSystem2.Container2, \"Container 2\", \"\", $tags=\"\")\n" +
                "\n" +
                "Rel_D(SoftwareSystem1.Container1, SoftwareSystem2.Container2, \"Uses\", $tags=\"\")\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderComponentDiagramWithExternalComponents() {
        Workspace workspace = new Workspace("Name", "Description");

        SoftwareSystem softwareSystem1 = workspace.getModel().addSoftwareSystem("Software System 1");
        Container container1 = softwareSystem1.addContainer("Container 1");
        Component component1 = container1.addComponent("Component 1");
        SoftwareSystem softwareSystem2 = workspace.getModel().addSoftwareSystem("Software System 2");
        Container container2 = softwareSystem2.addContainer("Container 2");
        Component component2 = container2.addComponent("Component 2");

        component1.uses(component2, "Uses");

        ComponentView componentView = workspace.getViews().createComponentView(container1, "Components", "");
        componentView.add(component1);
        componentView.add(component2);

        componentView.setExternalSoftwareSystemBoundariesVisible(true);
        Diagram diagram = new C4PlantUMLExporter().export(componentView);
        assertEquals("@startuml\nset separator none\n" +
                "title Software System 1 - Container 1 - Components\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml\n" +
                "\n" +
                "Container_Boundary(\"SoftwareSystem1.Container1_boundary\", \"Container 1\", $tags=\"\") {\n" +
                "  Component(SoftwareSystem1.Container1.Component1, \"Component 1\", \"\", $tags=\"\")\n" +
                "}\n" +
                "\n" +
                "Container_Boundary(\"SoftwareSystem2.Container2_boundary\", \"Container 2\", $tags=\"\") {\n" +
                "  Component(SoftwareSystem2.Container2.Component2, \"Component 2\", \"\", $tags=\"\")\n" +
                "}\n" +
                "\n" +
                "Rel_D(SoftwareSystem1.Container1.Component1, SoftwareSystem2.Container2.Component2, \"Uses\", $tags=\"\")\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());

        componentView.setExternalSoftwareSystemBoundariesVisible(false);
        diagram = new C4PlantUMLExporter().export(componentView);
        assertEquals("@startuml\nset separator none\n" +
                "title Software System 1 - Container 1 - Components\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml\n" +
                "\n" +
                "Container_Boundary(\"SoftwareSystem1.Container1_boundary\", \"Container 1\", $tags=\"\") {\n" +
                "  Component(SoftwareSystem1.Container1.Component1, \"Component 1\", \"\", $tags=\"\")\n" +
                "}\n" +
                "\n" +
                "Component(SoftwareSystem2.Container2.Component2, \"Component 2\", \"\", $tags=\"\")\n" +
                "\n" +
                "Rel_D(SoftwareSystem1.Container1.Component1, SoftwareSystem2.Container2.Component2, \"Uses\", $tags=\"\")\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderDiagramWithElementUrls() {
        Workspace workspace = new Workspace("Name", "Description");

        SoftwareSystem softwareSystem = workspace.getModel().addSoftwareSystem("Software System");
        softwareSystem.setUrl("https://structurizr.com");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addDefaultElements();

        Diagram diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "\n" +
                "System(SoftwareSystem, \"Software System\", \"\", $tags=\"\")[[https://structurizr.com]]\n" +
                "\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderDiagramWithIncludes() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addSoftwareSystem("Software System");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addDefaultElements();

        view.getViewSet().getConfiguration().addProperty(C4PlantUMLExporter.PLANTUML_INCLUDES_PROPERTY, "styles.puml");

        Diagram diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "!include styles.puml\n" +
                "\n" +
                "System(SoftwareSystem, \"Software System\", \"\", $tags=\"\")\n" +
                "\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderDiagramWithNewLineCharacterInElementName() {
        Workspace workspace = new Workspace("Name", "Description");

        workspace.getModel().addSoftwareSystem("Software\nSystem");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addDefaultElements();

        Diagram diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "\n" +
                "System(SoftwareSystem, \"Software\\nSystem\", \"\", $tags=\"\")\n" +
                "\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_renderInfrastructureNodeWithTechnology() {
        Workspace workspace = new Workspace("Name", "Description");
        DeploymentNode deploymentNode = workspace.getModel().addDeploymentNode("Deployment node");
        deploymentNode.addInfrastructureNode("Infrastructure node", "description", "technology");

        DeploymentView view = workspace.getViews().createDeploymentView("key", "view description");
        view.addDefaultElements();

        Diagram diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title Deployment - Default\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Deployment.puml\n" +
                "\n" +
                "Deployment_Node(Default.Deploymentnode, \"Deployment node\", $tags=\"\") {\n" +
                "  Deployment_Node(Default.Deploymentnode.Infrastructurenode, \"Infrastructure node\", \"technology\", \"description\", $tags=\"\")\n" +
                "}\n" +
                "\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void test_printProperties() throws Exception {
        Workspace workspace = new Workspace("Name", "Description");
        SoftwareSystem softwareSystem = workspace.getModel().addSoftwareSystem("SoftwareSystem");
        Container container1 = softwareSystem.addContainer("Container 1");
        container1.addProperty("structurizr.dsl.identifier", "container1");
        container1.addProperty("IP", "127.0.0.1");
        container1.addProperty("Region", "East");
        Container container2 = softwareSystem.addContainer("Container 2");
        container1.addProperty("structurizr.dsl.identifier", "container2");
        container2.addProperty("Region", "West");
        container2.addProperty("IP", "127.0.0.2");
        Relationship relationship = container1.uses(container2, "");
        relationship.addProperty("Prop1", "Value1");
        relationship.addProperty("Prop2", "Value2");

        workspace.getViews().getConfiguration().addProperty(C4PlantUMLExporter.C4PLANTUML_ELEMENT_PROPERTIES_PROPERTY, Boolean.TRUE.toString());
        workspace.getViews().getConfiguration().addProperty(C4PlantUMLExporter.C4PLANTUML_RELATIONSHIP_PROPERTIES_PROPERTY, Boolean.TRUE.toString());
        ContainerView view = workspace.getViews().createContainerView(softwareSystem, "containerView", "");
        view.addDefaultElements();

        Diagram diagram = new C4PlantUMLExporter().export(view);

        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/printProperties-containerView.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_deploymentViewPrintProperties() throws Exception {
        Workspace workspace = new Workspace("Name", "Description");

        DeploymentNode deploymentNode = workspace.getModel().addDeploymentNode("Deployment node");
        deploymentNode.addProperty("Prop1", "Value1");

        InfrastructureNode infraNode = deploymentNode.addInfrastructureNode("Infrastructure node", "description", "technology");
        infraNode.addProperty("Prop2", "Value2");

        workspace.getViews().getConfiguration().addProperty(C4PlantUMLExporter.C4PLANTUML_ELEMENT_PROPERTIES_PROPERTY, Boolean.TRUE.toString());
        DeploymentView deploymentView = workspace.getViews().createDeploymentView("deploymentView", "");
        deploymentView.addDefaultElements();

        Diagram diagram = new C4PlantUMLExporter().export(deploymentView);

        String expected = readFile(new File("./src/test/java/com/structurizr/export/plantuml/c4plantuml/printProperties-deploymentView.puml"));
        assertEquals(expected, diagram.getDefinition());
    }

    @Test
    public void test_legendAndStereotypes() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addSoftwareSystem("Name");

        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addDefaultElements();

        // legend (true) and stereotypes (false)
        view.addProperty(C4PlantUMLExporter.C4PLANTUML_LEGEND_PROPERTY, "true");
        view.addProperty(C4PlantUMLExporter.C4PLANTUML_STEREOTYPES_PROPERTY, "false");
        Diagram diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "\n" +
                "System(Name, \"Name\", \"\", $tags=\"\")\n" +
                "\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition());

        // legend (true) and stereotypes (true)
        view.addProperty(C4PlantUMLExporter.C4PLANTUML_LEGEND_PROPERTY, "true");
        view.addProperty(C4PlantUMLExporter.C4PLANTUML_STEREOTYPES_PROPERTY, "true");
        diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "\n" +
                "System(Name, \"Name\", \"\", $tags=\"\")\n" +
                "\n" +
                "\n" +
                "SHOW_LEGEND(false)\n" +
                "@enduml", diagram.getDefinition());

        // legend (false) and stereotypes (false)
        view.addProperty(C4PlantUMLExporter.C4PLANTUML_LEGEND_PROPERTY, "false");
        view.addProperty(C4PlantUMLExporter.C4PLANTUML_STEREOTYPES_PROPERTY, "false");
        diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "\n" +
                "System(Name, \"Name\", \"\", $tags=\"\")\n" +
                "\n" +
                "\n" +
                "hide stereotypes\n" +
                "@enduml", diagram.getDefinition());

        // legend (false) and stereotypes (true)
        view.addProperty(C4PlantUMLExporter.C4PLANTUML_LEGEND_PROPERTY, "false");
        view.addProperty(C4PlantUMLExporter.C4PLANTUML_STEREOTYPES_PROPERTY, "true");
        diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "\n" +
                "System(Name, \"Name\", \"\", $tags=\"\")\n" +
                "\n" +
                "\n" +
                "show stereotypes\n" +
                "@enduml", diagram.getDefinition());
    }

    @Test
    public void testFont() {
        Workspace workspace = new Workspace("Name", "Description");
        workspace.getModel().addPerson("User");
        SystemLandscapeView view = workspace.getViews().createSystemLandscapeView("key", "Description");
        view.addAllElements();
        workspace.getViews().getConfiguration().getBranding().setFont(new Font("Courier"));

        Diagram diagram = new C4PlantUMLExporter().export(view);
        assertEquals("@startuml\nset separator none\n" +
                "title System Landscape\n" +
                "\n" +
                "skinparam {\n" +
                "  defaultFontName \"Courier\"\n" +
                "}\n" +
                "top to bottom direction\n" +
                "\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4.puml\n" +
                "!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml\n" +
                "\n" +
                "Person(User, \"User\", \"\", $tags=\"\")\n" +
                "\n" +
                "\n" +
                "SHOW_LEGEND(true)\n" +
                "@enduml", diagram.getDefinition().toString());

    }

}
