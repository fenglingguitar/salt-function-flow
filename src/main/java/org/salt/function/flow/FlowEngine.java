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

package org.salt.function.flow;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.salt.function.flow.config.IFlowInit;
import org.salt.function.flow.context.ContextBus;
import org.salt.function.flow.node.FlowNode;
import org.salt.function.flow.node.IResult;
import org.salt.function.flow.node.register.FlowNodeManager;
import org.salt.function.flow.node.structure.FlowNodeStructure;
import org.salt.function.flow.node.structure.internal.*;
import org.salt.function.flow.thread.IThreadContent;
import org.salt.function.flow.thread.TheadHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
public class FlowEngine implements InitializingBean {

    protected FlowNodeManager flowNodeManager;

    protected IFlowInit flowInit;

    protected ThreadPoolTaskExecutor flowThreadPool;

    protected IThreadContent threadContent;

    private static ConcurrentMap<String, FlowInstance> processInstanceMap = new ConcurrentHashMap<>();

    public FlowEngine(FlowNodeManager flowNodeManager, IFlowInit flowInit, ThreadPoolTaskExecutor flowThreadPool, IThreadContent threadContent) {
        this.flowNodeManager = flowNodeManager;
        this.flowInit = flowInit;
        this.flowThreadPool = flowThreadPool;
        this.threadContent = threadContent;
    }

    @Override
    public void afterPropertiesSet() {
        if (flowInit != null) {
            flowInit.configure(this);
        }
    }

    public <T, R> R execute(String flowId, T param) {
        return execute(flowId, param, null);
    }

    public <T, R> R execute(String flowId, T param, Map<String, Object> transmitMap) {
        return execute(flowId, param, transmitMap, null);
    }

    public <T, R> R execute(String flowId, T param, Map<String, Object> transmitMap, Map<String, Object> conditionMap) {
        FlowInstance flowInstance = processInstanceMap.get(flowId);
        if (flowInstance != null) {
            return flowInstance.execute(param, transmitMap, conditionMap);
        }
        throw new RuntimeException("no have this process");
    }

    public <T, R> R execute(FlowInstance flowInstance, T param) {
        return execute(flowInstance, param, null);
    }

    public <T, R> R execute(FlowInstance flowInstance, T param, Map<String, Object> transmitMap) {
        return flowInstance.execute(param, transmitMap, null);
    }

    public <T, R> R execute(FlowInstance flowInstance, T param, Map<String, Object> transmitMap, Map<String, Object> conditionMap) {
        return flowInstance.execute(param, transmitMap, conditionMap);
    }

    public <T, R> R execute(ContextBus<T, R> contextBus) {
        FlowInstance flowInstance = processInstanceMap.get(contextBus.getFlowId());
        if (flowInstance != null) {
            return flowInstance.execute(contextBus);
        }
        throw new RuntimeException("no have this process");
    }

    public Builder builder() {
        return new Builder(this);
    }

    public Builder branch() {
        String flowId = "branch-" + UUID.randomUUID().toString().replaceAll("-", "");
        return new Builder(this).id(flowId);
    }

    public static class Builder {
        String flowId;
        List<String> idList;
        private FlowEngine flowEngine;

        public Builder(FlowEngine flowEngine) {
            this.flowEngine = flowEngine;
        }

        public Builder id(String flowId) {
            this.flowId = flowId;
            idList = new ArrayList<>();
            return this;
        }

        //next
        public Builder next(String... ids) {
            return next(toInfos(ids));
        }

        public Builder next(Info... infos) {
            return next(InitParam.builder().infos(infos).build());
        }

        public Builder next(IResult result, Info... infos) {
            return next(InitParam.builder().infos(infos).result(result).build());
        }

        private Builder next(InitParam initParam) {
            init(tempName("next", initParam.idTmp), new FlowNodeNext<>(), initParam);
            return this;
        }

        //all
        public Builder all(String... ids) {
            return all(toInfos(ids));
        }

        public Builder all(IResult result, String... ids) {
            return all(result, toInfos(ids));
        }

        public Builder all(Info... infos) {
            return all(InitParam.builder().infos(infos).build());
        }

        public Builder all(IResult result, Info... infos) {
            return all(InitParam.builder().infos(infos).result(result).build());
        }

        private Builder all(InitParam initParam) {
            init(tempName("all", initParam.idTmp), new FlowNodeAll<>(), initParam);
            return this;
        }

        //concurrent
        public Builder concurrent(String... ids) {
            return concurrent(toInfos(ids));
        }

        public Builder concurrent(long timeout, String... ids) {
            return concurrent(timeout, toInfos(ids));
        }

        public Builder concurrent(IResult result, String... ids) {
            return concurrent(result, toInfos(ids));
        }

        public Builder concurrent(IResult result, long timeout, String... ids) {
            return concurrent(result, timeout, toInfos(ids));
        }

        public Builder concurrent(IResult result, long timeout, ExecutorService isolate, String... ids) {
            return concurrent(result, timeout, isolate, toInfos(ids));
        }

        public Builder concurrent(Info... infos) {
            return concurrent(InitParam.builder().infos(infos).build());
        }

        public Builder concurrent(long timeout, Info... infos) {
            return concurrent(InitParam.builder().infos(infos).timeout(timeout).build());
        }

        public Builder concurrent(IResult result, Info... infos) {
            return concurrent(InitParam.builder().infos(infos).result(result).build());
        }

        public Builder concurrent(IResult result, long timeout, Info... infos) {
            return concurrent(InitParam.builder().infos(infos).result(result).timeout(timeout).build());
        }

        public Builder concurrent(IResult result, long timeout, ExecutorService isolate, Info... infos) {
            return concurrent(InitParam.builder().infos(infos).result(result).timeout(timeout).isolate(isolate).build());
        }

        private Builder concurrent(InitParam initParam) {
            init(tempName("concurrent", initParam.idTmp), new FlowNodeConcurrent<>(creatThreadHelper(initParam)), initParam);
            return this;
        }

        //notify
        public Builder notify(String... ids) {
            return notify(toInfos(ids));
        }

        public Builder notify(ExecutorService isolate, String... ids) {
            return notify(isolate, toInfos(ids));
        }

        public Builder notify(Info... infos) {
            return notify(InitParam.builder().infos(infos).build());
        }

        public Builder notify(ExecutorService isolate, Info... infos) {
            return notify(InitParam.builder().infos(infos).isolate(isolate).build());
        }

        private Builder notify(InitParam initParam) {
            init(tempName("notify", initParam.idTmp), new FlowNodeNotify<>(creatThreadHelper(initParam)), initParam);
            return this;
        }

        //future
        public Builder future(String... ids) {
            return future(toInfos(ids));
        }

        public Builder future(ExecutorService isolate, String... ids) {
            return future(isolate, toInfos(ids));
        }

        public Builder future(Info... infos) {
            return future(InitParam.builder().infos(infos).build());
        }

        public Builder future(ExecutorService isolate, Info... infos) {
            return future(InitParam.builder().infos(infos).isolate(isolate).build());
        }

        private Builder future(InitParam initParam) {
            init(tempName("future", initParam.idTmp), new FlowNodeFuture(creatThreadHelper(initParam)), initParam);
            return this;
        }

        //wait
        public Builder wait(String... ids) {
            return wait(toInfos(ids));
        }

        public Builder wait(IResult result, String... ids) {
            return wait(result, toInfos(ids));
        }

        public Builder wait(long timeout, String... ids) {
            return wait(timeout, toInfos(ids));
        }

        public Builder wait(IResult result, long timeout, String... ids) {
            return wait(result, timeout, toInfos(ids));
        }

        public Builder wait(Info... infos) {
            return wait(InitParam.builder().infos(infos).build());
        }

        public Builder wait(IResult result, Info... infos) {
            return wait(InitParam.builder().infos(infos).result(result).build());
        }

        public Builder wait(long timeout, Info... infos) {
            return wait(InitParam.builder().infos(infos).timeout(timeout).build());
        }

        public Builder wait(IResult result, long timeout, Info... infos) {
            return wait(InitParam.builder().infos(infos).result(result).timeout(timeout).build());
        }

        private Builder wait(InitParam initParam) {
            init(tempName("wait", initParam.idTmp), new FlowNodeWait<>(initParam.timeout), initParam);
            return this;
        }

        //result
        public Builder result(String nodeId) {
            return result(InitParam.builder().id(nodeId).build());
        }

        private Builder result(InitParam initParam) {
            FlowNodeResult flowNodeResult = new FlowNodeResult<>();
            flowNodeResult.getNodeIdResult(initParam.id);
            init(tempName("result", initParam.idTmp), flowNodeResult, initParam);
            return this;
        }

        public String build() {
            check();
            if (processInstanceMap.containsKey(flowId)) {
                throw new RuntimeException("flow already exists. flowId:" + flowId);
            }
            processInstanceMap.put(flowId, new FlowInstance(flowId, idList, flowEngine));
            return flowId;
        }

        public FlowInstance buildDynamic() {
            check();
            return new FlowInstance(flowId, idList, flowEngine);
        }

        private void check() {
            if (StringUtils.isEmpty(flowId)) {
                throw new RuntimeException("flow flowId is empty.");
            }
            if (CollectionUtils.isEmpty(idList)) {
                throw new RuntimeException("flow node list is empty.");
            }
        }

        private String tempName(String type, String idTmp) {
            if (StringUtils.isNotEmpty(idTmp)) {
                return idTmp;
            } else {
                return StringUtils.join(type, "-", UUID.randomUUID().toString().replaceAll("-", ""));
            }
        }

        private Info[] toInfos(String... ids) {
            return Arrays.stream(ids)
                    .map(id -> Info.builder().id(id).build())
                    .collect(Collectors.toList())
                    .toArray(new Info[ids.length]);
        }

        private void init(String id, FlowNode flowNode, InitParam initParam) {
            flowNode.setNodeId(id);
            if (flowNode instanceof FlowNodeStructure) {
                FlowNodeStructure flowNodeStructure = (FlowNodeStructure) flowNode;
                flowNodeStructure.setFlowEngine(flowEngine);
                flowNodeStructure.setFlowNodeManager(flowEngine.flowNodeManager);
                flowNodeStructure.setResult(initParam.result);
                if (initParam.infos != null) {
                    flowNodeStructure.setNodeInfoList(Arrays.asList(initParam.infos));
                }
            }
            flowEngine.flowNodeManager.doRegistration(flowNode);
            idList.add(id);
        }

        private TheadHelper creatThreadHelper(InitParam initParam) {
            return TheadHelper.builder()
                        .timeout(initParam.timeout)
                        .threadContent(flowEngine.threadContent)
                        .executor(
                                initParam.isolate != null ?
                                        initParam.isolate :
                                        flowEngine.flowThreadPool.getThreadPoolExecutor()
                        ).build();
        }
    }

    @lombok.Builder
    @lombok.Data
    public static class InitParam {
        private static long MAP_WAIT_TIMEOUT = 3000;
        String id;
        @lombok.Builder.Default
        String idTmp = "";
        @lombok.Builder.Default
        long timeout = MAP_WAIT_TIMEOUT;
        IResult result;
        ExecutorService isolate;
        Info[] infos;
    }
}
