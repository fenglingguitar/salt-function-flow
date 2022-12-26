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
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.structure.FlowNodeStructure;
import org.salt.function.flow.thread.TheadHelper;
import org.salt.function.flow.util.FlowUtil;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.Future;

@Slf4j
public class FlowNodeFuture<P> extends FlowNodeStructure<P> {

    protected TheadHelper theadHelper;

    public FlowNodeFuture(TheadHelper theadHelper) {
        this.theadHelper = theadHelper;
    }

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
            Future<?> future = theadHelper.getExecutor().submit(theadHelper.getDecorator(() -> {
                try {
                    ContextBus.setNodeInfo(info);
                    if (isFlowNode(info.id)) {
                        return flowNodeManager.execute(info.id,
                                flowNode -> {
                                    Object object = ((FlowNodeWithReturn) flowNode).doProcess(contextBus);
                                    contextBus.roolbackExec(flowNode);
                                    return object;
                                });
                    } else {
                        ContextBus contextBusChild = contextBus.copy(info.id);
                        return flowEngine.execute(contextBusChild);
                    }
                } catch (Exception e) {
                    contextBus.putPassException(info.id, e);
                } finally {
                    ContextBus.cleanNodeInfo(info);
                }
                return null;
            }));
            contextBus.putPassResult(info.id, future);
        }
        return null;
    }
}
