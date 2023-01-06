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

import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.Info;
import org.salt.function.flow.config.IFlowInit;
import org.salt.function.flow.demo.train.param.Passenger;
import org.salt.function.flow.demo.train.param.Station;

public class TrainFlowInit implements IFlowInit {

    /**
     * Build and register all flow
     */
    @Override
    public void configure(FlowEngine flowEngine) {

        flowEngine.builder().id("train_ticket")
                .next("base_price")
                .next(
                        Info.builder().include("age < 14").id("child_ticket").build(),
                        Info.builder().include("age >= 14").id("adult_tickt").build())
                .result("ticket_result")
                .build();

        flowEngine.builder().id("train_ticket_match")
                .next("base_price")
                .next(
                        Info.builder().match(iContextBus -> ((Passenger) iContextBus.getParam()).getAge() < 14).id("child_ticket").build(),
                        Info.builder().match(iContextBus -> ((Passenger) iContextBus.getParam()).getAge() >= 14).id("adult_tickt").build())
                .result("ticket_result")
                .build();

        flowEngine.builder().id("train_ticket_input")
                .next(
                        Info.builder().id("base_price")
                                .input(iContextBus -> {
                                    Passenger passenger = (Passenger) iContextBus.getParam();
                                    return Station.builder().from(passenger.getFrom()).to(passenger.getTo()).build();
                                })
                                .output((iContextBus, result) -> {
                                    System.out.println("base_price return " + result);
                                    return result;
                                }).build())
                .next(
                        Info.builder().include("age < 14").id("child_ticket").build(),
                        Info.builder().include("age >= 14").id("adult_tickt").build())
                .result("ticket_result")
                .build();
    }
}
