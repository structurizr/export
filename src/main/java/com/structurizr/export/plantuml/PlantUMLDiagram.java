package com.structurizr.export.plantuml;

import com.structurizr.export.Diagram;
import com.structurizr.view.View;

public class PlantUMLDiagram extends Diagram {

    public PlantUMLDiagram(View view, String definition) {
        super(view, definition);
    }

    @Override
    public String getFileExtension() {
        return "puml";
    }

}