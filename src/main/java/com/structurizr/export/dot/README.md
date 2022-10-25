# DOT (Graphviz)

The [DOTExporter](DOTExporter.java) class provides a way to export views to
diagram definitions that are compatible with [Graphviz](https://graphviz.org).

## Example usage

You can either export all views in a workspace:

```
Workspace workspace = ...
DOTExporter exporter = new DOTExporter();
Collection<Diagram> diagrams = exporter.export(workspace);
```

Or just a single view:

```
Workspace workspace = ...
SystemLandscape view = ...
DOTExporter exporter = new DOTExporter();
Diagram diagram = exporter.export(view);
```