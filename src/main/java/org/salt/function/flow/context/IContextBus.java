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

import java.util.Map;

public interface IContextBus<T, R> {

    /**
     * Get flow execution parameters
     */
    T getParam();

    /**
     * Get flow execution result
     */
    R getResult();

    /**
     * Put additional transmission context information
     */
    <P> void putTransmitInfo(String key, P content);

    /**
     * Get additional transmission context information
     */
    <P> P getTransmitInfo(String key);

    /**
     * Get the parameters of flow condition judgment
     */
    Map<String, Object> getConditionMap();
    /**
     * Add the parameters of flow condition judgment
     */
    <P> void addCondition(String key, P value);

    /**
     * Get the execution result of the last node, which may return null
     */
    <P> P getPreResult();

    /**
     * Get the execution result of any node
     */
    <P> P getPassResult(String nodeId);

    /**
     * Get the execution exception of any node
     */
    Exception getPassException(String nodeId);

    /**
     * Get flow ID
     */
    String getFlowId();

    /**
     * Get flow execution Instance ID
     */
    String getRuntimeId();

    /**
     * Stop flow execution instance
     */
    void stopProcess();

    /**
     * Rollback flow execution instance
     */
    void rollbackProcess();
}
