package pinger_v0_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingThread extends Thread {
  private String  ip;
  private String  pingCmd;
  private String  name;
  private String  nslookupCmd;
  private int     pingState;             // 0 : down, 1 : up, -1 : unknown
  private Runtime r;
  private Process p;
  private Process n;

  public PingThread(String ip) throws IOException {
    super();
    this.ip = ip;
    this.name = "*not found*";
    this.pingCmd = "ping " + ip + " -n 1";
    this.nslookupCmd = "nslookup " + ip;
    this.pingState = -1;
    r = Runtime.getRuntime();
    p = r.exec(pingCmd);
    n = r.exec(nslookupCmd);
  }

  public void run() {
    doPing();
    doNslookup();
  }
  private void doNslookup(){
    if(pingState == 1){
      try {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(n.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
          if (inputLine.contains("Name")) {
            this.name = inputLine.replace("Name:    ", "");
          }
        }
        in.close();

      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }

  private void doPing() {
    pingState = 0;
    try {
      BufferedReader in = new BufferedReader(
          new InputStreamReader(p.getInputStream()));
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        if (inputLine.contains("Reply")) {
          if (inputLine.contains("time")) {
            pingState = 1;
          }
        }
      }
      in.close();

    } catch (IOException e) {
      System.out.println(e);
    }
  }
  
  public String getHostName(){
    return this.name;
  }

  public String getIp() {
    return this.ip;
  }

  public int getPingState() {
    return this.pingState;
  }
}
