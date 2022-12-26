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

package org.salt.function.flow.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salt.function.flow.FlowEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
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
        Assert.assertTrue(result != null && result == 13);
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

    /**
     * Single flow exe
     */
    @Test
    public void testConditionDemo() {
        System.out.println("demo_flow_condition test: ");
        Integer result = flowEngine.execute("demo_flow_condition", 39);
        System.out.println("demo_flow_condition result: " + result);
        Assert.assertTrue(result != null && result == 12);
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
        Assert.assertTrue(result != null && result == 13);
    }

    @Test
    public void testBranchFutureDemo() {
        System.out.println("demo_branch_future test: ");
        Integer result = flowEngine.execute("demo_branch_future", 39);
        System.out.println("demo_branch_future result: " + result);
        Assert.assertTrue(result != null && result == 6);
    }

    @Test
    public void testBranchAnonymousDemo() {
        System.out.println("demo_anonymous_branch test: ");
        Integer result = flowEngine.execute("demo_anonymous_branch", 39);
        System.out.println("demo_anonymous_branch result: " + result);
        Assert.assertTrue(result != null && result == 2);
    }
}
