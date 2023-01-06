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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.FlowInstance;
import org.salt.function.flow.TestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@SpringBootConfiguration
public class DemoTest {

    @Autowired
    FlowEngine flowEngine;

    /**
     * Single flow exe
     */
    @Test
    public void testDemo() {
        System.out.println("demo_flow test: ");
        Integer result = flowEngine.execute("demo_flow", 39);
        System.out.println("demo_flow result: " + result);
        Assert.assertTrue(result != null && result == 894);
    }

    /**
     * Single flow with condition extend exe
     */
    @Test
    public void testExtendDemo() {
        System.out.println("demo_flow_extend test: ");
        Integer result = flowEngine.execute("demo_flow_extend", 39);
        System.out.println("demo_flow_extend result: " + result);
        Assert.assertTrue(result != null && result == 73);
    }

    /**
     * Single flow with condition extend exe
     */
    @Test
    public void testExclusiveDemo() {
        System.out.println("demo_flow_exclusive test: ");
        Integer result = flowEngine.execute("demo_flow_exclusive", 39);
        System.out.println("demo_flow_exclusive result: " + result);
        Assert.assertTrue(result != null && result == 985);
    }

    /**
     * Concurrent flow exe
     */
    @Test
    public void testConcurrentDemo() {
        System.out.println("demo_flow_concurrent test: ");
        Integer result = flowEngine.execute("demo_flow_concurrent", 39);
        System.out.println("demo_flow_concurrent result: " + result);
        Assert.assertTrue(result != null && result == 997);
    }

    /**
     * Notify flow exe
     */
    @Test
    public void testNotifyDemo() {
        System.out.println("demo_flow_notify test: ");
        Integer result = flowEngine.execute("demo_flow_notify", 39);
        System.out.println("demo_flow_notify result: " + result);
        Assert.assertTrue(result != null && result == 985);
    }

    /**
     * Future flow exe
     */
    @Test
    public void testFutureDemo() {
        System.out.println("demo_flow_future test: ");
        Integer result = flowEngine.execute("demo_flow_future", 39);
        System.out.println("demo_flow_future result: " + result);
        Assert.assertTrue(result != null && result == 997);
    }

    @Test
    public void testFuture1Demo1() {
        System.out.println("demo_flow_future_1 test: ");
        Integer result = flowEngine.execute("demo_flow_future_1", 39);
        System.out.println("demo_flow_future_1 result: " + result);
        Assert.assertTrue(result != null && result == 997);
    }

    @Test
    public void testInclusiveDemo() {
        System.out.println("demo_flow_inclusive test: ");
        Integer result = flowEngine.execute("demo_flow_inclusive", 39);
        System.out.println("demo_flow_inclusive result: " + result);
        Assert.assertTrue(result != null && result == 894);
    }

    @Test
    public void testInclusiveConcurrentDemo() {
        System.out.println("demo_flow_inclusive_concurrent test: ");
        Integer result = flowEngine.execute("demo_flow_inclusive_concurrent", 39);
        System.out.println("demo_flow_inclusive_concurrent result: " + result);
        Assert.assertTrue(result != null && result == 997);
    }

    /**
     * Single flow with branch exe
     */
    @Test
    public void testBranchDemo() {
        System.out.println("demo_branch test: ");
        Integer result = flowEngine.execute("demo_branch", 39);
        System.out.println("demo_branch result: " + result);
        Assert.assertTrue(result != null && result == 2);
    }

    @Test
    public void testBranchConcurrentDemo() {
        System.out.println("demo_branch_concurrent test: ");
        Integer result = flowEngine.execute("demo_branch_concurrent", 39);
        System.out.println("demo_branch_concurrent result: " + result);
        Assert.assertTrue(result != null && result == 6);
    }

    @Test
    public void testBranchNotifyDemo() {
        System.out.println("demo_branch_notify test: ");
        Integer result = flowEngine.execute("demo_branch_notify", 39);
        System.out.println("demo_branch_notify result: " + result);
        Assert.assertTrue(result != null && result == 2);
    }

    @Test
    public void testBranchFutureDemo() {
        System.out.println("demo_branch_future test: ");
        Integer result = flowEngine.execute("demo_branch_future", 39);
        System.out.println("demo_branch_future result: " + result);
        Assert.assertTrue(result != null && result == 6);
    }

    @Test
    public void testBranchNestedDemo() {
        System.out.println("demo_branch_nested test: ");
        Integer result = flowEngine.execute("demo_branch_nested", 39);
        System.out.println("demo_branch_nested result: " + result);
        Assert.assertTrue(result != null && result == 2);
    }

    @Test
    public void testBranchAnonymousDemo() {
        System.out.println("demo_branch_anonymous test: ");
        Integer result = flowEngine.execute("demo_branch_anonymous", 39);
        System.out.println("demo_branch_anonymous result: " + result);
        Assert.assertTrue(result != null && result == 2);
    }

    @Test
    public void testDynamicDemo() {
        for (int i=0; i<5; i++) {
            int a = (new Random()).nextInt(20);
            String flowID = "demo_flow_dynamic_" + i;
            FlowEngine.Builder builder = flowEngine.builder().id(flowID);
            builder.next("demo_add");
            if (a < 10) {
                builder.next("demo_reduce");
            } else {
                builder.next("demo_multiply");
            }
            builder.result("demo_division");
            FlowInstance flowInstance = builder.buildDynamic();
            System.out.println(flowID + " a: " + a);
            System.out.println(flowID + " test: ");
            Integer result = flowEngine.execute(flowInstance, 39);
            System.out.println(flowID + " result: " + result);
            Assert.assertTrue(result != null && ((a < 10 && result == 12) || (a >= 10 && result == 985)));
        }
    }
}
