package pinger_v1_4;

import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.paint.Color;

public class PingAddresses extends Thread {
    PingerView view;
    boolean stop;
    public PingAddresses(PingerView view){
        this.view = view;
        this.stop = false;
    }
    public void run(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                view.btnStart2.setText("Stop pinging");
                view.btnStart2.setDisable(false);
            }
          });
        while(!stop){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    view.progress2.setProgress(-1);
                }
              });
            PingThread pingThread[] = new PingThread[4];
            try {
                for (int i = 0; i < pingThread.length; i++) {
                    pingThread[i] = new PingThread(view.pingAddr[i].getText(), 1000);
                }
                for (int i = 0; i < pingThread.length; i++) {
                    pingThread[i].start();
                }
                for (int i = 0; i < pingThread.length; i++) {
                    pingThread[i].join();
                }
                for (int i = 0; i < pingThread.length; i++) {
                    final int ii = i;
                    if(pingThread[i].getHostName()!=""){
                        view.stateAddr[i].setTextFill(Color.LIGHTGREEN);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                view.stateAddr[ii].setText("● ["+pingThread[ii].getTime()+" ms]");
                            }
                          });
                    }else{
                        view.stateAddr[i].setTextFill(Color.RED);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                view.stateAddr[ii].setText("●");
                            }
                          });
                    }
                }
            } catch (IOException | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            pause();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < view.stateAddr.length; i++) {
                    view.stateAddr[i].setText("●");
                    view.stateAddr[i].setTextFill(Color.BLACK);
                }
                view.btnStart2.setText("Start pinging");
                view.btnStart2.setDisable(false);
                view.progress2.setProgress(1);
            }
          });
    }
    public void end(){
        stop = true;
    }
    private void pause(){
        try {
            for (int i = 0; i < 100; i++) {
                final int ii = i;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        view.progress2.setProgress((double)ii/100);
                    }
                  });
                if(!stop) Thread.sleep((int)(Double.parseDouble(view.pingWait.getValue())*1000/100));
            }
        } catch (InterruptedException e) {
            System.err.printf("Error occured while pausing ping thread !\n");
            e.printStackTrace();
        }
    }
}
