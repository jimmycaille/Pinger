package pinger_v1_1;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PingerCtrl {
  private PingerView pingerView;
  private PingAddresses pingAddresses;

  public PingerCtrl(PingerView pingerView) {
    this.pingerView = pingerView;
  }
  
  public void pingAddressesStartStop(){
      if(pingAddresses == null){
          pingerView.btnStart2.setDisable(true);
          pingAddresses = new PingAddresses(pingerView);
          pingAddresses.start();

      }else{
          pingerView.btnStart2.setDisable(true);
          pingAddresses.end();
          pingAddresses = null;
      }
  }
  public void pingSubnet() {
        // begin of subnet
        String subnet = pingerView.from1.getText() + "."
        + pingerView.from2.getText() + ".";
        // compute nbr of subnets
        int nbrSabnet = Integer.parseInt(pingerView.to3.getText())
            - Integer.parseInt(pingerView.from3.getText()) + 1;
        // compute nbr of hosts
        int nbrHosts = 0;
        if (nbrSabnet == 1) {
          nbrHosts = Integer.parseInt(pingerView.to4.getText())
              - Integer.parseInt(pingerView.from4.getText()) + 1;
        } else {
          nbrHosts = 255 - Integer.parseInt(pingerView.from4.getText())
              + Integer.parseInt(pingerView.to4.getText()) + (nbrSabnet - 2) * 254;
        }
        System.out.println("Nbr of host : " + nbrHosts);
        if (nbrHosts > 1000) {
          showWarning(nbrHosts);
        } else {
          StartThreads startThreads = new StartThreads(pingerView, nbrHosts, nbrSabnet, subnet);
          pingerView.progress.progressProperty().bind(startThreads.processProperty);
          startThreads.start();
        }
    }
  
  public void updateMyIp() {
      boolean found = false;
      String host = "";
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while(netInterfaces.hasMoreElements()){
                NetworkInterface netInt = netInterfaces.nextElement();
                if(netInt.isUp() & netInt.getDisplayName().contains("Wireless")){
                    Enumeration<InetAddress> netAddresses = netInt.getInetAddresses();
                    while(netAddresses.hasMoreElements()){
                        InetAddress addr = netAddresses.nextElement();
                        //System.out.println(netInt.getDisplayName()+addr.getHostAddress());
                        if(!found){
                            host = addr.getHostAddress();
                            found = true;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.out.printf("Error while getting host ip...\n");
            e.printStackTrace();
        }
        if(host != "" && !host.contains(":")){
            pingerView.from1.setText(host.split("\\.")[0]);
            pingerView.from2.setText(host.split("\\.")[1]);
            pingerView.from3.setText(host.split("\\.")[2]);
            pingerView.to1.setText(host.split("\\.")[0]);
            pingerView.to2.setText(host.split("\\.")[1]);
            pingerView.to3.setText(host.split("\\.")[2]);
        }
  }

  public static void showWarning(int nbrHosts) {
    Alert dialog = new Alert(AlertType.WARNING);
    dialog.setTitle("WARNING");
    dialog.setHeaderText("Maximum number of hosts exceeded");
    dialog.setContentText("You was going to execute a discovery on " + nbrHosts
        + " hosts !\nPlease reduce the range under 1000 hosts.");
    dialog.showAndWait();
  }
}
