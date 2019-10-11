/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 07/apr/2014
 * Copyright 2017 by Sober Lemur S.a.s. di Vacondio Andrea (info@pdfsam.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.redact;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.builder.Builder;
import org.pdfsam.context.UserContext;
import org.pdfsam.i18n.DefaultI18nContext;
import org.pdfsam.module.ModuleCategory;
import org.pdfsam.module.ModuleDescriptor;
import org.pdfsam.module.ModuleInputOutputType;
import org.pdfsam.module.ModulePriority;
import org.pdfsam.support.params.SinglePdfSourceMultipleOutputParametersBuilder;
import org.pdfsam.ui.commons.ClearModuleEvent;
import org.pdfsam.ui.io.BrowsableOutputDirectoryField;
import org.pdfsam.ui.io.PdfDestinationPane;
import org.pdfsam.ui.module.BaseTaskExecutionModule;
import org.pdfsam.ui.module.Footer;
import org.pdfsam.ui.module.OpenButton;
import org.pdfsam.ui.module.RunButton;
import org.pdfsam.ui.prefix.PrefixPane;
import org.pdfsam.ui.selection.single.TaskParametersBuilderSingleSelectionPane;
import org.pdfsam.ui.support.Views;
import org.sejda.eventstudio.annotation.EventListener;
import org.sejda.eventstudio.annotation.EventStation;
import org.sejda.injector.Auto;
import org.sejda.injector.Components;
import org.sejda.injector.Provides;
import org.sejda.model.parameter.AbstractSplitByPageParameters;
import org.sejda.model.prefix.Prefix;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.pdfsam.module.ModuleDescriptorBuilder.builder;
import static org.pdfsam.ui.io.PdfDestinationPane.DestinationPanelFields.DISCARD_BOOKMARKS;

@Auto
public class RedactModule extends BaseTaskExecutionModule {

    private static final String MODULE_ID = "redact.simple";

    private TaskParametersBuilderSingleSelectionPane selectionPane;
    private BrowsableOutputDirectoryField destinationDirectoryField;
    private PdfDestinationPane destinationPane;
    private RedactOptionsPane redactOptions = new RedactOptionsPane();
    private PrefixPane prefix = new PrefixPane();
    private ModuleDescriptor descriptor = builder().category(ModuleCategory.REDACT)
            .inputTypes(ModuleInputOutputType.SINGLE_PDF).name(DefaultI18nContext.getInstance().i18n("Redact"))
            .description(DefaultI18nContext.getInstance().i18n("Redacts selected text in a PDF document."))
            .priority(ModulePriority.HIGH.getPriority()).build();

    @Inject
    public RedactModule(@Named(MODULE_ID + "field") BrowsableOutputDirectoryField destinationDirectoryField,
                        @Named(MODULE_ID + "pane") PdfDestinationPane destinationPane, @Named(MODULE_ID + "footer") Footer footer) {
        super(footer);
        this.destinationDirectoryField = destinationDirectoryField;
        this.destinationPane = destinationPane;
        this.selectionPane = new TaskParametersBuilderSingleSelectionPane(id());
        this.selectionPane.setPromptText(
                DefaultI18nContext.getInstance().i18n("Select or drag and drop the PDF you want to redact"));
        this.selectionPane.addOnLoaded(d -> redactOptions.setMaxPages(d.pages().getValue()));
        initModuleSettingsPanel(settingPanel());
    }

    @Override
    public ModuleDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public void onSaveWorkspace(Map<String, String> data) {
        selectionPane.saveStateTo(data);
        redactOptions.saveStateTo(data);
        destinationDirectoryField.saveStateTo(data);
        destinationPane.saveStateTo(data);
        prefix.saveStateTo(data);
    }

    @Override
    public void onLoadWorkspace(Map<String, String> data) {
        selectionPane.restoreStateFrom(data);
        redactOptions.restoreStateFrom(data);
        destinationDirectoryField.restoreStateFrom(data);
        destinationPane.restoreStateFrom(data);
        prefix.restoreStateFrom(data);
    }

    @Override
    protected Builder<? extends AbstractSplitByPageParameters> getBuilder(Consumer<String> onError) {
        Optional<SinglePdfSourceMultipleOutputParametersBuilder<? extends AbstractSplitByPageParameters>> builder = Optional
                .ofNullable(redactOptions.getBuilder(onError));
        builder.ifPresent(b -> {
            selectionPane.apply(b, onError);
            destinationDirectoryField.apply(b, onError);
            destinationPane.apply(b, onError);
            prefix.apply(b, onError);
        });
        return builder.orElse(null);
    }

    private VBox settingPanel() {
        VBox pane = new VBox();
        pane.setAlignment(Pos.TOP_CENTER);

        TitledPane prefixTitled = Views.titledPane(DefaultI18nContext.getInstance().i18n("File names settings"),
                prefix);
        prefix.addMenuItemFor(Prefix.CURRENTPAGE);
        prefix.addMenuItemFor(Prefix.FILENUMBER);
        prefix.addMenuItemFor("[TOTAL_FILESNUMBER]");

        pane.getChildren().addAll(selectionPane,
                Views.titledPane(DefaultI18nContext.getInstance().i18n("Split settings"), redactOptions),
                Views.titledPane(DefaultI18nContext.getInstance().i18n("Output settings"), destinationPane),
                prefixTitled);
        return pane;
    }

    @EventListener
    public void onClearModule(ClearModuleEvent e) {
        if (e.clearEverything) {
            redactOptions.resetView();
            prefix.resetView();
            destinationPane.resetView();
        }
    }

    @Override
    @EventStation
    public String id() {
        return MODULE_ID;
    }

    @Override
    public Node graphic() {
        return new ImageView("redact.png");
    }

    @Components({ RedactModule.class })
    public static class ModuleConfig {
        @Provides
        @Named(MODULE_ID + "field")
        public BrowsableOutputDirectoryField destinationDirectoryField() {
            return new BrowsableOutputDirectoryField();
        }

        @Provides
        @Named(MODULE_ID + "pane")
        public PdfDestinationPane destinationPane(@Named(MODULE_ID + "field") BrowsableOutputDirectoryField outputField,
                UserContext userContext) {
            PdfDestinationPane panel = new PdfDestinationPane(outputField, MODULE_ID, userContext, DISCARD_BOOKMARKS);
            panel.enableSameAsSourceItem();
            return panel;
        }

        @Provides
        @Named(MODULE_ID + "footer")
        public Footer footer(RunButton runButton, @Named(MODULE_ID + "openButton") OpenButton openButton) {
            return new Footer(runButton, openButton, MODULE_ID);
        }

        @Provides
        @Named(MODULE_ID + "openButton")
        public OpenButton openButton() {
            return new OpenButton(MODULE_ID, ModuleInputOutputType.MULTIPLE_PDF);
        }
    }
}
