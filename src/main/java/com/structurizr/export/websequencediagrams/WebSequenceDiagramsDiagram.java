package com.structurizr.export.websequencediagrams;

import com.structurizr.export.Diagram;
import com.structurizr.view.View;

public class WebSequenceDiagramsDiagram extends Diagram {

    public WebSequenceDiagramsDiagram(View view, String definition) {
        super(view, definition);
    }

    @Override
    public String getFileExtension() {
        return "wsd";
    }

}