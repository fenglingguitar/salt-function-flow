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

package org.salt.function.flow.node.register;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.salt.function.flow.context.ContextBus;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.IFlowNode;
import org.salt.function.flow.util.FlowUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
@Slf4j
public class FlowNodeManager {

    private Map<String, IFlowNode> flowNodeMap = new HashMap<>();

    public void doRegistration(IFlowNode iFlowNode) {
        Class<?> nodeClazz = getNodeClazz(iFlowNode);
        NodeIdentity nodeIdentity = nodeClazz.getDeclaredAnnotation(NodeIdentity.class);
        String nodeId;
        if (nodeIdentity != null) {
            nodeId = nodeIdentity.nodeId();
        } else {
            nodeId = iFlowNode.nodeId();
        }
        if (StringUtils.isEmpty(nodeId)) {
            throw new RuntimeException("nodeId or extConfig must not be all null ");
        }
        if (flowNodeMap.containsKey(nodeId)) {
            throw new RuntimeException("repeat node " + iFlowNode.nodeId());
        }
        flowNodeMap.put(nodeId, iFlowNode);
    }

    public IFlowNode getIFlowNode(String nodeId) {
        return flowNodeMap.get(nodeId);
    }

    private Class<?> getNodeClazz(IFlowNode iFlowNode) {
        Object target = null;
        try {
            target = FlowUtil.getTarget(iFlowNode);
        } catch (Exception ex) {
            log.error("", ex);
        }
        if (target == null) {
            throw new RuntimeException("get aop target failed with error :" + iFlowNode.getClass());
        }

        return target.getClass();
    }

    public <R> R execute(IContextBus iContextBus, String nodeId) {
        IFlowNode iFlowNode = flowNodeMap.get(nodeId);
        if (iFlowNode != null && iFlowNode instanceof FlowNodeWithReturn) {
            R result = (R) ((FlowNodeWithReturn) iFlowNode).doProcess(iContextBus);
            ((ContextBus) iContextBus).roolbackExec(iFlowNode);
            return result;
        }
        return null;
    }

    public void executeVoid(IContextBus iContextBus, String nodeId) {
        IFlowNode iFlowNode = flowNodeMap.get(nodeId);
        if (iFlowNode != null) {
            iFlowNode.process(iContextBus);
            ((ContextBus) iContextBus).roolbackExec(iFlowNode);
        }
    }

    public void executeVoidSingle(IContextBus iContextBus, String nodeId) {
        IFlowNode iFlowNode = flowNodeMap.get(nodeId);
        if (iFlowNode != null) {
            iFlowNode.process(iContextBus);
        }
    }

    public <R> R execute(String nodeId, Function<IFlowNode, R> exeFunction) {
        IFlowNode iFlowNode = flowNodeMap.get(nodeId);
        if (iFlowNode != null) {
            return exeFunction.apply(iFlowNode);
        }
        return null;
    }

    public void executeVoid(String nodeId, Consumer<IFlowNode> exeFunction) {
        IFlowNode IFlowNode = flowNodeMap.get(nodeId);
        if (IFlowNode != null) {
            exeFunction.accept(IFlowNode);
        }
    }
}
