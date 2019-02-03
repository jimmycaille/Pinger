package pinger_v1_4;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingThread extends Thread {
  private String  ip;
  private String  name;
  private int     time;
  private int     timeout;

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
        System.out.printf("Error while getting hostname of ip:%s\n",ip);
        e.printStackTrace();
    }
  }
  private boolean doPing() {
    boolean reachable = false;
    if(!ip.isEmpty()){
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            time = (int) System.currentTimeMillis();
            reachable = inetAddress.isReachable(timeout);
            time = Math.abs(time - (int) System.currentTimeMillis());
        } catch (IOException e) {
            System.out.printf("Error while pinging addr:%s\n",ip);
            e.printStackTrace();
        }
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
