package com.antitiktok.nekobus.test;

import com.antitiktok.nekobus.NekoEvent;
import com.antitiktok.nekobus.type.Cancellable;

@Cancellable
public class ExampleEvent extends NekoEvent {
    public int param;

    public ExampleEvent(int param) {
        this.param = param;
    }
}