package pinger_v0_6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PingerCtrl {
  private static final String IPV4_NAME   = "IPv4";
  private static final String IPV4_REMOVE = "   IPv4 Address. . . . . . . . . . . : ";
  private PingerView          pingerView;

  public PingerCtrl(PingerView pingerView) {
    this.pingerView = pingerView;
  }

  public void pingSubnet() {
    // begin of subnet
    String subnet = pingerView.from1.getText() + "."
        + pingerView.from2.getText() + ".";
    ArrayList<PingThread> pingThreads = new ArrayList<PingThread>();
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
      // create threads
      // only one sabnet, do between from and to
      if (nbrSabnet == 1) {
        for (int i = Integer.parseInt(pingerView.from4.getText()); i <= Integer
            .parseInt(pingerView.to4.getText()); i++) {
          try {
            pingThreads.add(new PingThread(subnet
                + Integer.parseInt(pingerView.from3.getText()) + "." + i));
          } catch (IOException e) {
            e.printStackTrace();
          }
          System.out.println("Creating thread " + i);
        }
      } else {
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
            // do between 1 and 254 include
            from = 1;
            to = 254;
          }
          for (int i = from; i <= to; i++) {
            try {
              pingThreads.add(new PingThread(subnet + byte3 + "." + i));
            } catch (IOException e) {
              e.printStackTrace();
            }
            System.out.println("Creating thread " + i + " of the subnet " + j);
          }
        }
      }

      // run threads
      for (int i = 0; i < pingThreads.size(); i++) {
        pingThreads.get(i).run();
        System.out.println("Running thread " + i);
      }
      // test threads
      String output = "";
      for (int i = 0; i < pingThreads.size(); i++) {
        String ip = pingThreads.get(i).getIp();
        String host = pingThreads.get(i).getHostName();
        String srvName = pingThreads.get(i).getSrvName();
        String srvAddr = pingThreads.get(i).getSrvAddr();
        String time = pingThreads.get(i).getTime();
        int pingState = pingThreads.get(i).getPingState();
        boolean chkAlive = pingerView.chkAlive.isSelected();
        boolean chkTime = pingerView.chkTime.isSelected();
        boolean chkNsLookup = pingerView.chkNslookup.isSelected();
        boolean chkShowSrv = pingerView.chkShowSrv.isSelected();
        String thisPing = "";
        if (chkAlive) {
          // show only alive
          if (pingState == 1) {
            // if up, add ip
            thisPing += ip;
            if (chkTime) {
              thisPing += " [" + time + "]";
            }
            if (chkNsLookup) {
              thisPing += " [" + host + "]";
            }
            if (chkShowSrv) {
              thisPing += " [" + srvName + ":" + srvAddr + "]";
            }
            thisPing += "\n";
          }
        } else {
          // show all hosts
          if (pingState == 1) {
            thisPing += ip + " has responded";
            if(chkTime){
              thisPing += " in " + time;
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

  public void updateMyIp(String cardName) {
    Runtime r = Runtime.getRuntime();
    String addr = "";
    try {
      Process p = r.exec("ipconfig");
      BufferedReader in = new BufferedReader(
          new InputStreamReader(p.getInputStream()));
      String inputLine;
      boolean found = false;
      while ((inputLine = in.readLine()) != null) {
        if (inputLine.contains(cardName)) {
          found = true;
        }
        if (found && inputLine.contains(IPV4_NAME)) {
          addr = inputLine.replace(IPV4_REMOVE, "");
          break;
        }
      }
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (addr != "") {
      String[] parts = addr.split("\\.");
      pingerView.from1.setText(parts[0]);
      pingerView.from2.setText(parts[1]);
      pingerView.from3.setText(parts[2]);
      pingerView.to1.setText(parts[0]);
      pingerView.to2.setText(parts[1]);
      pingerView.to3.setText(parts[2]);
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
