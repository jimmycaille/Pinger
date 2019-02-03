package pinger_v1_3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import javafx.application.Platform;

public class TraceRoute extends Thread{
    private boolean stop;
    private boolean end;
    private PingerView pingerView;
    public TraceRoute(PingerView pingerView){
        this.stop = false;
        this.end = false;
        this.pingerView = pingerView;
    }
    public void run(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pingerView.progress3.setProgress(-1);
            }
        });
        String addr = pingerView.traceAddr.getText();
        String os = System.getProperty("os.name").toLowerCase();
        String resolve = pingerView.chkResolve.isSelected() ? "" : "-d " ;
        InetAddress address;
        try {
            address = InetAddress.getByName(addr);
            Process traceRt;
            if(os.contains("win")) traceRt = Runtime.getRuntime().exec("tracert "+ resolve + address.getHostAddress());
            else traceRt = Runtime.getRuntime().exec("traceroute " + address.getHostAddress());

            // read the output from the command
            BufferedReader stdInput = new BufferedReader(new 
                    InputStreamReader(traceRt.getInputStream()));
            String s;
            pingerView.txtBox3.setText("");
            while ((s = stdInput.readLine()) != null && !stop) {
                if(!s.isEmpty())
                pingerView.txtBox3.setText(pingerView.txtBox3.getText()+s+"\n");
                pingerView.txtBox3.appendText("");
            }
        }catch (IOException e) {
            System.err.printf("Error while performing trace route command\n%s", e);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pingerView.progress3.setProgress(1);
            }
        });
        this.end = true;
    }
    public void end(){
        stop = true;
    }
    public boolean getEnd(){
        return this.end;
    }
}
