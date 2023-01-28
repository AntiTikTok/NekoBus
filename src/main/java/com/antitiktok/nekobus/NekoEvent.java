package com.antitiktok.nekobus;

import com.antitiktok.nekobus.exception.InvalidOperationException;
import com.antitiktok.nekobus.type.Cancellable;

/**
 * Event class for posting to neko eventbus.
 * <p> You can add @Cancellable annotation if you want to cancel event
 *
 * @author KamiSkidder
 */
public abstract class NekoEvent {
    protected boolean isCancelled;

    /**
     * Cancel event. Event class must have @Cancellable Annotation
     */
    public void cancel() {
        setCancelled(true);
    }

    /**
     * Get that is event cancelled
     *
     * @return is event cancelled
     */
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * Set event cancelled. <p> Event class must have @Cancellable Annotation
     *
     * @param cancelled
     */
    public void setCancelled(boolean cancelled) {
        if (!isCancellable())
            throw new InvalidOperationException("The event is not cancelable");
        this.isCancelled = cancelled;
    }

    /**
     * Get that is event cancellable. <p> You can add @Cancellable annotation to event class for enabling cancellation
     *
     * @return is event cancellable
     */
    public boolean isCancellable() {
        return getClass().getAnnotation(Cancellable.class) != null;
    }
}
