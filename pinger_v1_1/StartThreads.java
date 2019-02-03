package pinger_v1_1;

import java.io.IOException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


public class StartThreads extends Thread {
    private final int TIMEOUT = 1000;
    private PingerView pingerView;
    private int        nbrHosts;
    private int        nbrSabnet;
    private String     subnet;
    public DoubleProperty processProperty;
    public StartThreads(PingerView pingerView, int nbrHosts, int nbrSabnet, String subnet){
        this.pingerView = pingerView;
        this.nbrHosts = nbrHosts;
        this.nbrSabnet = nbrSabnet;
        this.subnet = subnet;
        processProperty = new SimpleDoubleProperty();
    }
    public void run(){
        // set progress bar
        processProperty.set(0.1);
        pingerView.btnStart.setDisable(true);
        // Create table
        PingThread[] pingThreads2 = new PingThread[nbrHosts];
        // Create threads
        // only one sabnet, do between from and to
        if(nbrSabnet == 1){
            for (int i = 0; i < pingThreads2.length; i++) {
                try {
                    pingThreads2[i] = new PingThread(subnet + Integer.parseInt(pingerView.from3.getText()) + "." + (Integer.parseInt(pingerView.from4.getText())+i), TIMEOUT);
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
                    pingThreads2[j*254+i-Integer.parseInt(pingerView.from4.getText())] = new PingThread(subnet + byte3 + "." + i, TIMEOUT);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                  System.out.println("Creating thread " + i + " of the subnet " + j);
                }
              }
        }
        // set progress bar
        processProperty.set(0.2);
        // Start threads
        System.out.println("Starting threads...");
        for (int i = 0; i < pingThreads2.length; i++) {
            System.out.println(i);
            pingThreads2[i].start();
        }
        // set progress bar
        processProperty.set(0.3);
        // Block the Main until the threads are finished
        for (int i = 0; i < pingThreads2.length; i++) {
            if(i == pingThreads2.length/4)
                processProperty.set(0.5);
            if(i == pingThreads2.length/4*2)
                processProperty.set(0.6);
            if(i == pingThreads2.length/4*3)
                processProperty.set(0.7);
            try {
                pingThreads2[i].join();
            } catch (InterruptedException e) {
                System.err.printf("ERROR waiting on thread n°%d",i);
                e.printStackTrace();
            }
        }
        // set progress bar
        processProperty.set(0.9);
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
        // set start btn
        pingerView.btnStart.setDisable(false);
        processProperty.set(1);
    }
}
