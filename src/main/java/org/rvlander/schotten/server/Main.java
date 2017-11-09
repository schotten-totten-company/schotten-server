package org.rvlander.schotten.server;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.ArrayList;

public class Main {

    private final static String WORKER_READY = "\001"; //  Signals worker is ready

    public static void main(String[] args) {
        ZContext ctx = new ZContext();
        ZMQ.Socket backend = ctx.createSocket(ZMQ.ROUTER);
        backend.bind("tcp://*:5555"); //  For clients

        //  Queue of available workers
        ArrayList<ZFrame> workers = new ArrayList<ZFrame>();

        ZMQ.Poller poller = ctx.createPoller(2);
        poller.register(backend, ZMQ.Poller.POLLIN);

        System.out.println("Listening on port 5555");

        //  The body of this example is exactly the same as lruqueue2.
        while (true) {
            int rc = poller.poll(-1);

            //  Poll frontend only if we have available workers
            if (rc == -1)
                break; //  Interrupted

            System.out.println("receiving stuff");

            //  Handle worker activity on backend
            if (poller.pollin(0)) {
                //  Use worker address for LRU routing
                ZMsg msg = ZMsg.recvMsg(backend);
                System.out.println(msg);
                /*if (msg == null)
                    break; //  Interrupted
                ZFrame address = msg.unwrap();
                workers.add(address);

                //  Forward message to client if it's not a READY
                ZFrame frame = msg.getFirst();
                /*if (new String(frame.getData(), ZMQ.CHARSET).equals(WORKER_READY))
                    msg.destroy();*/
                //else
                msg.send(backend);
            }
        }
        //  When we're done, clean up properly
        while (workers.size() > 0) {
            ZFrame frame = workers.remove(0);
            frame.destroy();
        }
        workers.clear();
        ctx.close();
    }
}
