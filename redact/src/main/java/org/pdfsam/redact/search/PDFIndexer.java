package org.pdfsam.redact.search;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
public class PDFIndexer {
    public IndexItem index(File file) throws IOException {
        PDDocument doc = PDDocument.load(file);
        String content = new PDFTextStripper().getText(doc);
        doc.close();
        return new IndexItem((long)file.getName().hashCode(), file.getName(), content);
    }

}