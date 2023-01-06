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
import org.salt.function.flow.demo.train.param.Ticket;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.register.NodeIdentity;

@NodeIdentity(nodeId = "ticket_result")
public class TrainTicketResult extends FlowNodeWithReturn<Ticket> {
    @Override
    public Ticket doProcess(IContextBus iContextBus) {
        int price = (int) iContextBus.getPreResult();
        System.out.println("Issue ticket result, price " + price);
        return Ticket.builder().result(true).price(price).build();
    }
}
