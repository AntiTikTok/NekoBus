# NekoBus

イベントバス。遅い気がしなくもないわからん

バグがあったらおしえｗ

# 使い方

1. libsフォルダーをプロジェクトのルートディレクトリに追加
2. NekoBus-1.0.jar を設置

build.gradle
```groovy
implementation files("libs/NekoBus-1.0.jar")
```

3. リロード

# イグザンプル

Main.java
```java
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
```

ExampleEvent.java
```java
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
```

アウトプット:
```
Event Received: 10
Cancelled event: 100
```

# ライセンス

フリー