package pinger_v0_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingThread extends Thread {
  private String  ip;
  private String  pingCmd;
  private int pingState; // 0 : down, 1 : up, -1 : unknown
  private Runtime r;
  private Process p;
  public PingThread(String ip) throws IOException {
    super();
    this.ip = ip;
    this.pingCmd = "ping " + ip + " -n 1";
    this.pingState = -1;
    r = Runtime.getRuntime();
    p = r.exec(pingCmd);
  }

  public void run() {
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
  public String getIp(){
    return this.ip;
  }
  public int getPingState(){
    return this.pingState;
  }
}
