# Structurizr exporters

[![License](https://img.shields.io/badge/License-Apache_2.0-green.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/com.structurizr/structurizr-export.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.structurizr/structurizr-export)


This library provides the ability to export the model and views defined in a Structurizr workspace to a number of formats, including:

- [PlantUML and C4-PlantUML](https://github.com/structurizr/export/tree/main/src/main/java/com/structurizr/export/plantuml)
- [DOT](https://github.com/structurizr/export/tree/main/src/main/java/com/structurizr/export/dot)
- [Mermaid](https://github.com/structurizr/export/tree/main/src/main/java/com/structurizr/export/mermaid)
- [WebSequenceDiagrams](https://github.com/structurizr/export/tree/main/src/main/java/com/structurizr/export/websequencediagrams)
- [Ilograph](https://github.com/structurizr/export/tree/main/src/main/java/com/structurizr/export/ilograph)

This library is included in the [Structurizr CLI](https://github.com/structurizr/cli)
via the [export command](https://github.com/structurizr/cli/blob/master/docs/export.md).
It is also available on [Maven Central](https://mvnrepository.com/artifact/com.structurizr/structurizr-export), for inclusion in your own Java applications:

- groupId: `com.structurizr`
- artifactId: `structurizr-export`

## Diagram notation

As far as practical, the exporters implemented in this repo try to replicate the way that the
[Structurizr cloud service/on-premises installation/Lite](https://structurizr.com) renders diagrams.
See [Structurizr - Notation](https://structurizr.com/help/notation) for more details but, in summary,
every element/relationship in the model can have one or more text-based tags associated with it, and you can
create element/relationship styles associated with those tags to customise how the items are rendered.
This concept is similar to how to might add one or more CSS classes to a HTML element, to customise how it's rendered
in the browser.
The [DSL cookbook - Element styles](https://github.com/structurizr/dsl/tree/master/docs/cookbook/element-styles) and
[DSL cookbook - Relationship styles](https://github.com/structurizr/dsl/tree/master/docs/cookbook/relationship-styles)
show some examples of how this works if you're using the Structurizr DSL.

It's important to note that some features provided by the
[Structurizr cloud service/on-premises installation/Lite](https://structurizr.com) are not supported by these exporters.
For example, the PlantUML exporter doesn't support the same [set of element shapes](https://structurizr.com/help/shapes),
because PlantUML itself doesn't support many of them. The DSL demo page at https://structurizr.com/dsl provides a way to
try out the exporters implemented by this repo, so you can compare and contrast the various features they provide.
In general though, the
[Structurizr cloud service/on-premises installation/Lite](https://structurizr.com) will provide the most feature
complete and richest set of diagrams.

The following table summarises the features that are available via the exporters implemented in this library.

| Feature                                                                                         | Structurizr Lite/cloud/on-premises | StructurizrPlantUMLExporter          | C4PlantUMLExporter                    | MermaidExporter | DOTExporter |
|-------------------------------------------------------------------------------------------------|------------------------------------|--------------------------------------|---------------------------------------|-----------------|-------------|
| [Element styles](https://structurizr.com/help/notation#elements)                                | All                                | Limited                              | Limited (with `c4plantuml.tags true`) | Limited         | Limited     |
| [Relationship styles](https://structurizr.com/help/notation#relationships)                      | All                                | Limited                              | Limited (with `c4plantuml.tags true`) | Limited         | Limited     |
| [Shapes](https://structurizr.com/help/shapes)                                                   | All                                | Limited                              | Limited                               | Limited         | Limited     |
| [Icons](https://structurizr.com/help/icons)                                                     | Yes                                | Limited                              | Limited (with `c4plantuml.tags true`) | No              | No          |
| [Automatic diagram key/legend](https://structurizr.com/help/diagram-key)                        | Yes                                | Yes                                  | Yes                                   | No              | No          |
| [Click to zoom](https://structurizr.com/help/diagram-navigation)                                | Yes                                | No                                   | No                                    | No              | No          |
| Interactive features (tooltips, [perspectives](https://structurizr.com/help/perspectives), etc) | Yes                                | No                                   | No                                    | No              | No          |
| [Animation](https://structurizr.com/help/animation)                                             | Yes                                | Yes (with `plantuml.animation true`) | Yes (with `plantuml.animation true`)  | No              | No          |
| Automatic layout                                                                                | Yes                                | Yes                                  | Yes                                   | Yes             | Yes         |
| Manual layout                                                                                   | Yes                                | No                                   | No                                    | No              | No          |

## Custom exporters

There are two interfaces defined in this library, that be used as a starting point for building your own custom exporters:

- [DiagramExporter](https://github.com/structurizr/export/blob/main/src/main/java/com/structurizr/export/DiagramExporter.java): for exporting a collection of diagram definitions from a workspace (the PlantUML, Mermaid, etc exporters implement this interface), and [AbstractDiagramExporter](https://github.com/structurizr/export/blob/main/src/main/java/com/structurizr/export/AbstractDiagramExporter.java) is provided as a convenient starting point.
- [WorkspaceExporter](https://github.com/structurizr/export/blob/main/src/main/java/com/structurizr/export/WorkspaceExporter.java): for exporting a single definition from a workspace (the Ilograph exporter implements this interface).

## Links

- [Structurizr DSL demo page](https://structurizr.com/dsl) (demo of export formats)
- [D2 exporter](https://github.com/goto1134/structurizr-d2-exporter) (this is included in the Structurizr CLI)
- [structurizr2csv](https://gitlab.com/souliane/structurizr2csv/) (export Structurizr views to diagrams.net CSV format)
- [Changelog](docs/changelog.md)