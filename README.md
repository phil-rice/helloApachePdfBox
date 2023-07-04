# This is a simple library for slightly modifying a PDF

It is intended to be used for the use case where we
* Read a pdf to use as a template
* Add text to it
* Add images to it
* Do something with the result

It is a simple DSL / Builder around Apache PDFBox.

Usually this template is filled in from a generic <Data>


## Installation
By maven:
```xml
<dependency>
  <groupId>one.xingyi.pdftemplate</groupId>
  <artifactId>pdftemplate</artifactId>
  <version>1.2.0</version>
</dependency>
```

## Example usage:

```java

ResourceBundle bundle = ResourceBundle.getBundle("graphconfig");
        List<IPdfPart<List<Map<String, Object>>>> parts = PdfBuilder.<List<Map<String, Object>>>builder(bundle)
        .addParts(new GraphDefn<List<Map<String, Object>>>(0, 0, layout,
        GraphContents.fromListOfMap("one", "value")))

        .addParts(new GraphDefn<List<Map<String, Object>>>(280, 0, layout,
        GraphContents.fromListOfMap("one", "value2")))

        .addParts(new GraphDefn<List<Map<String, Object>>>(0, 400, layout,
        GraphContents.fromListOfMap("one", "value")))
        .addParts(new GraphDefn<List<Map<String, Object>>>(280, 400, layout,
        GraphContents.fromListOfMap("one", "value2")))
        .build();
    
    //And then to use it...
    //load the template, apply the data, and save it to a new file        
    var processor = processTemplate("/test.pdf", parts);
    //then for each data
    processor.accept(Example.data, doc -> doc.save("output.pdf"));
            
   //Or if you want to return the bytes
    var makeBytes = processTemplateAndReturn("/test.pdf", parts, doc -> {
        try (var stream = new ByteArrayOutputStream()) {
            doc.save(stream);
            return stream.toByteArray();
        }
    });
    //Then for each data
    byte[] bytes = makeBytes.apply(Example.data); 
            

```
This makes four changes to the template. The template is loaded from the class path
and then saved to a new file: `output.pdf`

* The units are in 'whatever the pds's units are'. Normally it will be in points aka 1/72th of an inch.
* The coordinate (0,0) is the bottom left hand corner of the page
* The builder allows us to add text, images, and buffered images
* Tt supports the usage of different fonts
* The items can be added to different pages
