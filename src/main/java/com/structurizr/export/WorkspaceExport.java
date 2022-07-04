package com.structurizr.export;

public abstract class WorkspaceExport {

    private String definition;

    public WorkspaceExport(String definition) {
        if (definition.length() > 0) {
            this.definition = definition.substring(0, definition.length() - 1);
        } else {
            this.definition = definition;
        }
    }

    public String getDefinition() {
        return definition;
    }

    public abstract String getFileExtension();

}