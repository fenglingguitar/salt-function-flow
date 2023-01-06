package org.salt.function.flow.test.thread;

import org.salt.function.flow.thread.IThreadContent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestThreadContent implements IThreadContent {

    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Override
    public Object getThreadContent() {
        return threadLocal.get();
    }

    @Override
    public void setThreadContent(Object content) {
        threadLocal.set((Map<String, Object>) content);
    }

    @Override
    public void cleanThreadContent() {
        threadLocal.remove();
    }

    public synchronized void put(String key, Object value) {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<>());
        }
        threadLocal.get().put(key, value);
    }

    public synchronized Object get(String key) {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<>());
        }
        return threadLocal.get().get(key);
    }
}
