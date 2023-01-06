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

package org.salt.function.flow.test.thread.node;

import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.register.NodeIdentity;
import org.salt.function.flow.test.thread.TestThreadContent;
import org.springframework.beans.factory.annotation.Autowired;

@NodeIdentity(nodeId = "demo_bit_left")
public class DemoBitLeftNode extends FlowNodeWithReturn<Integer> {

    @Autowired
    TestThreadContent testThreadContent;

    @Override
    public Integer doProcess(IContextBus iContextBus) {
        System.out.println("TheadLocal: " + testThreadContent.get("test"));
        Integer preResult = (Integer) iContextBus.getPreResult();
        Integer result = preResult << 1;
        System.out.println("DemoBitLeftNode: " + preResult + "<<1=" + result);
        return result;
    }
}
