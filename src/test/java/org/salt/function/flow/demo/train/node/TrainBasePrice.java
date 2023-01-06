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

package org.salt.function.flow.demo.train.node;

import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.demo.train.param.Station;
import org.salt.function.flow.node.FlowNodeWithReturnAndInput;
import org.salt.function.flow.node.register.NodeIdentity;

@NodeIdentity(nodeId = "base_price")
public class TrainBasePrice extends FlowNodeWithReturnAndInput<Integer, Station> {

    @Override
    public Integer doProcessWithInput(IContextBus iContextBus, Station station) {
        if (station != null) {
            System.out.println("Passengers travel from " + station.getFrom() + " to " + station.getTo());
        }
        System.out.println("Calculate the basic train ticket price 300");
        return 300;
    }
}
