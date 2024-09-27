package com.eggplanters;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import com.eggplanters.lib.*;
import javafx.application.Application;
import javafx.css.Stylesheet;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.image.Image;

public class App extends Application {

    private VBox appList;
    private VBox appDetails;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(createRoot(), 1280, 720);
        stage.setScene(scene);
        Font.loadFont(getClass().getResourceAsStream("/com/eggplanters/Poppins.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/com/eggplanters/Poppins-Bold.ttf"), 12);
        stage.getScene().getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("dracula-theme.css")).toExternalForm()
                // Stylesheet provided by https://github.com/mkpaz/atlantafx
                , Objects.requireNonNull(getClass().getResource("fontstyle.css")).toExternalForm());
        stage.setTitle("Eggplanters Store");
        stage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/eggplanters/app_icon.png"))));

        loadApps();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public Parent createRoot() {
        HBox hBox = new HBox();
        hBox.setPrefSize(1280, 720);

        VBox leftVBox = new VBox();
        leftVBox.setPrefSize(300, 400);
        leftVBox.setMinSize(300, Region.USE_COMPUTED_SIZE);
        leftVBox.setMaxSize(400, Region.USE_COMPUTED_SIZE);
        HBox.setHgrow(leftVBox, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(200, 200);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        appList = new VBox();
        appList.setMaxHeight(Double.MAX_VALUE);
        appList.setMaxWidth(Double.MAX_VALUE);
        appList.setPadding(new Insets(12));
        appList.setSpacing(12);

        scrollPane.setContent(appList);

        leftVBox.getChildren().add(scrollPane);

        // Details
        VBox rightVBox = new VBox();
        HBox.setHgrow(rightVBox, Priority.ALWAYS);
        rightVBox.setPrefSize(700, 400);

        ScrollPane detailsScrollPane = new ScrollPane();
        detailsScrollPane.setFitToWidth(true);

        detailsScrollPane.setPrefSize(200, 200);
        VBox.setVgrow(detailsScrollPane, Priority.ALWAYS);

        appDetails = new VBox();
        appDetails = new VBox();
        appDetails.setMaxHeight(Double.MAX_VALUE);
        appDetails.setMaxWidth(Double.MAX_VALUE);
        appDetails.setPadding(new Insets(12));
        appDetails.setSpacing(12);

        detailsScrollPane.setContent(appDetails);
        rightVBox.getChildren().add(detailsScrollPane);

        hBox.getChildren().addAll(leftVBox, rightVBox);

        VBox parent = new VBox();
        parent.getChildren().addAll(hBox);
        VBox.setVgrow(hBox, Priority.ALWAYS);
        parent.getStyleClass().add("parent");
        return parent;
    }

    public void addEntry(AppEntry appEntry, VBox appList) {

        AppEntryNode node = new AppEntryNode(appEntry);
        node.setOnAction((e) -> {
            setDetails(appEntry);
            for (var appEntries : appList.getChildren()) {
                appEntries.getStyleClass().removeIf((s) -> s.equals("selected"));
            }
            node.getStyleClass().add("selected");
        });

        appList.getChildren().add(node);
    }

    public void loadApps() {
        File file = new File("src/main/resources/com/eggplanters/appStore.json");
        try {
            AppStoreReader appStoreReader = new AppStoreReader(file);
            var appEntries = appStoreReader.parseJSON();
            for (AppEntry entry : appEntries) {
                addEntry(entry, appList);
            }
        } catch (NotJSONException e) {
            System.out.println("File is not JSON");
        }
    }

    public void setDetails(AppEntry entry) {
        Label appTitle = new Label(entry.getTitle());
        appTitle.getStyleClass().add("detail-title");
        Label appGenre = new Label(entry.getGenre());
        appGenre.getStyleClass().add("detail-subtitle");
        Label appPublisher = new Label("Published by " + entry.getPublisher());
        appPublisher.getStyleClass().add("detail-subtitle");
        Label appMetricsText = new Label(
                entry.getStar_rating() + " - " + formatNumber(entry.getDownloads()) + "+ downloads");
        appMetricsText.getStyleClass().add("detail-subtitle");
        HBox appMetrics = new HBox(12);
        appMetrics.getChildren().addAll(
                new Icon(Objects.requireNonNull(getClass().getResourceAsStream("/com/eggplanters/star.png")), 18),
                appMetricsText);

        HBox headerPane = new HBox(12);
        Icon appImage = new Icon(
                Objects.requireNonNull(getClass().getResourceAsStream("/com/eggplanters/app_placeholder.png")),
                156);
        VBox headerText = new VBox(12);
        headerText.getChildren().addAll(appTitle, appPublisher, appGenre, appMetrics);
        headerPane.getChildren().addAll(appImage, headerText);

        TextFlow description = new TextFlow();
        description.getChildren().add(new Text("\t" + entry.getDescription()));
        description.getStyleClass().add("description");
        description.setPadding(new Insets(36));
        description.setTextAlignment(TextAlignment.JUSTIFY);

        appDetails.getChildren().clear();
        appDetails.getChildren().addAll(headerPane, description);
    }

    public String formatNumber(int number) {
        NumberFormat fmt = NumberFormat.getCompactNumberInstance(
                Locale.US, NumberFormat.Style.SHORT);
        return fmt.format(number);
    }
}