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

package org.salt.function.flow.demo.train;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.TestApplication;
import org.salt.function.flow.demo.train.param.Passenger;
import org.salt.function.flow.demo.train.param.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@SpringBootConfiguration
public class TrainTest {

    @Autowired
    FlowEngine flowEngine;

    @Test
    public void testTrainTicket() {
        System.out.println("train_ticket test: ");
        Passenger passenger = Passenger.builder().name("jack").age(12).build();
        Ticket ticket = flowEngine.execute("train_ticket", passenger);
        System.out.println("train_ticket result: " + ticket.getPrice());
        Assert.assertTrue(ticket != null && ticket.getPrice() == 150);
    }

    @Test
    public void testTrainTicketCondition() {
        System.out.println("train_ticket test: ");
        Passenger passenger = Passenger.builder().name("jack").age(12).build();
        Map<String, Object> condition = new HashMap();
        condition.put("age", 12);
        Ticket ticket = flowEngine.execute("train_ticket", passenger, condition);
        System.out.println("train_ticket result: " + ticket.getPrice());
        Assert.assertTrue(ticket != null && ticket.getPrice() == 150);
    }

    @Test
    public void testTrainTicketMatch() {
        System.out.println("train_ticket_match test: ");
        Passenger passenger = Passenger.builder().name("jack").age(12).build();
        Ticket ticket = flowEngine.execute("train_ticket_match", passenger);
        System.out.println("train_ticket_match result: " + ticket.getPrice());
        Assert.assertTrue(ticket != null && ticket.getPrice() == 150);
    }

    @Test
    public void testTrainTicketInput() {
        System.out.println("train_ticket_input test: ");
        Passenger passenger = Passenger.builder().name("jack").age(12).from("Beijing").to("Shanghai").build();
        Ticket ticket = flowEngine.execute("train_ticket_input", passenger);
        System.out.println("train_ticket_input result: " + ticket.getPrice());
        Assert.assertTrue(ticket != null && ticket.getPrice() == 150);
    }
}
