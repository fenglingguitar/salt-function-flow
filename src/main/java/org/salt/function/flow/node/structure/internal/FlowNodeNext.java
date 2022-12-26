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
public class FlowNodeNext<P> extends FlowNodeStructure<P> {

    @Override
    public P doProcess(IContextBus iContextBus) {
        if (CollectionUtils.isEmpty(infoList)) {
            return null;
        }

        ContextBus contextBus = ((ContextBus) iContextBus);
        for (Info info : infoList) {
            if (!FlowUtil.isExe(contextBus, info)) {
                continue;
            }
            try {
                ContextBus.setNodeInfo(info);
                if (isFlowNode(info.id)) {
                    flowNodeManager.executeVoid(info.id,
                            flowNode -> {
                                flowNode.process(contextBus);
                                contextBus.roolbackExec(flowNode);
                            });
                } else {
                    ContextBus contextBusChild = contextBus.copy(info.id);
                    Object result = flowEngine.execute(contextBusChild);
                    if (result != null) {
                        contextBus.putPassResult(info.id, result);
                    }
                }
            } catch (Exception e) {
                contextBus.putPassException(info.id, e);
                if (getModel()) {
                    throw e;
                }
            } finally {
                ContextBus.cleanNodeInfo(info);
            }
            if (getModel()) {
                return null;
            }
        }
        if (result != null) {
            return result.handle(iContextBus, false);
        }
        return null;
    }

    protected boolean getModel() {
        return true;
    }
}
