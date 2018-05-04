package main.java;

public class MyEvent {

    public enum MyEventType {
        INVOKE_MENU, JOIN_SERVER, HOST, START_LOCAL, CLICKED
    }

    private MyEventType eventType;

    private Object args;

    public MyEvent(MyEventType eventType) {
        this.eventType = eventType;
    }

    public MyEvent(MyEventType eventType, Object args) {
        this(eventType);
        this.args = args;
    }

    @Override
    public boolean equals(Object other) {
        MyEvent otherEvent = (MyEvent) other;
        return ((this.getClass() == other.getClass()) && (eventType == otherEvent.eventType));
    }

    @Override
    public int hashCode() {
        return eventType.hashCode();
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }
}

