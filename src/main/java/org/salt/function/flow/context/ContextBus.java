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

package org.salt.function.flow.context;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.salt.function.flow.Info;
import org.salt.function.flow.node.IFlowNode;
import org.salt.function.flow.thread.TheadHelper;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

@Builder
@Slf4j
public class ContextBus<T, R> implements IContextBus<T, R> {

    private static String LAST_NODE_ID_KEY = "last_node_id_key";

    /**
     * Flow call parameters
     */
    private T param;

    /**
     * Flow call result
     */
    private R result;

    /**
     * Store additional transmission context information
     */
    private ConcurrentMap<String, Object> transmitMap;

    /**
     * Store the returned results of execution nodes
     */
    private ConcurrentMap<String, Object> passResultMap;

    /**
     * Store the exception information of each node (asynchronous execution node)
     */
    private ConcurrentMap<String, Exception> passExceptionMap;

    /**
     * Store the parameters involved in condition judgment, initially flow param
     */
    private ConcurrentMap<String, Object> conditionMap;

    /**
     * Flow ID
     */
    private String flowId;

    /**
     * Flow execution instance ID
     */
    private String runtimeId;

    /**
     * Flow stop flag
     */
    private volatile boolean stopFlag;

    /**
     * Flow rollback flag
     */
    private boolean rollbackFlag;
    /**
     * Executed node list
     */
    private Deque<IFlowNode> rollbackList;

    /**
     * Thread delivery cache
     */
    private ConcurrentMap<String, Function<IContextBus<T, R>, ?>> functionMap;


    @Override
    public T getParam() {
        return param;
    }

    @Override
    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }

    @Override
    public <P> void putTransmitInfo(String key, P content) {
        transmitMap.put(key, content);
    }

    @Override
    public <P> P getTransmitInfo(String key) {
        return (P) transmitMap.get(key);
    }

    @Override
    public <P> void addCondition(String key, P value) {
        if (key == null || value == null) {
            return;
        }
        if (conditionMap.containsKey(key)) {
            log.warn("{} process addCondition param repeat. key:{}, value:{}, traceId:{}", flowId, key, value, runtimeId);
        }
        conditionMap.put(key, value);
    }

    @Override
    public Map<String, Object> getConditionMap() {
        return conditionMap;
    }

    @Override
    public <P> P getPassResult(String nodeId) {
        return (P) passResultMap.get(nodeId);
    }

    public <P> void putPassResult(String nodeId, P result) {
        passResultMap.put(nodeId, result);
    }

    public <P> void removePassResult(String nodeId) {
        passResultMap.remove(nodeId);
    }

    public static void putLastNodeId(String nodeId) {
        TheadHelper.putThreadLocal(LAST_NODE_ID_KEY, nodeId);
    }
    public static String getLastnodeId() {
        return TheadHelper.getThreadLocal(LAST_NODE_ID_KEY);
    }
    public static void clean() {
        TheadHelper.clean();
    }

    public <P> P getPassResult(String nodeId, long timeout) throws InterruptedException, ExecutionException, TimeoutException {
        Object result = passResultMap.get(nodeId);
        if (result != null && result instanceof Future) {
            return ((Future<P>) result).get(timeout, TimeUnit.MILLISECONDS);
        }
        throw new RuntimeException("node is not Future");
    }

    @Override
    public Exception getPassException(String nodeId) {
        return passExceptionMap.get(nodeId);
    }

    public void putPassException(String nodeId, Exception e) {
        passExceptionMap.put(nodeId, e);
    }

    @Override
    public String getFlowId() {
        return flowId;
    }

    @Override
    public String getRuntimeId() {
        return runtimeId;
    }

    @Override
    public <P> P getPreResult() {
        String nodeId = TheadHelper.getThreadLocal(LAST_NODE_ID_KEY);
        if (StringUtils.isNotEmpty(nodeId)) {
            log.debug("{} process getPreResult. nodeId:{}, traceId:{}", flowId, nodeId, runtimeId);
            return (P) passResultMap.get(nodeId);
        }
        return null;
    }

    public ContextBus<T, R> copy(String processId) {
        ContextBus<T, R> contextBus = ContextBus.<T, R>builder()
                .param(param)
                .conditionMap(conditionMap)
                .passResultMap(passResultMap)
                .passExceptionMap(passExceptionMap)
                .transmitMap(transmitMap)
                .flowId(processId)
                .runtimeId(runtimeId)
                .rollbackList(rollbackList)
                .functionMap(functionMap)
                .build();
        return contextBus;
    }

    public ContextBus<T, R> copyNotify(String processId) {
        ContextBus<T, R> contextBus = ContextBus.<T, R>builder()
                .param(param)
                .conditionMap(conditionMap)
                .passResultMap(passResultMap)
                .passExceptionMap(passExceptionMap)
                .transmitMap(transmitMap)
                .flowId(processId)
                .runtimeId(runtimeId)
                .rollbackList(new LinkedList<>())
                .functionMap(functionMap)
                .build();
        return contextBus;
    }

    public static <T, R> ContextBus<T, R> create(String flowId, T param, Map<String, Object> conditionMap) {
        Map<String, Object> conditionTmp = conditionMap;
        if (conditionTmp == null) {
            try {
                if (param.getClass().isPrimitive()
                        || param instanceof Number
                        || param instanceof Boolean
                        || param instanceof String
                        //|| param.getClass().isArray()
                        //|| param instanceof Collection
                ) {
                    conditionTmp = new HashMap<>();
                    conditionTmp.put("param", param);
                } else {
                    conditionTmp = BeanUtils.describe(param);
                    conditionTmp.entrySet().removeIf(entry -> Objects.isNull(entry.getValue()));
                }
            } catch (Exception e) {
                throw new RuntimeException("param to conditionMap error");
            }
        }
        ContextBus<T, R> contextBus = ContextBus.<T, R>builder()
                .param(param)
                .conditionMap(conditionTmp != null ? new ConcurrentHashMap<>(conditionTmp) : new ConcurrentHashMap<>())
                .passResultMap(new ConcurrentHashMap<>())
                .passExceptionMap(new ConcurrentHashMap<>())
                .transmitMap(new ConcurrentHashMap<>())
                .flowId(flowId)
                .runtimeId(UUID.randomUUID().toString().replaceAll("-", ""))
                .rollbackList(new LinkedList<>())
                .functionMap(new ConcurrentHashMap<>())
                .build();
        contextBus.putPassResult(flowId, param);
        ContextBus.clean();
        ContextBus.putLastNodeId(flowId);
        return contextBus;
    }

    public void stopProcess() {
        this.stopFlag = true;
    }
    public boolean isStopProcess() {
        return this.stopFlag;
    }

    public synchronized void rollbackProcess() {
        this.rollbackFlag = true;
    }

    public synchronized boolean isRollbackProcess() {
        return this.rollbackFlag;
    }

    public synchronized void roolbackAll() {
        for(int i=rollbackList.size()-1; i>=0; i--) {
            IFlowNode execNode = rollbackList.pop();
            try {
                execNode.rollback(this);
            } catch (Exception e) {
            }
        }
    }

    public synchronized boolean roolbackExec(IFlowNode iFlowNode) {
        if (rollbackFlag) {
            try {
                iFlowNode.rollback(this);
            } catch (Exception e) {
            }
            return true;
        } else {
            rollbackList.push(iFlowNode);
            return false;
        }
    }

    public static Info getNodeInfo(String key) {
        return TheadHelper.getThreadLocal(key);
    }
}
