# This is a simple library for slightly modifying a PDF

It is intended to be used for the use case where we
* Read a pdf to use as a template
* Add text to it
* Add images to it
* Do something with the result

It is a simple DSL / Builder around Apache PDFBox.

## Installation
By maven:
```xml
<dependency>
  <groupId>one.xingyi.pdftemplate</groupId>
  <artifactId>pdftemplate</artifactId>
  <version>1.1.1</version>
</dependency>
```

## Example usage:

```java
    var parts = PdfBuilder.builder()
            .addText(400, 200, "Hello World as text")
            .addText(400, 300, "Second text")
            .addBufferedImage(100, 200, makeImage())
            .addImage(100, 400, doc -> PDImageXObject.createFromFileByContent(new File(ClassLoader.getSystemResource("picture.jpg").toURI()), doc))
            .build();

    IPdfPrinter.updateTemplate("test.pdf", parts, doc -> doc.save("HelloWorld.pdf"));
```
This makes four changes to the template. The template is loaded from the class path
and then saved to a new file: `HelloWorld.pdf`

* The units are in 'whatever the pds's units are'. Normally it will be in points aka 1/72th of an inch.
* The coordinate (0,0) is the bottom left hand corner of the page
* The builder allows us to add text, images, and buffered images
* Tt supports the usage of different fonts
* The items can be added to different pages
