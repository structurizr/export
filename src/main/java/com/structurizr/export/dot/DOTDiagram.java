package com.structurizr.export.dot;

import com.structurizr.export.Diagram;
import com.structurizr.view.View;

public class DOTDiagram extends Diagram {

    public DOTDiagram(View view, String definition) {
        super(view, definition);
    }

    @Override
    public String getFileExtension() {
        return "dot";
    }

}