package pinger_v0_7_win10;

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
  public TextField   from1;
  public TextField   from2;
  public TextField   from3;
  public TextField   from4;
  public TextField   to1;
  public TextField   to2;
  public TextField   to3;
  public TextField   to4;
  public CheckBox    chkAlive;
  public CheckBox    chkTime;
  public CheckBox    chkNslookup;
  public CheckBox    chkShowSrv;
  public TextArea    txtBox;

  @Override
  public void start(Stage myStage) throws Exception {
    // pingerCtrl
    pingerCtrl = new PingerCtrl(this);

    // window title
    myStage.setTitle("Pinger v0.7 Win10Eng");

    // Checkbox to show only alive, enable nslookup
    chkNslookup = new CheckBox("Show hostname");
    chkNslookup.setDisable(true);
    chkTime = new CheckBox("Show time");
    chkTime.setSelected(true);
    chkShowSrv = new CheckBox("Show server");
    chkShowSrv.setDisable(true);
    chkAlive = new CheckBox("Only alive");
    chkAlive.setOnAction(event ->{
      if(chkAlive.isSelected()){
        chkNslookup.setDisable(false);
        chkShowSrv.setDisable(false);
      }else{
        chkNslookup.setDisable(true);
        chkShowSrv.setDisable(true);
      }
    });
    VBox checkPane = new VBox(2);
    checkPane.setAlignment(Pos.CENTER_LEFT);
    checkPane.getChildren().addAll(chkAlive,chkTime, chkNslookup, chkShowSrv);

    // Pane for network entry
    Label lblPt1 = new Label(".");
    Label lblPt2 = new Label(".");
    Label lblPt3 = new Label(".");
    Label lblPt4 = new Label(".");
    Label lblPt5 = new Label(".");
    Label lblPt6 = new Label(".");
    Label lblFrom = new Label("From ");
    Label lblTo = new Label("To ");
    from1 = new TextField("192");
    from1.setOnKeyReleased(event ->{
      to1.setText(from1.getText());
    });
    from2 = new TextField("168");
    from2.setOnKeyReleased(event ->{
      to2.setText(from2.getText());
    });
    from3 = new TextField("1");
    from3.setOnKeyReleased(event ->{
      to3.setText(from3.getText());
    });
    from4 = new TextField("1");
    to1 = new TextField("192");
    to1.setDisable(true);
    to2 = new TextField("168");
    to2.setDisable(true);
    to3 = new TextField("1");
    to4 = new TextField("254");
    lblPt1.setPrefHeight(25);
    lblPt2.setPrefHeight(25);
    lblPt3.setPrefHeight(25);
    lblPt4.setPrefHeight(25);
    lblPt5.setPrefHeight(25);
    lblPt6.setPrefHeight(25);
    lblFrom.setPrefHeight(25);
    lblTo.setPrefHeight(25);
    from1.setPrefWidth(40);
    from2.setPrefWidth(40);
    from3.setPrefWidth(40);
    from4.setPrefWidth(40);
    to1.setPrefWidth(40);
    to2.setPrefWidth(40);
    to3.setPrefWidth(40);
    to4.setPrefWidth(40);
    HBox netFromPane = new HBox(2);
    netFromPane.setAlignment(Pos.CENTER_RIGHT);
    netFromPane.getChildren().addAll(lblFrom, from1, lblPt1, from2, lblPt2,
        from3, lblPt3, from4);
    HBox netToPane = new HBox(2);
    netToPane.setAlignment(Pos.CENTER_RIGHT);
    netToPane.getChildren().addAll(lblTo, to1, lblPt4, to2, lblPt5, to3,
        lblPt6, to4);
    VBox twoNetsPane = new VBox(10);
    twoNetsPane.getChildren().addAll(netFromPane, netToPane);
    
    // Top HPane
    HBox topPane = new HBox(10);
    topPane.setAlignment(Pos.CENTER);
    topPane.getChildren().addAll(twoNetsPane, checkPane);

    // Button to start the pings
    Button btnStart = new Button("Run the discovery");
    btnStart.setPrefWidth(340);
    btnStart.setOnAction(event -> {
      btnStart.setDisable(true);
      pingerCtrl.pingSubnet();
      btnStart.setDisable(false);
    });

    // Textbox showing output
    txtBox = new TextArea();
    txtBox.setPrefSize(340, 300);
    txtBox.setEditable(false);

    // Principal content
    VBox masterPane = new VBox(10);
    masterPane.setAlignment(Pos.CENTER);
    masterPane.setPadding(new Insets(5));
    masterPane.getChildren().addAll(topPane,
        btnStart, txtBox);

    // set scene
    Scene myScene = new Scene(masterPane);
    myStage.setScene(myScene);
    myStage.sizeToScene();
    myStage.show();
    
    //update ip
    pingerCtrl.updateMyIp("WiFi");
  }
}
