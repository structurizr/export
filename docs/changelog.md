# Changelog

## 1.8.2 (5th January 2023)

- Fixes #37 (Mermaid diagrams have a yellow border).

## 1.8.1 (23rd December 2022)

- Updated dependencies.

## 1.8.0 (21st December 2022)

- The C4-PlantUML export now mimics how the Structurizr renderer uses tags when a view set or view property named `c4plantuml.tags` is set to `true`.
- Fixes #24 (plantuml.sequenceDiagrams changes the export of static diagrams).
- Adds the ability to configure the PlantUML exports via properties on the view set or view (#22).
- Renamed `plantuml.legend` to `c4plantuml.legend`.
- Renamed `plantuml.sequenceDiagrams` to `plantuml.sequenceDiagram`.
- Adds a view/view set property named `c4plantuml.stereotypes` that can be used to enable/disable stereotypes (these are always on by default when the legend is not shown; #29).
- Adds a `c4plantuml.stdlib` view/view set property to allow users to choose which version of C4-PlantUML should be used (built-in standard library, or GitHub).
- Fixes an issue with relationship properties not showing when `c4plantuml.relationshipProperties` is set to `true`.
- Fixes #35 (Dark mode interfaces not well supported by Mermaid exporter).
- Adds a `mermaid.title` property that can be used to enable/disable diagram titles.
- Renamed `mermaid.sequenceDiagrams` to `mermaid.sequenceDiagram`.

## 1.7.0 (3rd October 2022)

- Adds the ability to export a diagram legend when using the `StructurizrPlantUMLExporter`.
- Adds support for icons to the  `StructurizrPlantUMLExporter` (HTTP/HTTPS icon URLs only).

## 1.6.1 (9th September 2022)

- Fixes #15 (PlantUML export fails when element names are Unicode characters)
- Fixes an issue with the last character of workspace exports being stripped. 

## 1.6.0 (15th August 2022)

- `com.structurizr.export.DiagramExporter` and `com.structurizr.export.WorkspaceExporter` can now be implemented to build custom exporters, for use with the Structurizr CLI `export` command.
- Resolves #2 (Allow to export properties to C4Plantuml files).
- Resolves #8 (fixes the rendering of infrastructure nodes in C4-PlantUML deployment diagrams).
- Resolves #17 (tags are no longer included by default in the C4-PlantUML export, and can be configured via a view set property named `c4plantuml.tags`).

## 1.5.0 (30th March 2022)

- Adds support for relationship colours in the Ilograph export.
- Adds support for the new relationship line style property.
- Adds support for Mermaid sequence diagrams (#6).
- Adds support for custom views and elements.

## 1.4.0 (20th February 2022)

- Package change from `com.structurizr.io` to `com.structurizr.export`.
- Fixes #4 (Remove sequence numeration from messages in PlantUML Sequence diagrams for dynamic views).

## 1.3.0 (29th December 2021)

- Fixes a bug when exporting views to PlantUML formats, when there are newline characters in element names/descriptions/technologies and relationship descriptions/technologies.
- The C4-PlantUML export now includes tags.
- Adds support for customizing PlantUML exports via view set properties (plantuml.title, plantuml.includes, etc).

## v1.2.1 (29th November 2021)

- Adds support for hyperlinked elements via the StructurizrPlantUML and C4-PlantUML exporters.
- Adds support for styling groups via an element style named `Group' (for all groups) or `Group:Name` (for the "Name" group).

### v1.2.0 (9th September 2021)

- Adds support for C4-PlantUML `SHOW_LEGEND()`.
- Identifiers in PlantUML exports are now based upon element names, rather than internal IDs (#59).

### v1.1.1 (9th June 2021)

- Adds support for "left to right direction" layouts with C4-PlantUML.

### v1.1.0 (7th June 2021)

- Adds support for "external" software system/container boundaries on dynamic views.
- Adds support for more shapes (pipe and hexagon) via the StructurizrPlantUMLExporter.
- Adds support for exporting animations (StructurizrPlantUML and C4-PlantUML only).

### v1.0.1 (29th April 2021)

- Trying to render a sequence diagram with C4-PlantUML now throws an unsupported exception, as C4-PlantUML doesn't natively support sequence diagrams.

### v1.0.0 (27th April 2021)

- Initial version, refactored from existing (and separate) PlantUML, Mermaid, DOT, WebSequenceDiagrams, and Ilograph exporters.