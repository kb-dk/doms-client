The purpose of this document is to give an overview of the various data layers involved in the Doms-Gui
architecture, and to identify and describe the components responsible for
making transformations between them.

# The Data Layers

There are four identifiable datalayers in play:

1. The Doms layer (e.g. objects, datastreams, rdf, templates, content models etc.)
1. The SDO layer (e.g. DataObject, Property, and Type instances from commonj.sdo and sub-packages)
1. The SDO-DOC layer (SDOParsedXmlDocument and SDOParsedXmlElement instances from dk.statsbiblioteket.doms.client.sdo)
1. The front-end layer (ie html/css/javascript)

Crucially, the transformation between the SDO-DOC layer and the front-end is carried out automagically by the SEAM
framework in the GUI itself. This means that we can test
the GUI as a whole as if we were manipulating actual data in a browser by building the SDO and SDO-DOC layers from XML files or Strings
, manipulating the data in the SDO-DOC layer, and then simulating the
submit process which would write the data back to Doms. The tests in the class SdoTest are structured like this.

## The SDO Layer

SDO is a very rich API for describing structured data and it is easy to blunder around in a fog when using it.
Fortunately the javadoc on the base interface/classes is usually quite
helpful. The three fundamental classes of objects in SDO (as we use it) are

1. DataObjects. These are rather like Nodes in a w3c DOM tree in that they can represent XML elements, attributes or text-data. DataObjects in SDO are typed. The allowed types
are generated dynamically from the XML schema document and the type of any given object can be found from its getType() method.
1. Types. A type can be identified by its name (from getName()) which is typically the name of the XML schema type it represents. Each type has associated with it a
list of Properties which identifies the allowed "children" of objects of this type.
1. Properties. A property in SDO is something like a relation in an ontology language such as rdf. Properties are named and typed so that SDO specifies relations like "Objects of
of Type A may have a property called B which refers to an object of Type C".

### Mixed and Sequenced Data

This is probably the part of the SDO API which has caused most confusion for us. SDO DataObjects possess an isSequenced() boolean method. Sequenced objects
in SDO can be used to represent choices in an XML Schema or XML mixed data - e.g. an ordered list of text and elements like an html body-element. SDO sequences
are quite distinct from sequences in XML Schema (see http://pic.dhe.ibm.com/infocenter/esbsoa/wesbv7r5m1/topic/com.ibm.websphere.wesb.programming.doc/topics/cbo_usingsequences.html
for details). A
sequenced DataObject has a Sequence which can be accessed with the getSequence() method. The Sequence can be iterated over. Those elements which represent
sub-elements will have both a Property and a Value, but those representing text in mixed data will only have a Value - their Property is null. The Value
itself may be either a DataObject or primitive, such as a String.

The getInstanceProperties() method of a DataObject will return all its properties, including those defined in its Sequence, but will not include references to
its text-content.

## The SDO-DOC Layer

The SDO-DOC layer is a tree-like structure which acts as the underlying data layer for the actual gui front-end and
mediates between it and the SDO layer. The SDO-DOC layer consists of a single SDOParsedXmlDocumentImpl object which
refers to a root element of type SDOParsedXmlElementImpl. Each SDOParsedXmlElementImpl has a list of child objects,
also of type SDOParsedXmlElementImpl. The SDO-DOC layer is therefore intrinsically much less rich than the SDO layer:
 all objects are of the same type and all relationships are of the "hasChild" type.

However the SDO-DOC layer still "knows about" the SDO layer as each element object has a reference to the SDO
DataObject it represents and also to the SDO Property which connects that object to its parent. In addition, each element has a
value (which may be null) representing explicit content of the corresponding XML element or attribute.

(There is clearly some potential for difficulties in transforming consistently between the SDO-DOC and SDO layers, and
this is where most of our difficulties occur. In particular, the element-tree model is not rich enough to represent
all the possible structure which could be present in the SDO layer. As an example, SDO mixed content could consist of
a list of alternating text-elements and xml sub-elements. But the SDO-DOC element, in its current form, assigns a
single scalar value to each element.)

### The "+" button

When a user presses the "+" button to create a new sibling element for an existing element, this activates the create()
 method in the class SDOParsedXmlElement. This method essentially clones the current element as a sibling of itself, but with
 empty content. In particular, the new element uses the same concrete types as the element from which it is cloned, so there
 is never a problem with trying to create an element of abstract type.

## Doms -> SDO and SDO-DOC

The parsing of the XML from DOMS into both SDO and SDO-DOC layers is a two-step process which is carried out in the constructor to SDOParsedXmlDocumentImpl.
First the XML Schema is parsed (using standard SDO API methods) to create a list of Types. This list is available as a field in the SDOParsedXmlDocumentImpl
instance.

The SDO and SDO-DOC trees are built in parallel as follows. First the actual XML datastream in parsed as an SDO tree using standard
SDO API methods. This tree, of course, only contains those elements which are actually present in the datastream XML. For the GUI we
need to create a tree containing the complete tree structure with empty placeholder elements for all the possible elements which
could be added. To do this, we proceed as follows:

1. Find the root element of the datastream XML and perform some sanity checks that it is an allowable root element for the schema.
2. Create the SDOParsedXmlElementImpl representing the root element of the document.
3. Iterate over every property defined for the type of this root element:

     3.1  If the current property is simple (ie represents a leaf or leaves in the tree) create the leaf elements, taking their value from
     the original datastream XML if set.

     3.2  If the current property is complex, recurse over all its defined properties in turn, adding new DataObject instances and
     SDO-DOC elements for any properties which are not set in the datastream.
4. For each SDOParsedXmlElementImpl, reorder its children so that those children corresponding to Sequence elements come in the same order
as in the elements DataObject.


## SDO-DOC -> SDO

When there is a changed focus on an element in the GUI (e.g. due to a mouse click), the SEAM front-end calls the submit() method on the
corresponding SDOParsedXmlElementImpl object in the SDO-DOC tree. This recursively traverses the element and
its children, updating the corresponding SDO tree. Specifically, if the element is a leaf-element then the value
of the element is written into the SDO tree by setting the corresponding Property on the correct DataObject. This
logic becomes somewhat convoluted if the DataObject is sequenced.

For non-leaf objects, the submit() method just recurses over child-elements.

## SDO -> Doms

The final transformation of the SDO tree to the Doms XML is carried out in the method SDOParsedXmlDocumentImpl.dumpToString().
In theory, this operation could be carried out using SDO API methods alone. However in practice this produces an
XML output which is different from the input because it includes all the empty placeholder elements we added when creating the SDO and SDO-DOC trees.
 The dumpToString() method
therefore includes an additional pre-processing step which removes all the empty elements which were absent in
the original parsed XML.

In order for this pruning of empty objects to be carried out correctly, elements in SDO-DOC have to be flagged when
they are created.
There are two flags: "originallySet" is true if the element or attribute was present in the original Doms XML and additionally
 "originallySetNonEmpty" if it was both present and set to a non-empty value. The submit() method, when it processes
 the SDO-DOC to SDO, has special logic for empty elements. Specifically, if the element was originally set then its
  value is now set to a temporary placeholder value depending on whether it was originally empty or originally non-empty.

The dumpToString() method (using utility methods in SdoDataObjectRemovalUtil) then prunes all elements which are still empty and also those
elements which were originally set but whose content is now empty. After this pruning process, the output is dumped to
XML using SDO API methods.