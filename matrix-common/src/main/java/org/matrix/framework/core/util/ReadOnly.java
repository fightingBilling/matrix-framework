package org.matrix.framework.core.util;

public class ReadOnly<T> {
    private T value;
    private boolean assigned;

    public boolean assigned() {
        return this.assigned;
    }

    public T value() {
        return this.value;
    }

    public void set(T value) {
        if (this.assigned)
            throw new IllegalStateException("value has been assigned, oldValue=" + this.value + ", newValue=" + value);
        this.assigned = true;
        this.value = value;
    }

    public boolean valueEquals(T otherValue) {
        return ((this.value == null) && (otherValue == null)) || ((otherValue != null) && (otherValue.equals(this.value)));
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}