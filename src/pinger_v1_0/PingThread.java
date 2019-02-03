package pinger_v1_0;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingThread extends Thread {
  private String  ip;
  private String  name;
  private int     time;
  private int timeout;

  public PingThread(String ip, int timeout) throws IOException {
    super();
    this.ip = ip;
    this.name = "";
    this.timeout = timeout;
  }
  public void run() {
    if(doPing())
        doNslookup();
  }
  private void doNslookup(){
      try {
        this.name = InetAddress.getByName(ip).getHostName();
    } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }
  private boolean doPing() {
    boolean reachable = false;
    try {
        InetAddress inetAddress = InetAddress.getByName(ip);
        time = (int) System.currentTimeMillis();
        reachable = inetAddress.isReachable(timeout);
        time = Math.abs(time - (int) System.currentTimeMillis());
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      return reachable;
  }
  public String getHostName(){
    return this.name;
  }
  public String getIp() {
    return this.ip;
  }
  public int getTime(){
    return this.time;
  }
}
