package com.structurizr.export.ilograph;

import com.structurizr.Workspace;
import com.structurizr.export.AbstractExporterTests;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ThemeUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class IlographWriterTests extends AbstractExporterTests {

    @Test
    public void test_BigBankPlcExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-36141-workspace.json"));
        IlographExporter ilographExporter = new IlographExporter();
        String definition = ilographExporter.export(workspace);

        String expected = readFile(new File("./src/test/java/com/structurizr/export/ilograph/36141.ilograph"));
        assertEquals(expected, definition);
    }

    @Test
    public void test_AmazonWebServicesExample() throws Exception {
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(new File("./src/test/structurizr-54915-workspace.json"));
        ThemeUtils.loadThemes(workspace);
        IlographExporter ilographExporter = new IlographExporter();
        String definition = ilographExporter.export(workspace);

        String expected = readFile(new File("./src/test/java/com/structurizr/export/ilograph/54915.ilograph"));
        assertEquals(expected, definition);
    }

}