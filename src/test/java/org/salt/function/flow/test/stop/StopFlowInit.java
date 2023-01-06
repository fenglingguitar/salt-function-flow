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

package org.salt.function.flow.test.stop;

import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.config.IFlowInit;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.IResult;

public class StopFlowInit implements IFlowInit {

    /**
     * Build and register all flow
     */
    @Override
    public void configure(FlowEngine flowEngine) {

        flowEngine.builder().id("demo_bit_and")
                .next("demo_add")
                .next("demo_reduce")
                .next("demo_bit_and")
                .next("demo_multiply")
                .result("demo_division").build();

        flowEngine.builder().id("demo_bit_and_concurrent")
                .next("demo_add")
                .concurrent(new AddBitAndResult(), "demo_reduce", "demo_multiply", "demo_bit_and")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_and_future")
                .next("demo_add")
                .future("demo_reduce", "demo_multiply", "demo_bit_and")
                .wait(new AddBitAndResult(), "demo_reduce", "demo_multiply", "demo_bit_and")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_and_all")
                .next("demo_add")
                .all("demo_reduce", "demo_bit_and", "demo_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_and_notify")
                .next("demo_add")
                .notify("demo_reduce", "demo_multiply", "demo_bit_and")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_or")
                .next("demo_add")
                .next("demo_reduce")
                .next("demo_bit_or")
                .next("demo_multiply")
                .result("demo_division").build();

        flowEngine.builder().id("demo_bit_or_concurrent")
                .next("demo_add")
                .concurrent(new AddBitOrResult(), "demo_reduce", "demo_multiply", "demo_bit_or")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_or_future")
                .next("demo_add")
                .future("demo_reduce", "demo_multiply", "demo_bit_or")
                .wait(new AddBitOrResult(), "demo_reduce", "demo_multiply", "demo_bit_or")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_or_all")
                .next("demo_add")
                .all("demo_reduce", "demo_bit_or", "demo_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_or_notify")
                .next("demo_add")
                .notify("demo_reduce", "demo_multiply", "demo_bit_or")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_xor")
                .next("demo_add")
                .next("demo_reduce")
                .next("demo_bit_xor")
                .next("demo_multiply")
                .result("demo_division").build();

        flowEngine.builder().id("demo_bit_xor_concurrent")
                .next("demo_add")
                .concurrent(new AddBitAndResult(), "demo_bit_xor", "demo_reduce", "demo_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_xor_future")
                .next("demo_add")
                .future("demo_reduce", "demo_multiply", "demo_bit_xor")
                .wait(new AddBitAndResult(), "demo_reduce", "demo_multiply", "demo_bit_xor")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_xor_all")
                .next("demo_add")
                .all("demo_reduce", "demo_bit_xor", "demo_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_bit_xor_notify")
                .next("demo_add")
                .notify("demo_reduce", "demo_multiply", "demo_bit_xor")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_and_reduce").next("demo_reduce").next("demo_bit_and").result("demo_remainder").build();
        flowEngine.builder().id("demo_branch_bit_and_multiply").next("demo_multiply").result("demo_remainder").build();

        flowEngine.builder().id("demo_branch_bit_and")
                .next("demo_add")
                .next("demo_branch_bit_and_reduce")
                .next("demo_branch_bit_and_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_and_concurrent")
                .next("demo_add")
                .concurrent(new AddBranchBitAndResult(),"demo_branch_bit_and_reduce", "demo_branch_bit_and_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_and_future")
                .next("demo_add")
                .future("demo_branch_bit_and_reduce", "demo_branch_bit_and_multiply")
                .wait(new AddBranchBitAndResult(),"demo_branch_bit_and_reduce", "demo_branch_bit_and_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_and_all")
                .next("demo_add")
                .all("demo_branch_bit_and_reduce", "demo_branch_bit_and_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_and_notify")
                .next("demo_add")
                .notify("demo_branch_bit_and_reduce", "demo_branch_bit_and_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_or_reduce").next("demo_reduce").next("demo_bit_or").result("demo_remainder").build();
        flowEngine.builder().id("demo_branch_bit_or_multiply").next("demo_multiply").result("demo_remainder").build();

        flowEngine.builder().id("demo_branch_bit_or")
                .next("demo_add")
                .all("demo_branch_bit_or_reduce", "demo_branch_bit_or_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_or_concurrent")
                .next("demo_add")
                .concurrent(new AddBranchBitOrResult(), "demo_branch_bit_or_reduce", "demo_branch_bit_or_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_or_future")
                .next("demo_add")
                .future("demo_branch_bit_or_reduce", "demo_branch_bit_or_multiply")
                .wait(new AddBranchBitOrResult(),"demo_branch_bit_or_reduce", "demo_branch_bit_or_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_or_all")
                .next("demo_add")
                .all("demo_branch_bit_or_reduce", "demo_branch_bit_or_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_or_notify")
                .next("demo_add")
                .notify("demo_branch_bit_or_reduce", "demo_branch_bit_or_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_xor_reduce").next("demo_reduce").next("demo_bit_xor").result("demo_remainder").build();
        flowEngine.builder().id("demo_branch_bit_xor_multiply").next("demo_multiply").result("demo_remainder").build();

        flowEngine.builder().id("demo_branch_bit_xor")
                .next("demo_add")
                .next("demo_branch_bit_xor_reduce")
                .next("demo_branch_bit_xor_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_xor_concurrent")
                .next("demo_add")
                .concurrent(new AddBranchBitAndResult(),"demo_branch_bit_xor_reduce", "demo_branch_bit_xor_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_xor_future")
                .next("demo_add")
                .future("demo_branch_bit_xor_multiply", "demo_branch_bit_xor_reduce")
                .wait(new AddBranchBitAndResult(),"demo_branch_bit_xor_reduce", "demo_branch_bit_xor_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_xor_all")
                .next("demo_add")
                .all("demo_branch_bit_xor_reduce", "demo_branch_bit_xor_multiply")
                .result("demo_division")
                .build();

        flowEngine.builder().id("demo_branch_bit_xor_notify")
                .next("demo_add")
                .notify("demo_branch_bit_xor_reduce", "demo_branch_bit_xor_multiply")
                .result("demo_division")
                .build();
    }

    private static class AddBitAndResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            Integer demoReduceResult = iContextBus.getPassResult("demo_reduce") != null ?  (Integer) iContextBus.getPassResult("demo_reduce") : 0;
            Integer demoMultiplyResult = iContextBus.getPassResult("demo_multiply") != null ? (Integer) iContextBus.getPassResult("demo_multiply"): 0;
            Integer demoBitAndResult = iContextBus.getPassResult("demo_bit_and") != null ? (Integer) iContextBus.getPassResult("demo_bit_and"): 0;
            Integer handleResult = demoReduceResult + demoMultiplyResult + demoBitAndResult;
            System.out.println("Addresult " + demoReduceResult + "+" + demoMultiplyResult + "+" + demoBitAndResult + "=" + handleResult);
            return handleResult;
        }
    }

    private static class AddBitOrResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            Integer demoReduceResult = iContextBus.getPassResult("demo_reduce") != null ?  (Integer) iContextBus.getPassResult("demo_reduce") : 0;
            Integer demoMultiplyResult = iContextBus.getPassResult("demo_multiply") != null ? (Integer) iContextBus.getPassResult("demo_multiply"): 0;
            Integer demoBitAndResult = iContextBus.getPassResult("demo_bit_or") != null ? (Integer) iContextBus.getPassResult("demo_bit_or"): 0;
            Integer handleResult = demoReduceResult + demoMultiplyResult + demoBitAndResult;
            System.out.println("Addresult " + demoReduceResult + "+" + demoMultiplyResult + "+" + demoBitAndResult + "=" + handleResult);
            return handleResult;
        }
    }

    private static class AddBranchBitAndResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            Integer branchReduce = iContextBus.getPassResult("demo_branch_bit_and_reduce") != null ? (Integer) iContextBus.getPassResult("demo_branch_bit_and_reduce") : 0;
            Integer branchMultiply = iContextBus.getPassResult("demo_branch_bit_and_multiply") != null ? (Integer) iContextBus.getPassResult("demo_branch_bit_and_multiply") : 0;
            Integer handleResult = branchReduce + branchMultiply;
            System.out.println("AddBranchresult " + branchReduce + "+" + branchMultiply + "=" + handleResult);
            return handleResult;
        }
    }
    private static class AddBranchBitOrResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            Integer branchReduce = iContextBus.getPassResult("demo_branch_bit_or_reduce") != null ? (Integer) iContextBus.getPassResult("demo_branch_bit_or_reduce") : 0;
            Integer branchMultiply = iContextBus.getPassResult("demo_branch_bit_or_multiply") != null ? (Integer) iContextBus.getPassResult("demo_branch_bit_or_multiply") : 0;
            Integer handleResult = branchReduce + branchMultiply;
            System.out.println("AddBranchresult " + branchReduce + "+" + branchMultiply + "=" + handleResult);
            return handleResult;
        }
    }
}
