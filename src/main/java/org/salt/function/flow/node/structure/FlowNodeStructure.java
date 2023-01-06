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

package org.salt.function.flow.node.structure;

import lombok.extern.slf4j.Slf4j;
import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.Info;
import org.salt.function.flow.context.ContextBus;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.IResult;
import org.salt.function.flow.node.register.FlowNodeManager;
import org.salt.function.flow.thread.TheadHelper;
import org.salt.function.flow.util.FlowUtil;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class FlowNodeStructure<P> extends FlowNodeWithReturn<P> {

    protected FlowEngine flowEngine;

    protected FlowNodeManager flowNodeManager;

    protected IResult<P> result;

    protected List<Info<P, ?>> infoList;

    protected TheadHelper theadHelper;

    public void setFlowEngine(FlowEngine flowEngine) {
        this.flowEngine = flowEngine;
    }

    public void setFlowNodeManager(FlowNodeManager flowNodeManager) {
        this.flowNodeManager = flowNodeManager;
    }

    public void setResult(IResult<P> result) {
        this.result = result;
    }

    public void setNodeInfoList(List<Info<P, ?>> infoList) {
        this.infoList = infoList;
    }

    public void setTheadHelper(TheadHelper theadHelper) {
        this.theadHelper = theadHelper;
    }

    protected boolean isFlowNode(String nodeId) {
        return flowNodeManager.getIFlowNode(nodeId) != null;
    }

    public P doProcess(IContextBus iContextBus) {
        if (CollectionUtils.isEmpty(infoList)) {
            return null;
        }
        List<Info> infoListExe = infoList.stream().filter(info -> FlowUtil.isExe(iContextBus, info)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(infoListExe)) {
            return null;
        }
        return doProcessGateway(iContextBus, infoListExe);
    }

    protected P doProcessGateway(IContextBus iContextBus, List<Info> infoList) {
        return null;
    }

    protected P execute(IContextBus iContextBus, String id) {
        if (isFlowNode(id)) {
            return flowNodeManager.execute(iContextBus, id);
        } else {
            return (P) flowEngine.executeBranch(iContextBus, id);
        }
    }

    protected void executeVoid(IContextBus iContextBus, String id) {
        if (isFlowNode(id)) {
            flowNodeManager.executeVoid(iContextBus, id);
        } else {
            flowEngine.executeBranchVoid(iContextBus, id);
        }
    }

    protected boolean isSuspend(IContextBus iContextBus) {
        return ((ContextBus) iContextBus).isRollbackProcess() || ((ContextBus) iContextBus).isStopProcess();
    }
}
