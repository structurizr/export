# PlantUML

There are two PlantUML exporters in this package - [StructurizrPlantUMLExporter](StructurizrPlantUMLExporter.java) and [C4PlantUMLExporter](C4PlantUMLExporter.java).

## StructurizrPlantUMLExporter

This exporter generates PlantUML diagram definitions based upon how the Structurizr diagram renderer
creates diagrams using tags and styles. The following view types are supported:

- Custom
- System landscape
- System context
- Containers
- Components
- Dynamic (collaboration and sequence diagram)

There are a number of properties that can be set to customise the exports, as follows:

- `plantuml.title`: `true` (default) to include diagram titles, `false` to exclude diagram titles.
- `plantuml.includes`: a comma separated list of file names that should be included (via `!include`) in the diagram definition.
- `plantuml.sequenceDiagram`: `true` to generate a UML sequence diagram, `false` (default) to generate a collaboration diagram.
- `plantuml.animation`: `true` to generate one PlantUML diagram definition per animation frame, `false` (default) to ignore animations.

These properties can either be set on individual views, or on the view set to apply to all views.

## C4PlantUMLExporter

This exporter generates C4-PlantUML diagram definitions. The following view types are supported:

- System landscape
- System context
- Containers
- Components
- Dynamic (collaboration diagram only)

There are a number of properties that can be set to customise the exports, as follows:

- `plantuml.title`: `true` (default) to include diagram titles, `false` to exclude diagram titles.
- `plantuml.includes`: a comma separated list of file names that should be included (via `!include`) in the diagram definition.
- `plantuml.animation`: `true` to generate one PlantUML diagram definition per animation frame, `false` (default) to ignore animations.
- `c4plantuml.tags`: `true` to generate diagram definitions based upon how the Structurizr diagram renderer creates diagrams using tags and styles, `false` (default) to generate diagram definitions that use the default C4-PlantUML styling (i.e. blue and grey boxes).
- `c4plantuml.legend`: `true` (default) to include the diagram legend, `false` to exclude the legend.

These properties can either be set on individual views, or on the view set to apply to all views.