package info.smart_tools.examples.items.items_feature.items_actor.exception;

public class ItemsActorException extends Exception {

    public ItemsActorException(final String message) {
        super(message);
    }

    public ItemsActorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ItemsActorException(final Throwable cause) {
        super(cause);
    }
}
