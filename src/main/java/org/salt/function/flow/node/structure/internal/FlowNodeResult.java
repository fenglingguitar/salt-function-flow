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
import org.salt.function.flow.context.ContextBus;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.structure.FlowNodeStructure;

@Slf4j
public class FlowNodeResult<P> extends FlowNodeStructure<P> {

    private String nodeIdResult;

    @Override
    public P doProcess(IContextBus iContextBus) {
        ContextBus contextBus = (ContextBus) iContextBus;
        P result = (P) flowNodeManager.execute(
                nodeIdResult,
                s -> {
                    if (!(s instanceof FlowNodeWithReturn)) {
                        throw new RuntimeException("node is not ProcessNodeWithReturn");
                    }
                    return ((FlowNodeWithReturn)s).doProcess(contextBus);
                });
        if (result != null) {
            contextBus.setResult(result);
        }
        return result;
    }

    public void getNodeIdResult(String nodeIdResult) {
        this.nodeIdResult = nodeIdResult;
    }
}
