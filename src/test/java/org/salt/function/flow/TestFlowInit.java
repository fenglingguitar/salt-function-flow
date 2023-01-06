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

package org.salt.function.flow;

import org.salt.function.flow.config.IFlowInit;
import org.salt.function.flow.demo.math.DemoFlowInit;
import org.salt.function.flow.demo.train.TrainFlowInit;
import org.salt.function.flow.test.stop.StopFlowInit;
import org.salt.function.flow.test.thread.ThreadFlowInit;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestFlowInit implements IFlowInit {

    /**
     * Build and register all flow
     */
    @Override
    public void configure(FlowEngine flowEngine) {

        DemoFlowInit demoFlowInit = new DemoFlowInit();
        demoFlowInit.configure(flowEngine);

        TrainFlowInit trainFlowInit = new TrainFlowInit();
        trainFlowInit.configure(flowEngine);

        StopFlowInit stopFlowInit = new StopFlowInit();
        stopFlowInit.configure(flowEngine);

        ThreadFlowInit threadFlowInit = new ThreadFlowInit();
        threadFlowInit.configure(flowEngine);
    }
}
