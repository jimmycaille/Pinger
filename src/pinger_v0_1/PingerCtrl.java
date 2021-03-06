package pinger_v0_1;

import java.io.IOException;
import java.util.ArrayList;

public class PingerCtrl {
  private PingerView pingerView;

  public PingerCtrl(PingerView pingerView) {
    this.pingerView = pingerView;
  }

  public void pingSubnet() {
    String subnet = pingerView.net1.getText()+"."+pingerView.net2.getText()+"."+pingerView.net3.getText()+".";
    ArrayList<PingThread> pingThreads = new ArrayList<PingThread>();
    // create threads
    for (int i = Integer.parseInt((String)pingerView.netFrom.getValue()); i <= Integer.parseInt((String)pingerView.netTo.getValue()); i++) {
      try {
        pingThreads.add(new PingThread(subnet + i));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    // run threads
    for (int i = 0; i < pingThreads.size(); i++) {
      pingThreads.get(i).run();
    }
    // test threads
    String output = "";
    for (int i = 0; i < pingThreads.size(); i++) {
      if (pingThreads.get(i).getPingState() == 1) {
        output += pingThreads.get(i).getIp() + " has responded !\n";
      } else if (!pingerView.chkAlive.isSelected()
          && pingThreads.get(i).getPingState() == 0) {
        output += pingThreads.get(i).getIp() + " is dead...\n";
      } else if (!pingerView.chkAlive.isSelected()
          && pingThreads.get(i).getPingState() == -1) {
        output += pingThreads.get(i).getIp() + " was not tested.\n";
      }

    }
    // update view
    pingerView.txtBox.setText(output);
  }
}
