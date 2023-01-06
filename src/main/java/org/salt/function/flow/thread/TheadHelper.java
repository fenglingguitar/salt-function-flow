/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.salt.function.flow.thread;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.salt.function.flow.Info;
import org.salt.function.flow.util.FlowUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

@Slf4j
@Data
@Builder
public class TheadHelper {

    private IThreadContent threadContent;

    private ExecutorService executor;

    private long timeout;

    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    public static <P> void putThreadLocal(String key, P value) {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<>());
        }
        threadLocal.get().put(key, value);
    }

    public static <P> P getThreadLocal(String key) {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<>());
        }
        return (P) threadLocal.get().get(key);
    }

    public static void clean() {
        threadLocal.set(null);
    }

    public Runnable getDecoratorSync(Runnable runnable, Info info) {
        return () -> {
            putThreadLocal(FlowUtil.getNodeInfoKey(info.id) , info);
            try {
                runnable.run();
            } finally {
                TheadHelper.putThreadLocal(FlowUtil.getNodeInfoKey(info.id), null);
            }
        };
    }

    public Runnable getDecoratorAsync(Runnable runnable, Info info) {
        Map<String, Object> map = new HashMap<>(threadLocal.get());
        final Object content = getThreadContent();
        return () -> {
            threadLocal.set(map);
            setThreadContent(content);
            putThreadLocal(FlowUtil.getNodeInfoKey(info.id) , info);
            try {
                runnable.run();
            } finally {
                TheadHelper.putThreadLocal(FlowUtil.getNodeInfoKey(info.id), null);
                cleanThreadContent();
                threadLocal.set(null);
            }
        };
    }

    public Callable getDecoratorAsync(Callable callable, Info info) {
        Map<String, Object> map = new HashMap<>(threadLocal.get());
        final Object content = getThreadContent();
        return () -> {
            threadLocal.set(map);
            setThreadContent(content);
            putThreadLocal(FlowUtil.getNodeInfoKey(info.id) , info);
            try {
                return callable.call();
            } finally {
                TheadHelper.putThreadLocal(FlowUtil.getNodeInfoKey(info.id), null);
                cleanThreadContent();
                threadLocal.set(null);
            }
        };
    }

    private Object getThreadContent() {
        if (threadContent != null) {
            return threadContent.getThreadContent();
        }
        return null;
    }
    private void setThreadContent(Object content) {
        if (threadContent != null) {
            threadContent.setThreadContent(content);
        }
    }
    private void cleanThreadContent() {
        if (threadContent != null) {
            threadContent.cleanThreadContent();
        }
    }
}
