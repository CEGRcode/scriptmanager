---
id: publications
title: Publications
sidebar_label: Publications
---
import { PesterDataTable } from "@site/src/components/PesterDataTable";
import { columns, publications, moduleTests } from "./useInPublications.table";

:::tip
If you're looking for how to cite ScriptManager in your work, check out our [Citing Us][citing-us] section on the Getting Started page.
:::

### Publications that use ScriptManager
Browse our list of publications that used ScriptManager in their work! You might find some interesting ideas and examples for building out your analysis.

<PesterDataTable
  columns={columns}
  data={ publications }
/>

[citing-us]:/docs/#citing-us
