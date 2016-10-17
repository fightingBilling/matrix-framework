package org.matrix.framework.core.platform.web.session;

import org.matrix.framework.core.collection.Key;
import org.matrix.framework.core.collection.KeyMap;
import org.matrix.framework.core.util.ReadOnly;

/**
 * Matrix Framework的session存储结构.
 * 
 * @author pankai 2015年6月18日
 */
public class SessionContext {

    private final ReadOnly<String> id = new ReadOnly<String>();
    private final KeyMap session = new KeyMap();
    private boolean changed;
    private boolean invalidated;

    public <T> T get(Key<T> key) {
        return this.session.get(key);
    }

    public <T> void set(Key<T> key, T value) {
        this.session.put(key, value);
        this.changed = true;
    }

    public void invalidate() {
        this.session.clear();
        this.changed = true;
        this.invalidated = true;
    }

    boolean changed() {
        return this.changed;
    }

    boolean invalidated() {
        return this.invalidated;
    }

    String getId() {
        return (String) this.id.value();
    }

    void setId(String id) {
        this.id.set(id);
    }

    void loadSessionData(String sessionData) {
        this.session.deserialize(sessionData);
    }

    String getSessionData() {
        return this.session.serialize();
    }

    void saved() {
        this.changed = false;
    }

    void requireNewSessionId() {
        this.changed = true;
    }
}