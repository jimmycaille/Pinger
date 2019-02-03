package pinger_v0_2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PingerView extends Application {
  private PingerCtrl pingerCtrl;
  public TextField   net1;
  public TextField   net2;
  public TextField   net3;
  public ComboBox    netFrom;
  public ComboBox    netTo;
  public CheckBox    chkAlive;
  public CheckBox    chkNslookup;
  public TextArea    txtBox;

  @Override
  public void start(Stage myStage) throws Exception {
    // pingerCtrl
    pingerCtrl = new PingerCtrl(this);

    // window title
    myStage.setTitle("Pinger v0.2");

    //
    String[] nbrList = new String[254];
    for (int i = 0; i < nbrList.length; i++) {
      nbrList[i] = Integer.toString(i + 1);
    }

    // Pane for network entry
    Label lblPt1 = new Label(".");
    Label lblPt2 = new Label(".");
    Label lblPt3 = new Label(".");
    Label lblFrom = new Label("From :");
    Label lblTo = new Label("To :");
    net1 = new TextField("192");
    net2 = new TextField("168");
    net3 = new TextField("1");
    netFrom = new ComboBox();
    netTo = new ComboBox();
    netFrom.getItems().addAll(nbrList);
    netTo.getItems().addAll(nbrList);
    netFrom.setValue("1");
    netTo.setValue("254");
    lblPt1.setPrefHeight(25);
    lblPt2.setPrefHeight(25);
    lblPt3.setPrefHeight(25);
    lblFrom.setPrefHeight(25);
    lblTo.setPrefHeight(25);
    net1.setPrefWidth(40);
    net2.setPrefWidth(40);
    net3.setPrefWidth(40);
    netFrom.setPrefWidth(70);
    netTo.setPrefWidth(70);
    HBox networkPane = new HBox(5);
    networkPane.getChildren().addAll(net1, lblPt1, net2, lblPt2, net3, lblPt3,
        lblFrom, netFrom, lblTo, netTo);

    // Checkbox to show only alive, enable nslookup
    chkAlive = new CheckBox("Display only alive");
    chkNslookup = new CheckBox("Try to get hostname");
    HBox checkboxes = new HBox(15);
    checkboxes.setAlignment(Pos.CENTER);
    checkboxes.getChildren().addAll(chkAlive, chkNslookup);

    // Button to start the pings
    Button btnStart = new Button("Run the discovery");
    btnStart.setOnAction(event -> {
      btnStart.setDisable(true);
      pingerCtrl.pingSubnet();
      btnStart.setDisable(false);
    });

    // Textbox showing output
    txtBox = new TextArea();
    txtBox.setPrefSize(250, 200);
    txtBox.setEditable(false);

    // Principal content
    VBox masterPane = new VBox(10);
    masterPane.setAlignment(Pos.CENTER);
    masterPane.setPadding(new Insets(5));
    masterPane.getChildren().addAll(networkPane, checkboxes, btnStart, txtBox);

    // set scene
    Scene myScene = new Scene(masterPane);
    myStage.setScene(myScene);
    myStage.sizeToScene();
    myStage.show();
  }
}
