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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salt.function.flow.FlowEngine;
import org.salt.function.flow.TestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@SpringBootConfiguration
public class StopTest {

    @Autowired
    FlowEngine flowEngine;
    
    int testParam = 2048;

    @Test
    public void testDemoBitAnd() {
        System.out.println("demo_bit_and test: ");
        Integer result = flowEngine.execute("demo_bit_and", testParam);
        System.out.println("demo_bit_and result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitAndConcurrent() {
        System.out.println("demo_bit_and_concurrent test: ");
        Integer result = flowEngine.execute("demo_bit_and_concurrent", testParam);
        System.out.println("demo_bit_and_concurrent result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitAndFuture() {
        System.out.println("demo_bit_and_future test: ");
        Integer result = flowEngine.execute("demo_bit_and_future", testParam);
        System.out.println("demo_bit_and_future result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitAndAll() {
        System.out.println("demo_bit_and_all test: ");
        Integer result = flowEngine.execute("demo_bit_and_all", testParam);
        System.out.println("demo_bit_and_all result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitAndNotify() {
        System.out.println("demo_bit_and_notify test: ");
        Integer result = flowEngine.execute("demo_bit_and_notify", testParam);
        System.out.println("demo_bit_and_notify result: " + result);
        Assert.assertTrue(result != null && result == 180);
    }

    @Test
    public void testDemoBitOr() {
        System.out.println("demo_bit_or test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_bit_or", testParam);
        } catch (Exception e) {
            System.out.println("demo_bit_or exception: " + e.getMessage());
        }
        System.out.println("demo_bit_or result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitOrConcurrent() {
        System.out.println("demo_bit_or_concurrent test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_bit_or_concurrent", testParam);
        } catch (Exception e) {
            System.out.println("demo_bit_or_concurrent exception: " + e.getMessage());
        }
        System.out.println("demo_bit_or_concurrent result: " + result);
        Assert.assertTrue(result != null && result == 13386);
    }

    @Test
    public void testDemoBitOrFuture() {
        System.out.println("demo_bit_or_future test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_bit_or_future", testParam);
        } catch (Exception e) {
            System.out.println("demo_bit_or_future exception: " + e.getMessage());
        }
        System.out.println("demo_bit_or_future result: " + result);
        Assert.assertTrue(result != null && result == 13386);
    }

    @Test
    public void testDemoBitOrAll() {
        System.out.println("demo_bit_or_all test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_bit_or_all", testParam);
        } catch (Exception e) {
            System.out.println("demo_bit_or_all exception: " + e.getMessage());
        }
        System.out.println("demo_bit_or_all result: " + result);
        Assert.assertTrue(result != null && result == 13115);
    }

    @Test
    public void testDemoBitOrNotify() {
        System.out.println("demo_bit_or_notify test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_bit_or_notify", testParam);
        } catch (Exception e) {
            System.out.println("demo_bit_or_notify exception: " + e.getMessage());
        }
        System.out.println("demo_bit_or_notify result: " + result);
        Assert.assertTrue(result != null && result == 180);
    }

    @Test
    public void testDemoBitXor() {
        System.out.println("demo_bit_xor test: ");
        Integer result = flowEngine.execute("demo_bit_xor", testParam);
        System.out.println("demo_bit_xor result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitXorConcurrent() {
        System.out.println("demo_bit_xor_concurrent test: ");
        Integer result = flowEngine.execute("demo_bit_xor_concurrent", testParam);
        System.out.println("demo_bit_xor_concurrent result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitXorFuture() {
        System.out.println("demo_bit_xor_future test: ");
        Integer result = flowEngine.execute("demo_bit_xor_future", testParam);
        System.out.println("demo_bit_xor_future result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitXorAll() {
        System.out.println("demo_bit_xor_all test: ");
        Integer result = flowEngine.execute("demo_bit_xor_all", testParam);
        System.out.println("demo_bit_xor_all result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBitXorNotify() {
        System.out.println("demo_bit_xor_notify test: ");
        Integer result = flowEngine.execute("demo_bit_xor_notify", testParam);
        System.out.println("demo_bit_xor_notify result: " + result);
        Assert.assertTrue(result != null && result == 180);
    }

    @Test
    public void testDemoBranchBitAnd() {
        System.out.println("demo_branch_bit_and test: ");
        Integer result = flowEngine.execute("demo_branch_bit_and", testParam);
        System.out.println("demo_branch_bit_and result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBranchBitAndConcurrent() {
        System.out.println("demo_branch_bit_and_concurrent test: ");
        Integer result = flowEngine.execute("demo_branch_bit_and_concurrent", testParam);
        System.out.println("demo_branch_bit_and_concurrent result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBranchBitAndFuture() {
        System.out.println("demo_branch_bit_and_future test: ");
        Integer result = flowEngine.execute("demo_branch_bit_and_future", testParam);
        System.out.println("demo_branch_bit_and_future result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBranchBitAndAll() {
        System.out.println("demo_branch_bit_and_all test: ");
        Integer result = flowEngine.execute("demo_branch_bit_and_all", testParam);
        System.out.println("demo_branch_bit_and_all result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBranchBitAndNotify() {
        System.out.println("demo_branch_bit_and_notify test: ");
        Integer result = flowEngine.execute("demo_branch_bit_and_notify", testParam);
        System.out.println("demo_branch_bit_and_notify result: " + result);
        Assert.assertTrue(result != null && result ==180);
    }

    @Test
    public void testDemoBranchBitOr() {
        System.out.println("demo_branch_bit_or test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_branch_bit_or", testParam);
        } catch (Exception e) {
            System.out.println("demo_branch_bit_or exception: " + e.getMessage());
        }
        System.out.println("demo_branch_bit_or result: " + result);
        Assert.assertTrue(result != null && result == 3);
    }

    @Test
    public void testDemoBranchBitOrConcurrent() {
        System.out.println("demo_branch_bit_or_concurrent test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_branch_bit_or_concurrent", testParam);
        } catch (Exception e) {
            System.out.println("demo_branch_bit_or_concurrent exception: " + e.getMessage());
        }
        System.out.println("demo_branch_bit_or_concurrent result: " + result);
        Assert.assertTrue(result != null && result == 2);
    }

    @Test
    public void testDemoBranchBitOrFuture() {
        System.out.println("demo_branch_bit_or_future test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_branch_bit_or_future", testParam);
        } catch (Exception e) {
            System.out.println("demo_branch_bit_or_future exception: " + e.getMessage());
        }
        System.out.println("demo_branch_bit_or_future result: " + result);
        Assert.assertTrue(result != null && result == 2);
    }

    @Test
    public void testDemoBranchBitOrAll() {
        System.out.println("demo_branch_bit_or_all test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_branch_bit_or_all", testParam);
        } catch (Exception e) {
            System.out.println("demo_branch_bit_or_all exception: " + e.getMessage());
        }
        System.out.println("demo_branch_bit_or_all result: " + result);
        Assert.assertTrue(result != null && result == 3);
    }

    @Test
    public void testDemoBranchBitOrNotify() {
        System.out.println("demo_branch_bit_or_notify test: ");
        Integer result = null;
        try {
            result = flowEngine.execute("demo_branch_bit_or_notify", testParam);
        } catch (Exception e) {
            System.out.println("demo_branch_bit_or_notify exception: " + e.getMessage());
        }
        System.out.println("demo_branch_bit_or_notify result: " + result);
        Assert.assertTrue(result != null && result == 180);
    }

    @Test
    public void testDemoBranchBitXor() {
        System.out.println("demo_branch_bit_xor test: ");
        Integer result = flowEngine.execute("demo_branch_bit_xor", testParam);
        System.out.println("demo_branch_bit_xor result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBranchBitXorConcurrent() {
        System.out.println("demo_branch_bit_xor_concurrent test: ");
        Integer result = flowEngine.execute("demo_branch_bit_xor_concurrent", testParam);
        System.out.println("demo_branch_bit_xor_concurrent result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBranchBitXorFuture() {
        System.out.println("demo_branch_bit_xor_future test: ");
        Integer result = flowEngine.execute("demo_branch_bit_xor_future", testParam);
        System.out.println("demo_branch_bit_xor_future result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBranchBitXorAll() {
        System.out.println("demo_branch_bit_xor_all test: ");
        Integer result = flowEngine.execute("demo_branch_bit_xor_all", testParam);
        System.out.println("demo_branch_bit_xor_all result: " + result);
        Assert.assertTrue(result == null);
    }

    @Test
    public void testDemoBranchBitXorNotify() {
        System.out.println("demo_branch_bit_xor_notify test: ");
        Integer result = flowEngine.execute("demo_branch_bit_xor_notify", testParam);
        System.out.println("demo_branch_bit_xor_notify result: " + result);
        Assert.assertTrue(result != null && result ==180);
    }
}
