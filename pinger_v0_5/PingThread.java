package pinger_v0_5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingThread extends Thread {
  private static final String REPLY_LINE = "Reply";
  private static final String REPLY_TIME = "time";
  private static final String NAME_LINE = "Name";
  private static final String NAME_REMOVE = "Name:    ";
  private static final String SRV_NAME_LINE = "Server";
  private static final String SRV_NAME_REMOVE = "Server:  ";
  private static final String SRV_ADDR_LINE = "Address";
  private static final String SRV_ADDR_REMOVE = "Address:  ";
  private String  ip;
  private String  name;
  private String  srv_addr;
  private String  srv_name;
  private String  pingCmd;
  private String  nslookupCmd;
  private int     pingState;             // 0 : down, 1 : up, -1 : unknown
  private Runtime r;
  private Process p;
  private Process n;

  public PingThread(String ip) throws IOException {
    super();
    this.ip = ip;
    this.name = "*name not found*";
    this.srv_addr = "";
    this.srv_name = "";
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
          if (inputLine.contains(NAME_LINE)) {
            this.name = inputLine.replace(NAME_REMOVE, "");
          }
          if (inputLine.contains(SRV_NAME_LINE)) {
            this.srv_name = inputLine.replace(SRV_NAME_REMOVE, "");
          }
          if (inputLine.contains(SRV_ADDR_LINE)) {
            if(this.srv_addr == ""){
              this.srv_addr = inputLine.replace(SRV_ADDR_REMOVE, "");
            }
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
        if (inputLine.contains(REPLY_LINE)) {
          if (inputLine.contains(REPLY_TIME)) {
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
  public String getSrvName(){
    return this.srv_name;
  }
  public String getSrvAddr(){
    return this.srv_addr;
  }
}
