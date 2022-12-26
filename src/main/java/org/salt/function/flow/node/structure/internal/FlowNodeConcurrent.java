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
import org.salt.function.flow.thread.TheadHelper;
import org.salt.function.flow.util.FlowUtil;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FlowNodeConcurrent<P> extends FlowNodeStructure<P> {

    private TheadHelper theadHelper;

    public FlowNodeConcurrent(TheadHelper theadHelper) {
        this.theadHelper = theadHelper;
    }

    @Override
    public P doProcess(IContextBus iContextBus) {
        if (CollectionUtils.isEmpty(infoList)) {
            return null;
        }
        ContextBus contextBus = ((ContextBus) iContextBus);
        CountDownLatch countDownLatch = null;
        if (getModel()) {
            long size = infoList.stream().filter(nodeInfo -> FlowUtil.isExe(contextBus, nodeInfo)).count();
            countDownLatch = new CountDownLatch((int) size);
        }
        for (Info info : infoList) {
            if(!FlowUtil.isExe(contextBus, info)) {
                continue;
            }
            CountDownLatch finalCountDownLatch = countDownLatch;
            theadHelper.getExecutor().submit(theadHelper.getDecorator(() -> {
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
                } finally {
                    if (getModel()) {
                        finalCountDownLatch.countDown();
                    }
                    ContextBus.cleanNodeInfo(info);
                }
            }));
        }
        if (getModel()) {
            try {
                boolean isTimeout = !countDownLatch.await(theadHelper.getTimeout(), TimeUnit.MILLISECONDS);
                if (result != null) {
                    return result.handle(iContextBus, isTimeout);
                }
            } catch (InterruptedException e) {
                contextBus.putPassException(nodeId, e);
            }
        }
        return null;
    }

    protected boolean getModel() {
        return true;
    }
}
