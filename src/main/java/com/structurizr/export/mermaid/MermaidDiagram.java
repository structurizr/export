package com.structurizr.export.mermaid;

import com.structurizr.export.Diagram;
import com.structurizr.view.View;

public class MermaidDiagram extends Diagram {

    public MermaidDiagram(View view, String definition) {
        super(view, definition);
    }

    @Override
    public String getFileExtension() {
        return "mmd";
    }

}
