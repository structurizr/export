package com.structurizr.export;

import com.structurizr.Workspace;

public abstract class WorkspaceExporter extends Exporter {

    /**
     * Exports the entire workspace to a single String.
     *
     * @param workspace     the workspace to be exported
     * @return  a String export of the workspace
     * @throws Exception    if something goes wrong
     */
    public abstract String export(Workspace workspace) throws Exception;

    public abstract String getFileExtension();

}