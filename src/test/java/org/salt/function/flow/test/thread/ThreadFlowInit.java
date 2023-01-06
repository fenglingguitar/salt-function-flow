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

package org.salt.function.flow.test.thread;

import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.config.IFlowInit;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.IResult;

import java.util.concurrent.Executors;

public class ThreadFlowInit implements IFlowInit {

    @Override
    public void configure(FlowEngine flowEngine) {

        flowEngine.builder().id("demo_flow_concurrent_timeout")
                .next("demo_add")
                .concurrent(new AddResult(), 10, "demo_reduce", "demo_bit_right")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_flow_future_timeout")
                .next("demo_add")
                .future("demo_reduce", "demo_bit_right")
                .wait(new AddResult(), 10,"demo_reduce", "demo_bit_right")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_flow_concurrent_isolate")
                .next("demo_add")
                .concurrent(new AddResult(), Executors.newFixedThreadPool(3), "demo_reduce", "demo_bit_right")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_flow_concurrent_threadlocal")
                .next("demo_add")
                .concurrent(new ReduceResult(), "demo_reduce", "demo_bit_left")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_right").next("demo_reduce").result("demo_bit_right").build();
        flowEngine.builder().id("demo_branch_bit_left").next("demo_multiply").result("demo_bit_left").build();

        flowEngine.builder().id("demo_branch_flow_concurrent_timeout")
                .next("demo_add")
                .concurrent(new AddBranchResult(), 10, "demo_branch_bit_right", "demo_branch_bit_left")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_flow_future_timeout")
                .next("demo_add")
                .future("demo_branch_bit_right", "demo_branch_bit_left")
                .wait(new AddBranchResult(), 20,"demo_branch_bit_right", "demo_branch_bit_left")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_flow_concurrent_isolate")
                .next("demo_add")
                .concurrent(new AddBranchResult(), Executors.newFixedThreadPool(3), "demo_branch_bit_right", "demo_branch_bit_left")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_flow_concurrent_threadlocal")
                .next("demo_add")
                .concurrent(new AddBranchResult(), "demo_branch_bit_right", "demo_branch_bit_left")
                .result("demo_division")
                .build();
    }

    private static class AddResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            System.out.println("AddResult handle isTimeout: " + isTimeout);
            Integer demoReduceResult = iContextBus.getPassResult("demo_reduce") != null && iContextBus.getPassResult("demo_reduce") instanceof Integer ?  (Integer) iContextBus.getPassResult("demo_reduce") : 0;
            Integer demoBitRightResult = iContextBus.getPassResult("demo_bit_right") != null && iContextBus.getPassResult("demo_bit_right") instanceof Integer ? (Integer) iContextBus.getPassResult("demo_bit_right"): 0;
            Integer handleResult = demoReduceResult + demoBitRightResult;
            System.out.println("Addresult " + demoReduceResult + "+" + demoBitRightResult + "=" + handleResult);
            return handleResult;
        }
    }

    private static class ReduceResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            System.out.println("AddResult handle isTimeout: " + isTimeout);
            Integer demoReduceResult = iContextBus.getPassResult("demo_reduce") != null && iContextBus.getPassResult("demo_reduce") instanceof Integer ?  (Integer) iContextBus.getPassResult("demo_reduce") : 0;
            Integer demoBitRightResult = iContextBus.getPassResult("demo_bit_left") != null && iContextBus.getPassResult("demo_bit_left") instanceof Integer ? (Integer) iContextBus.getPassResult("demo_bit_left"): 0;
            Integer handleResult = demoReduceResult - demoBitRightResult;
            System.out.println("ReduceResult " + demoReduceResult + "-" + demoBitRightResult + "=" + handleResult);
            return handleResult;
        }
    }

    private static class AddBranchResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            System.out.println("AddBranchResult handle isTimeout: " + isTimeout);
            Integer demoBitRightResult = iContextBus.getPassResult("demo_branch_bit_right") != null && iContextBus.getPassResult("demo_branch_bit_right") instanceof Integer ?  (Integer) iContextBus.getPassResult("demo_branch_bit_right") : 0;
            Integer demoBitLeftResult = iContextBus.getPassResult("demo_branch_bit_left") != null && iContextBus.getPassResult("demo_branch_bit_left") instanceof Integer ? (Integer) iContextBus.getPassResult("demo_branch_bit_left"): 0;
            Integer handleResult = demoBitRightResult + demoBitLeftResult;
            System.out.println("AddBranchResult " + demoBitRightResult + "+" + demoBitLeftResult + "=" + handleResult);
            return handleResult;
        }
    }
}
