package org.pdfsam.redact;

import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.pdfsam.support.params.SinglePdfSourceMultipleOutputParametersBuilder;
import org.pdfsam.ui.ResettableView;
import org.pdfsam.ui.support.Style;
import org.pdfsam.ui.workspace.RestorableView;
import org.sejda.model.parameter.AbstractSplitByPageParameters;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Panel for the Redact options
 * 
 */
class RedactOptionsPane extends VBox implements RedactParametersBuilderCreator, RestorableView, ResettableView {

    private ToggleGroup group = new ToggleGroup();


    RedactOptionsPane() {
        super(Style.DEFAULT_SPACING);

        /*RadioButtonDrivenTextFieldsPane grid = new RadioButtonDrivenTextFieldsPane(group);

        getStyleClass().addAll(Style.CONTAINER.css());
        getChildren().addAll(grid);*/
    }
    void setMaxPages(Integer value) {

    }

    @Override
    public SinglePdfSourceMultipleOutputParametersBuilder<? extends AbstractSplitByPageParameters> getBuilder(
            Consumer<String> onError) {
        return ((RedactParametersBuilderCreator) group.getSelectedToggle()).getBuilder(onError);
    }


    @Override
    public void resetView() {

    }

    @Override
    public void saveStateTo(Map<String, String> data) {

    }

    @Override
    public void restoreStateFrom(Map<String, String> data) {

    }
}
