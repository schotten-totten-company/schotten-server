package org.rvlander.schotten.server;

import org.rvlander.schotten.encoding.DummyEncoder;
import org.rvlander.schotten.persistence.MemoryStore;
import org.rvlander.schotten.persistence.Store;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.HashMap;


public class Main {

    private final static String WORKER_READY = "\001"; //  Signals worker is ready

    public static void main(String[] args) {
        ClientManager<DummyEncoder> clientManager = new ClientManager(new MemoryStore(new DummyEncoder()));

        ZContext ctx = new ZContext();
        ZMQ.Socket backend = ctx.createSocket(ZMQ.ROUTER);
        backend.bind("tcp://*:5555"); //  For clients

        //  Queue of available workers
        HashMap<ZFrame, Client> workers = new HashMap();

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
                if (msg == null)
                    break; //  Interrupted
                ZFrame address = msg.unwrap();
                System.out.println(msg);
                if(workers.get(address) == null && msg.size() > 2) {
                   System.out.println("Client won't register dropping");
                } else {
                    if(msg.size() == 2) { //Clients wants to register
                       ZClient client = new ZClient(clientManager, address, backend);
                       client.register(msg);
                       workers.put(address, client);
                    } else if (msg.size() == 3) { // Clients want's to play

                    } //Client sent garbage and wont get any response
                };



                //  Forward message to client if it's not a READY
                ZFrame frame = msg.getFirst();
                /*if (new String(frame.getData(), ZMQ.CHARSET).equals(WORKER_READY))
                    msg.destroy();*/
                //else
                msg.send(backend);
            }
        }
        //  When we're done, clean up properly
        /*while (workers.size() > 0) {
            ZFrame frame = workers.remove(0);
            frame.destroy();
        }*/
        workers.clear();
        ctx.close();
    }
}
