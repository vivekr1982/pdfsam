package org.pdfsam.basic;

import org.pdfsam.PdfsamApp;
import org.sejda.injector.Injector;

import javafx.application.Application;

/**
 * PDFsam Basic Edition App
 *
 */
public class App {
    public static void main(String[] args) {
        Injector.addConfig(new PdfsamBasicConfig(), new org.pdfsam.extract.ExtractModule.ModuleConfig(),
                new org.pdfsam.merge.MergeModule.ModuleConfig(), new org.pdfsam.rotate.RotateModule.ModuleConfig(),
                new org.pdfsam.split.SplitModule.ModuleConfig(), new org.pdfsam.splitbysize.SplitBySizeModule.ModuleConfig(),
                new org.pdfsam.redact.RedactModule.ModuleConfig());

        Application.launch(PdfsamApp.class, args);
    }

}
