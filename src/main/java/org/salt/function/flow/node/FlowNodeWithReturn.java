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

package org.salt.function.flow.node;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.salt.function.flow.Info;
import org.salt.function.flow.context.ContextBus;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.util.FlowUtil;

@Slf4j
public abstract class FlowNodeWithReturn<P> extends FlowNode {

    public void process(IContextBus iContextBus) {
        ContextBus<Object, Object> contextBus = ((ContextBus) iContextBus);
        P result = doProcess(iContextBus);
        if (result != null) {
            String idTmp = nodeId;
            Object adapterResult = null;
            Info info = ContextBus.getNodeInfo(FlowUtil.getNodeInfoKey(nodeId));
            if (info != null) {
                if (info.output != null) {
                    adapterResult = info.output.apply(contextBus, result);
                }
                if (StringUtils.isNotEmpty(info.idAlias)) {
                    idTmp = info.idAlias;
                }
            }
            if (adapterResult != null) {
                contextBus.putPassResult(idTmp, adapterResult);
            } else {
                contextBus.putPassResult(idTmp, result);
            }
            ContextBus.putLastNodeId(idTmp);
        }
    }

    public abstract P doProcess(IContextBus iContextBus);
}
