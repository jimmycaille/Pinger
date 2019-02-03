package pinger_v1_0;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PingerCtrl {
  private int timeout = 1000; //TODO ADD OPTION in VIEW
  private PingerView          pingerView;

  public PingerCtrl(PingerView pingerView) {
    this.pingerView = pingerView;
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
        // Create table
        PingThread[] pingThreads2 = new PingThread[nbrHosts];
        // Create threads
        // only one sabnet, do between from and to
        if(nbrSabnet == 1){
            for (int i = 0; i < pingThreads2.length; i++) {
                try {
                    pingThreads2[i] = new PingThread(subnet + Integer.parseInt(pingerView.from3.getText()) + "." + (Integer.parseInt(pingerView.from4.getText())+i), timeout);
                } catch (IOException e){
                    System.err.printf("ERROR starting thread n°%d",i);
                    e.printStackTrace();
                }
                System.out.println("Creating thread " + i);
            }
        }else{
            for (int j = 0; j < nbrSabnet; j++) {
                int byte3 = Integer.parseInt(pingerView.from3.getText()) + j;
                int from = 0;
                int to = 0;
                if (j == 0) {
                  // if first, do between from and 254
                  from = Integer.parseInt(pingerView.from4.getText());
                  to = 254;
                } else if (j + 1 == nbrSabnet) {
                  // if last, do between 1 and to
                  from = 1;
                  to = Integer.parseInt(pingerView.to4.getText());
                } else {
                  // do between 1 and 254 included
                  from = 1;
                  to = 254;
                }
                for (int i = from; i <= to; i++) {
                  try {
                    pingThreads2[j*254+i-Integer.parseInt(pingerView.from4.getText())] = new PingThread(subnet + byte3 + "." + i, timeout);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                  System.out.println("Creating thread " + i + " of the subnet " + j);
                }
              }
        }
        // Start threads
        System.out.println("Starting threads...");
        for (int i = 0; i < pingThreads2.length; i++) {
            System.out.println(i);
            pingThreads2[i].start();
        }
        // Block the Main until the threads are finished
        for (int i = 0; i < pingThreads2.length; i++) {
            try {
                pingThreads2[i].join();
            } catch (InterruptedException e) {
                System.err.printf("ERROR waiting on thread n°%d",i);
                e.printStackTrace();
            }
        }
        // test threads
        String output = "";
        for (int i = 0; i < pingThreads2.length; i++) {
          String ip = pingThreads2[i].getIp();
          String host = pingThreads2[i].getHostName();
          int time = pingThreads2[i].getTime();
          boolean chkAlive = pingerView.chkAlive.isSelected();
          boolean chkTime = pingerView.chkTime.isSelected();
          boolean chkNsLookup = pingerView.chkNslookup.isSelected();
          String thisPing = "";
          if (chkAlive) {
            // show only alive
            if (host != "") {
              // if up, add ip
              thisPing += ip;
              if (chkTime) {
                thisPing += " [" + time + " ms]";
              }
              if (chkNsLookup) {
                thisPing += " [" + host + "]";
              }
              thisPing += "\n";
            }
          } else {
            // show all hosts
            if (host != "") {
              thisPing += ip + " has responded";
              if(chkTime){
                thisPing += " in " + time + "ms";
              }
              thisPing += "\n";
            } else {
              thisPing += ip + " seems down...\n";
            }
          }
          output += thisPing;
        }
        // update view
        pingerView.txtBox.setText(output);
      }
    }
  
  public void updateMyIp() {
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
                        host = addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(host != ""){
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
