package pinger_v1_1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
  public Button      btnStart;
  public TextArea    txtBox;
  public ProgressBar progress;
  TextField[]        pingAddr = new TextField[4];
  Label[]            stateAddr = new Label[4];
  ComboBox<String>   pingWait;
  Button             btnStart2;
  public ProgressBar progress2;
  @Override
  public void start(Stage myStage) throws Exception {
    // pingerCtrl
    pingerCtrl = new PingerCtrl(this);

    // window title
    myStage.setTitle("Pinger v1.1");
    
    // BEGIN OF TAB 1 CONTENT

    // Checkbox to show only alive, enable nslookup
    chkNslookup = new CheckBox("Show hostname");
    chkNslookup.setDisable(true);
    chkTime = new CheckBox("Show time");
    chkTime.setSelected(true);
    chkAlive = new CheckBox("Only alive");
    chkAlive.setOnAction(event ->{
      if(chkAlive.isSelected()){
        chkNslookup.setDisable(false);
      }else{
        chkNslookup.setDisable(true);
      }
    });
    VBox checkPane = new VBox(2);
    checkPane.setAlignment(Pos.CENTER_LEFT);
    checkPane.getChildren().addAll(chkAlive,chkTime, chkNslookup);

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
    btnStart = new Button("Run the discovery");
    btnStart.setPrefWidth(340);
    btnStart.setOnAction(event -> {
      pingerCtrl.pingSubnet();
    });

    // Textbox showing output
    txtBox = new TextArea();
    txtBox.setPrefSize(340, 300);
    txtBox.setEditable(false);

    VBox masterPane = new VBox(10);
    
    // Progress bar
    progress = new ProgressBar(); 
    progress.setProgress(0);
    progress.prefWidthProperty().bind(masterPane.widthProperty());

    // Principal content
    masterPane.setAlignment(Pos.CENTER);
    masterPane.setPadding(new Insets(5));
    masterPane.getChildren().addAll(topPane,
        btnStart, txtBox, progress);
    
    // Tab 1
    Tab tab1 = new Tab("Discovery");
    tab1.setClosable(false);
    tab1.setContent(masterPane);
    
    // BEGIN OF TAB 2 CONTENT
    Label lblAddr1 = new Label("Address 1 :");
    Label lblAddr2 = new Label("Address 2 :");
    Label lblAddr3 = new Label("Address 3 :");
    Label lblAddr4 = new Label("Address 4 :");
    Label lblPingTime1 = new Label("Wait");
    Label lblPingTime2 = new Label("sec between pings");

    for(int i = 0; i < pingAddr.length; i++){
        pingAddr[i]  = new TextField();
        stateAddr[i] = new Label("●");
        stateAddr[i].setFont(Font.font("Arial",FontWeight.BOLD, 14));
    }
    
    ObservableList<String> options = 
    FXCollections.observableArrayList(
        "0",
        "0.5",
        "1",
        "2",
        "5"
    );
    pingWait = new ComboBox<String>(options);
    pingWait.setValue("1");
    
    btnStart2 = new Button("Start pinging");
    btnStart2.setOnAction(event -> {
      pingerCtrl.pingAddressesStartStop();
    });
    

    // Progress bar
    progress2 = new ProgressBar(); 
    progress2.setProgress(0);
    progress2.prefWidthProperty().bind(masterPane.widthProperty());
    
    // Lines
    HBox line1 = new HBox(10);
    line1.setPadding(new Insets(10, 0, 0, 20));
    line1.setAlignment(Pos.CENTER_LEFT);
    line1.getChildren().addAll(lblAddr1, pingAddr[0], stateAddr[0]);
    HBox line2 = new HBox(10);
    line2.setPadding(new Insets(0, 0, 0, 20));
    line2.setAlignment(Pos.CENTER_LEFT);
    line2.getChildren().addAll(lblAddr2, pingAddr[1], stateAddr[1]);
    HBox line3 = new HBox(10);
    line3.setPadding(new Insets(0, 0, 0, 20));
    line3.setAlignment(Pos.CENTER_LEFT);
    line3.getChildren().addAll(lblAddr3, pingAddr[2], stateAddr[2]);
    HBox line4 = new HBox(10);
    line4.setPadding(new Insets(0, 0, 0, 20));
    line4.setAlignment(Pos.CENTER_LEFT);
    line4.getChildren().addAll(lblAddr4, pingAddr[3], stateAddr[3]);
    HBox line5 = new HBox(10);
    line5.setAlignment(Pos.CENTER);
    line5.getChildren().addAll(lblPingTime1, pingWait, lblPingTime2);
    HBox line6 = new HBox(10);
    line6.setAlignment(Pos.CENTER);
    line6.getChildren().addAll(btnStart2);
    HBox line7 = new HBox(10);
    line7.setAlignment(Pos.CENTER);
    line7.getChildren().addAll(progress2);
    
    // Master pane
    VBox masterPane2 = new VBox(10);
    masterPane2.setAlignment(Pos.CENTER);
    masterPane2.setPadding(new Insets(5));
    masterPane2.getChildren().addAll(line1, line2, line3, line4, line5, line6, line7);
    
    // Tab 2
    Tab tab2 = new Tab("Ping");
    tab2.setClosable(false);
    tab2.setOnSelectionChanged(new EventHandler<Event>() {
        public void handle(Event t) {
            if (tab2.isSelected()) {
                myStage.setMaxWidth(350);
                myStage.setMaxHeight(320);
            }else{
                myStage.setMaxWidth(Double.MAX_VALUE);
                myStage.setMaxHeight(Double.MAX_VALUE);
                myStage.sizeToScene();
            }
        }
    });
    tab2.setContent(masterPane2);
    
    // Tab pane
    TabPane tabpane = new TabPane();
    tabpane.getTabs().addAll(tab1, tab2);
    
    // set scene
    Scene myScene = new Scene(tabpane);
    myStage.setScene(myScene);
    myStage.sizeToScene();
    myStage.show();
    
    //update ip
    pingerCtrl.updateMyIp();
  }
}
