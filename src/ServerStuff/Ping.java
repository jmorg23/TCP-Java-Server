package ServerStuff;

public class Ping extends Thread {

    /*
     * The ping data will include
     * The word Ping
     * The exact time it took
     * 
     */

    private byte[] buffer;
    // in miliseconds
    private int waitTime = 10000;
    private Client client;

    public Ping(Client c) {
        client = c;
    }

    void cyclePing() {

    }

    byte[] getPing() {
        return buffer;
    }

    @Override
    public void run() {
        stop = false;
        try {
            new Thread(() -> {
                while (true) {
                    if (!stop)
                        try {
                            Thread.sleep(1);
                            time++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    else
                        break;
                }
            }).start();
            byte[] pingBuffer = new byte[32];

            client.getIs().read(pingBuffer, 0, 32);
            stop = true;
            String s = new String(pingBuffer, "UTF-8");
            s += time;
            client.getOs().write(s.getBytes());
            System.out.println("Ping sent out. It took: "+time+" miliseconds");
            run();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    int time = 0;
    boolean stop = false;

    public void recieveAndReply() {
        try {
            new Thread(() -> {
                while (true) {
                    if (!stop)
                        try {

                            Thread.sleep(1);
                            time++;

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            }).start();
            byte[] pingBuffer = new byte[1024];

            client.getIs().read(pingBuffer, 0, 32);
            stop = true;
            String s = new String(pingBuffer, "UTF-8");
            s += time;
            client.getOs().write(s.getBytes());
            recieveAndReply();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
