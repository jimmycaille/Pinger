package pinger_v0_4;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class PingerCtrl {
  private PingerView pingerView;

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
        if (pingThreads.get(i).getPingState() == 1) {
          if (pingerView.chkNslookup.isSelected()) {
            output += pingThreads.get(i).getIp() + " ["
                + pingThreads.get(i).getHostName() + "]\n";
          } else {
            output += pingThreads.get(i).getIp() + " is up\n";
          }
        } else if (!pingerView.chkAlive.isSelected()
            && pingThreads.get(i).getPingState() == 0) {
          output += pingThreads.get(i).getIp() + " seems dead...\n";
        } else if (!pingerView.chkAlive.isSelected()
            && pingThreads.get(i).getPingState() == -1) {
          output += pingThreads.get(i).getIp() + " was not tested\n";
        }

      }
      // update view
      pingerView.txtBox.setText(output);
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
