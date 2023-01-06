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

package org.salt.function.flow.demo.math;

import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.Info;
import org.salt.function.flow.config.IFlowInit;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.IResult;

public class DemoFlowInit implements IFlowInit {

    /**
     * Build and register all flow
     */
    @Override
    public void configure(FlowEngine flowEngine) {

        //simple
        /**
         * Single flow construction
         */
        flowEngine.builder().id("demo_flow").next("demo_add").next("demo_reduce").next("demo_multiply").result("demo_division").build();

        /**
         * Single flow with condition extend construction
         */
        flowEngine.builder().id("demo_flow_extend")
                .next("demo_add")
                .next(
                        Info.builder().include("param <= 30").id("demo_reduce").build(),
                        Info.builder().include("param > 30").id("demo_remainder").build()
                )
                .next("demo_multiply")
                .result("demo_division").build();

        /**
         * Exclusive flow construction
         */
        flowEngine.builder().id("demo_flow_exclusive")
                .next("demo_add")
                .next(
                        Info.builder().include("param <= 30").id("demo_reduce").build(),
                        Info.builder().include("param > 30").id("demo_multiply").build()
                )
                .result("demo_division").build();

        /**
         * Concurrent flow construction
         */
        flowEngine.builder().id("demo_flow_concurrent")
                .next("demo_add")
                .concurrent(new AddResult(), "demo_reduce", "demo_multiply")
                .result("demo_division")
                .build();

        /**
         * Notify flow construction
         */
        flowEngine.builder().id("demo_flow_notify")
                .next("demo_add")
                .notify("demo_reduce")
                .next("demo_multiply")
                .result("demo_division")
                .build();

        /**
         * Future/Wait flow construction
         */
        flowEngine.builder().id("demo_flow_future")
                .next("demo_add")
                .future("demo_reduce", "demo_multiply")
                .wait(new AddResult(), "demo_reduce", "demo_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_flow_future_1")
                .next("demo_add")
                .future("demo_reduce")
                .next("demo_multiply")
                .wait(new AddResult(), "demo_reduce")
                .result("demo_division")
                .build();

        /**
         * Inclusive flow construction
         */
        flowEngine.builder().id("demo_flow_inclusive")
                .next("demo_add")
                .all(
                        Info.builder().include("param > 30").id("demo_reduce").build(),
                        Info.builder().include("param < 50").id("demo_multiply").build()
                )
                .result("demo_division").build();

        /**
         * Inclusive concurrent flow construction
         */
        flowEngine.builder().id("demo_flow_inclusive_concurrent")
                .next("demo_add")
                .concurrent(
                        new AddResult(),
                        Info.builder().include("param > 30").id("demo_reduce").build(),
                        Info.builder().include("param < 50").id("demo_multiply").build()
                )
                .result("demo_division").build();

        //branch
        flowEngine.builder().id("demo_branch_reduce").next("demo_reduce").result("demo_remainder").build();
        flowEngine.builder().id("demo_branch_multiply").next("demo_multiply").result("demo_remainder").build();

        /**
         * Exclusive branch flow construction
         */
        flowEngine.builder().id("demo_branch_exclusive")
                .next("demo_add")
                .next(
                        Info.builder().include("param <= 30").id("demo_branch_reduce").build(),
                        Info.builder().include("param > 30").id("demo_branch_multiply").build()
                )
                .result("demo_division")
                .build();

        /**
         * Concurrent branch flow construction
         */
        flowEngine.builder().id("demo_branch_concurrent")
                .next("demo_add")
                .concurrent(new AddBranchResult(), "demo_branch_reduce", "demo_branch_multiply")
                .result("demo_division")
                .build();

        /**
         * Notify branch flow construction
         */
        flowEngine.builder().id("demo_branch_notify")
                .next("demo_add")
                .notify("demo_branch_reduce")
                .next("demo_branch_multiply")
                .result("demo_division").build();

        /**
         * Future/Wait branch flow construction
         */
        flowEngine.builder().id("demo_branch_future")
                .next("demo_add")
                .future("demo_branch_reduce")
                .next("demo_branch_multiply")
                .wait(new AddBranchResult(), "demo_branch_reduce")
                .result("demo_division").build();

        /**
         * Inclusive branch flow construction
         */
        flowEngine.builder().id("demo_branch")
                .next("demo_add")
                .all("demo_branch_reduce", "demo_branch_multiply")
                .result("demo_division")
                .build();

        /**
         * Nested branch flow construction
         */
        flowEngine.builder().id("demo_branch_nested")
                .next("demo_add")
                .all(
                        flowEngine.builder().id("nested_1").next("demo_reduce").result("demo_remainder").build(),
                        flowEngine.builder().id("nested_2").next("demo_multiply").result("demo_remainder").build())
                .result("demo_division")
                .build();

        /**
         * Nested branch flow with anonymous branch construction
         */
        flowEngine.builder().id("demo_branch_anonymous")
                .next("demo_add")
                .all(
                        flowEngine.branch().next("demo_reduce").result("demo_remainder").build(),
                        flowEngine.branch().next("demo_multiply").result("demo_remainder").build())
                .result("demo_division")
                .build();
    }

    private static class AddResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            Integer demoReduceResult = iContextBus.getPassResult("demo_reduce") != null ?  (Integer) iContextBus.getPassResult("demo_reduce") : 0;
            Integer demoMultiplyResult = iContextBus.getPassResult("demo_multiply") != null ? (Integer) iContextBus.getPassResult("demo_multiply"): 0;
            Integer handleResult = demoReduceResult + demoMultiplyResult;
            System.out.println("Addresult " + demoReduceResult + "+" + demoMultiplyResult + "=" + handleResult);
            return handleResult;
        }
    }

    private static class AddBranchResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            Integer branchReduce = iContextBus.getPassResult("demo_branch_reduce") != null ? (Integer) iContextBus.getPassResult("demo_branch_reduce") : 0;
            Integer branchMultiply = iContextBus.getPassResult("demo_branch_multiply") != null ? (Integer) iContextBus.getPassResult("demo_branch_multiply") : 0;
            Integer handleResult = branchReduce + branchMultiply;
            System.out.println("AddBranchresult " + branchReduce + "+" + branchMultiply + "=" + handleResult);
            return handleResult;
        }
    }
}
