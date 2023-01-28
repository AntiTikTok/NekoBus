package com.antitiktok.nekobus.test;

import com.antitiktok.nekobus.NekoBus;
import com.antitiktok.nekobus.type.ReceiveEvent;

public class Main {
    public static void main(String[] args) {
        // create eventbus instance
        NekoBus eventBus = new NekoBus();
        // register this class to receive events
        Main main = new Main();
        eventBus.register(main);
        // post event (event class must extend NekoEvent)
        ExampleEvent event = new ExampleEvent(10);
        eventBus.post(event);
        if (event.isCancelled()) {
            System.out.printf("Cancelled event: %d", event.param);
        }
    }

    @ReceiveEvent
    public void onReceiveExampleEvent(ExampleEvent event) {
        System.out.printf("Event Received: %d\n", event.param);
        event.param *= event.param;
        event.cancel();
    }
}
