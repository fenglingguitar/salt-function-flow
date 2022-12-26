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

package org.salt.function.flow.node.structure.internal;

import lombok.extern.slf4j.Slf4j;
import org.salt.function.flow.Info;
import org.salt.function.flow.context.ContextBus;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.structure.FlowNodeStructure;
import org.salt.function.flow.util.FlowUtil;
import org.springframework.util.CollectionUtils;

@Slf4j
public class FlowNodeWait<P> extends FlowNodeStructure<P> {

    private long timeout;

    public FlowNodeWait(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public P doProcess(IContextBus iContextBus) {
        if (CollectionUtils.isEmpty(infoList)) {
            return null;
        }
        ContextBus contextBus = ((ContextBus) iContextBus);
        long lastTimeout = timeout;
        for (Info info : infoList) {
            if (!FlowUtil.isExe(contextBus, info)) {
                continue;
            }
            if (lastTimeout <= 0) {
                contextBus.putPassException(info.id, new RuntimeException("beyond maxTimeout"));
                return result(contextBus, true);
            }
            long start = System.currentTimeMillis();
            try {
                Object result = (P) contextBus.getPassResult(info.id, lastTimeout);
                if (result != null) {
                    contextBus.putPassResult(info.id, result);
                }
            } catch (Exception e) {
                contextBus.putPassException(info.id, e);
            }
            lastTimeout -= System.currentTimeMillis() - start;
        }
        return result(contextBus, lastTimeout <= 0);
    }

    public P result(IContextBus iContextBus, boolean isTimeout) {
        if (result != null) {
            return result.handle(iContextBus, isTimeout);
        }
        return null;
    }
}
