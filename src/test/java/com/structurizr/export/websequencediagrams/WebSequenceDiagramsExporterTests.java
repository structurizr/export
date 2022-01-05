package com.structurizr.export.websequencediagrams;

import com.structurizr.Workspace;
import com.structurizr.export.AbstractExporterTests;
import com.structurizr.export.Diagram;
import com.structurizr.util.WorkspaceUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class WebSequenceDiagramsExporterTests extends AbstractExporterTests {

    @Test
    public void test_BigBankPlcExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-36141-workspace.json"));
        WebSequenceDiagramsExporter exporter = new WebSequenceDiagramsExporter();

        Collection<Diagram> diagrams = exporter.export(workspace);
        assertEquals(1, diagrams.size());

        Diagram diagram = diagrams.stream().filter(d -> d.getKey().equals("SignIn")).findFirst().get();
        String expected = readFile(new File("./src/test/java/com/structurizr/export/websequencediagrams/36141-SignIn.wsd"));
        assertEquals(expected, diagram.getDefinition());
    }

}