# unigen

Developer tool supports two features of unitied pipeline:

1. Generate code for *uni* modules.
1. Developer server for live development (live repositories).

# uni modules

Uni modules contains definition for:

* Cloud API.
* Storage (database and file system folders).

The unified dev server uses this definitions to produce platform specific source code 
with type definitions and OOP wrappers for API call.

## Cloud API

Cloud API section define types, used in cloud API and *jsonrpc* services.

You must declare cloud API features in uni files under **cloud** element.

### Types

API type definition include data structures, used in API and JSON serialization methods.

There is no requirements to define types, used in API, because code generator can construct this types 
 directly from service definitions.

### Services


