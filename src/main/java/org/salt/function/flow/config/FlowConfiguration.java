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

package org.salt.function.flow.config;

import lombok.extern.slf4j.Slf4j;
import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.node.register.FlowNodeManager;
import org.salt.function.flow.node.register.FlowNodeScanner;
import org.salt.function.flow.thread.IThreadContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "salt.function.flow.enable", havingValue = "true", matchIfMissing = true)
public class FlowConfiguration {

    @Value("${salt.function.flow.threadpool.coreSize:100}")
    private int coreSize;
    @Value("${salt.function.flow.threadpool.maxSize:300}")
    private int maxSize;
    @Value("${salt.function.flow.threadpool.queueCapacity:1024}")
    private int queueCapacity;
    @Value("${salt.function.flow.threadpool.keepAlive:60}")
    private int keepAlive;

    @Bean
    public FlowNodeManager register() {
        return new FlowNodeManager();
    }

    @Bean(initMethod = "init")
    public FlowNodeScanner extensionScanner(FlowNodeManager flowNodeManager) {
        return new FlowNodeScanner(flowNodeManager);
    }

    @Bean
    @ConditionalOnMissingBean(name = "flowThreadPool")
    public ThreadPoolTaskExecutor flowThreadPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(coreSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAlive);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix("thread-pool-flow-");
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            threadPoolTaskExecutor.shutdown();
        }));
        return threadPoolTaskExecutor;
    }

    @Bean
    public FlowEngine flowEngine(FlowNodeManager flowNodeManager, @Autowired(required = false) IFlowInit flowInit,
                                 ThreadPoolTaskExecutor flowThreadPool, @Autowired(required = false) IThreadContent threadContent) {
        return new FlowEngine(flowNodeManager, flowInit, flowThreadPool, threadContent);
    }
}
