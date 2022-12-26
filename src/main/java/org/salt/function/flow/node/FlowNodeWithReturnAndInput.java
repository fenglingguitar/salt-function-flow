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

package org.salt.function.flow.node;

import lombok.extern.slf4j.Slf4j;
import org.salt.function.flow.Info;
import org.salt.function.flow.context.ContextBus;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.util.FlowUtil;

@Slf4j
public abstract class FlowNodeWithReturnAndInput<P, I> extends FlowNodeWithReturn<P> {

    @Override
    public P doProcess(IContextBus iContextBus) {
        ContextBus<Object, Object> contextBus = ((ContextBus<Object, Object>) iContextBus);
        Info info = ContextBus.getNodeInfo(FlowUtil.getNodeInfoKey(nodeId));
        I input = null;
        if (info != null && info.input != null) {
            input = (I) info.input.apply(contextBus);
        }
        return doProcessWithInput(contextBus, input);
    }

    public abstract P doProcessWithInput(IContextBus iContextBus, I input);
}
