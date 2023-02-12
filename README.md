# Salt Function Flow

The Salt Function Flow is a lightweight flow orchestration component at the memory level, which uses function APIs to implement orchestration.

## Quick Start

Including flow node implementation, flow orchestration and flow execution.

![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-113715%402x.png)

### Maven

```
<dependency>
    <groupId>cn.fenglingsoftware</groupId>
    <artifactId>salt-function-flow</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Implement flow node
Inherit the FlowNodeWithReturn class, implement the doProcess method, declare @ NodeIdentity, and specify the node ID.
```
//Get the return value of the previous node, and add 123 to return
@NodeIdentity(nodeId = "demo_add")
public class DemoAddNode extends FlowNodeWithReturn<Integer> {
    @Override
    public Integer doProcess(IContextBus iContextBus) {
        Integer preResult = (Integer) iContextBus.getPreResult();
        Integer result = preResult + 123;
        System.out.println("DemoAddNode: " + preResult + "+123=" + result);
        return result;
    }
}
```
```
//Get the return value of the previous node, and subtract 15 to return
@NodeIdentity(nodeId = "demo_reduce")
public class DemoReduceNode extends FlowNodeWithReturn<Integer> {
    @Override
    public Integer doProcess(IContextBus iContextBus) {
        Integer preResult = (Integer) iContextBus.getPreResult();
        Integer result = preResult - 15;
        System.out.println("DemoReduceNode: " + preResult + "-15=" + result) ;
        return result;
    }
}
```
```
//Get the return value of the previous node and multiply by 73 to return
@NodeIdentity(nodeId = "demo_multiply")
public class DemoMultiplyNode extends FlowNodeWithReturn<Integer> {
    @Override
    public Integer doProcess(IContextBus iContextBus) {
        Integer preResult = (Integer) iContextBus.getPreResult();
        Integer result = preResult * 73;
        System.out.println("DemoMultiplyNode: " + preResult + "*73=" + result);
        return result;
    }
}
```
```
//Get the return value of the previous node, divide by 12, and return
@NodeIdentity(nodeId = "demo_division")
public class DemoDivisionNode extends FlowNodeWithReturn<Integer> {
    @Override
    public Integer doProcess(IContextBus iContextBus) {
        Integer preResult = (Integer) iContextBus.getPreResult();
        Integer result = preResult / 12;
        System.out.println("DemoDivisionNode: " + preResult + "/12=" + result);
        return result;
    }
}
```

### Orchestrate flow node
Inject FlowEngine type bean with the registered ID of demo_flow process, use API to arrange nodes for sequential execution.
```
@Autowired
FlowEngine flowEngine;

flowEngine.builder().id("demo_flow").next("demo_add").next("demo_reduce").next("demo_multiply").result("demo_division").build();
```

### Execute flow

```
@Autowired
FlowEngine flowEngine;

Integer result = flowEngine.execute("demo_flow", 39);
System.out.println("demo_flow result: " + result);
```

### Result

```
DemoAddNode: 39+123=162
DemoReduceNode: 162-15=147
DemoMultiplyNode: 147*73=10731
DemoDivisionNode: 10731/12=894
demo_flow result: 894
```

## Node function expansion
Extended demo_ Reduce node, If the incoming parameter is>40, execute the extended demo_remainder node.
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-133831%402x.png)
```
flowEngine.builder().id("demo_flow_extend")
                .next("demo_add")
                .next(
                        Info.builder().include("param <= 40").id("demo_reduce").build(),
                        Info.builder().include("param > 40").id("demo_remainder").build()
                )
                .next("demo_multiply")
                .result("demo_division").build();
```
## Complex gateway orchestration
### Exclusive execution
Execute the demo according to the incoming conditions demo_reduce or demo_multiply node.
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-134336%402x.png)
```
flowEngine.builder().id("demo_flow_exclusive")
                .next("demo_add")
                .next(
                        Info.builder().include("param <= 40").id("demo_reduce").build(),
                        Info.builder().include("param > 40").id("demo_multiply").build()
                )
                .result("demo_division").build();
```
### Parallel execution
#### Parallel form I
Execute the demo in parallel(asynchronous concurrent) demo_reduce、demo_multiply nodes, and add the results.
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-134654%402x.png)
```
flowEngine.builder().id("demo_flow_concurrent")
                .next("demo_add")
                .concurrent(new AddResult(), "demo_reduce", "demo_multiply")
                .result("demo_division")
                .build();
```
```
//Result addition handle
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
```
#### Parallel form II
Asynchronous execution of demo_reduce、 demo_multiply nodes, and add the results.
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-135120%402x.png)
```
flowEngine.builder().id("demo_flow_future")
                .next("demo_add")
                .future("demo_reduce", "demo_multiply")
                .wait(new AddResult(), "demo_reduce", "demo_multiply")
                .result("demo_division")
                .build();
```
Asynchronous execution of demo_reduce node, and execute the demo synchronously demo_multiply, and add the results.
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-134857%402x.png)
```
flowEngine.builder().id("demo_flow_future_1")
                .next("demo_add")
                .future("demo_reduce")
                .next("demo_multiply")
                .wait(new AddResult(), "demo_reduce")
                .result("demo_division")
                .build();
```
### Notice execution
Notify execution of demo_reduce.
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-135300%402x.png)
```
flowEngine.builder().id("demo_flow_notify")
                .next("demo_add")
                .notify("demo_reduce")
                .next("demo_multiply")
                .result("demo_division")
                .build();
```
### Inclusive execution
#### Synchronous Inclusive execution
Synchronous Inclusive execution of demo_reduce、demo_multiply.
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-135739%402x.png)
```
flowEngine.builder().id("demo_flow_inclusive")
                .next("demo_add")
                .all(
                        Info.builder().include("param > 30").id("demo_reduce").build(),
                        Info.builder().include("param < 50").id("demo_multiply").build()
                )
                .result("demo_division").build();
```
#### Asynchronous Inclusive execution
Asynchronous Inclusive execution of demo_reduce、demo_multiply.
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-135910%402x.png)
```
flowEngine.builder().id("demo_flow_inclusive_concurrent")
                .next("demo_add")
                .concurrent(
                        new AddResult(),
                        Info.builder().include("param > 30").id("demo_reduce").build(),
                        Info.builder().include("param < 50").id("demo_multiply").build()
                )
                .result("demo_division").build();
```
## Sub process support
Build subprocess.
```
flowEngine.builder().id("demo_branch_reduce").next("demo_reduce").result("demo_remainder").build();
flowEngine.builder().id("demo_branch_multiply").next("demo_multiply").result("demo_remainder").build();

```
### Exclusive execution
It is basically the same as the same node orchestration.
```
flowEngine.builder().id("demo_branch_exclusive")
                .next("demo_add")
                .next(
                        Info.builder().include("param <= 40").id("demo_branch_reduce").build(),
                        Info.builder().include("param > 40").id("demo_branch_multiply").build()
                )
                .result("demo_division")
                .build();
```
### Parallel execution
```
flowEngine.builder().id("demo_branch_concurrent")
                .next("demo_add")
                .concurrent(new AddBranchResult(), "demo_branch_reduce", "demo_branch_multiply")
                .result("demo_division")
                .build();
```
```
flowEngine.builder().id("demo_branch_future")
                .next("demo_add")
                .future("demo_branch_reduce")
                .next("demo_branch_multiply")
                .wait(new AddBranchResult(), "demo_branch_reduce")
                .result("demo_division").build();
```
### Notice execution
```
flowEngine.builder().id("demo_branch_notify")
                .next("demo_add")
                .notify("demo_branch_reduce")
                .next("demo_branch_multiply")
                .result("demo_division").build();
```
### Inclusive execution
```
flowEngine.builder().id("demo_branch")
                .next("demo_add")
                .all("demo_branch_reduce", "demo_branch_multiply")
                .result("demo_division")
                .build();
```
### Nested execution
```
flowEngine.builder().id("demo_branch_nested")
                .next("demo_add")
                .all(
                        flowEngine.builder().id("nested_1").next("demo_reduce").result("demo_remainder").build(),
                        flowEngine.builder().id("nested_2").next("demo_multiply").result("demo_remainder").build())
                .result("demo_division")
                .build();
```
### Anonymous subprocess
```
flowEngine.builder().id("demo_branch_anonymous")
                .next("demo_add")
                .all(
                        flowEngine.branch().next("demo_reduce").result("demo_remainder").build(),
                        flowEngine.branch().next("demo_multiply").result("demo_remainder").build())
                .result("demo_division")
                .build();
```
## Conditional judgement
![image](https://github.com/Jindou2018/image/raw/master/flow-image/WX20230109-144441%402x.png)
### Rule script judgment
The condition judgment parameter defaults to the execution process input parameter. Children's tickets are issued when they are younger than 14 years old, and adult tickets are issued when they are older than 14 years old.
```
flowEngine.builder().id("train_ticket")
                .next("base_price")
                .next(
                        Info.builder().include("age < 14").id("child_ticket").build(),
                        Info.builder().include("age >= 14").id("adult_tickt").build())
                .result("ticket_result")
                .build();
```
```
Passenger passenger = Passenger.builder().name("jack").age(12).build();
Ticket ticket = flowEngine.execute("train_ticket", passenger);
System.out.println("train_ticket result: " + ticket.getPrice());
```
#### Custom Condition Parameters
The execution process input parameter is not used as the condition judgment parameter, and the input parameter is customized.
```
Passenger passenger = Passenger.builder().name("jack").age(12).build();
//Custom Condition Parameters
Map<String, Object> condition = new HashMap();
condition.put("age", 12);
Ticket ticket = flowEngine.execute("train_ticket", passenger, condition);
System.out.println("train_ticket result: " + ticket.getPrice());
```
### Embedded function judgment
Use functions to make conditional judgments.
```
flowEngine.builder().id("train_ticket_1")
               .next("base_price")
               .next(
                       Info.builder().match(iContextBus -> ((Passenger) iContextBus.getParam()).getAge() < 14).id("child_ticket").build(),
                       Info.builder().match(iContextBus -> ((Passenger) iContextBus.getParam()).getAge() >= 14).id("adult_tickt").build())
               .result("ticket_result")
               .build();
```
```
Passenger passenger = Passenger.builder().name("jack").age(12).build();
Ticket ticket = flowEngine.execute("train_ticket_1", passenger);
System.out.println("train_ticket_1 result: " + ticket.getPrice());
```
## Node input parameter return value
Nodes can have fixed input parameters and return value types. During the orchestration, embedded functions use context construction or transformation.
```
//The basic fare calculation input parameter is the distance information, 
//inherits the FlowNodeWithReturnAndInput abstract class, and defines the input parameter
@NodeIdentity(nodeId = "base_price")
public class TrainBasePrice extends FlowNodeWithReturnAndInput<Integer, Station> {

    @Override
    public Integer doProcessWithInput(IContextBus iContextBus, Station station) {
        if (station != null) {
            System.out.println("Passengers travel from " + station.getFrom() + " to " + station.getTo());
        }
        System.out.println("Calculate the basic train ticket price 300");
        return 300;
    }
}
```
```
//Input and output functions are embedded in the orchestration, 
//and input parameters and return values are processed through the following process
flowEngine.builder().id("train_ticket_2")
                .next(
                        Info.builder().id("base_price")
                                .input(iContextBus -> {
                                    Passenger passenger = (Passenger) iContextBus.getParam();
                                    return Station.builder().from(passenger.getFrom()).to(passenger.getTo()).build();
                                })
                                .output((iContextBus, result) -> {
                                    System.out.println("base_price return " + result);
                                }).build())
                .next(
                        Info.builder().include("age < 14").id("child_ticket").build(),
                        Info.builder().include("age >= 14").id("adult_tickt").build())
                .result("ticket_result")
                .build();
```
## Data transmission
The process execution instance context data is transferred through the IContextBus interface
```
public interface IContextBus<T, R> {

    //Get flow execution parameters
    T getParam();

    //Get flow execution result
    R getResult();

    //Set flow execution result
    void setResult(R result);

    //Put additional transmission context information
    <P> void putTransmitInfo(String key, P content);

    //Get additional transmission context information
    <P> P getTransmitInfo(String key);

    //Get the parameters of node condition judgment
    Map<String, Object> getConditionMap();
    
    //Add the parameters of node condition judgment
    <P> void addCondition(String key, P value);

    //Get the execution result of the last node, which may return null
    <P> P getPreResult();

    //Get the execution result of any node
    <P> P getPassResult(String nodeId);

    //Get the execution exception of any node
    Exception getPassException(String nodeId);

    //Get flow ID
    String getFlowId();

    //Get flow execution Instance ID
    String getRuntimeId();

    //Stop flow execution instance
    void stopProcess();

    //Rollback flow execution instance
    void rollbackProcess();
}
```
## Process termination
### Normal Termination
Stop the whole process through the stopProcess of the IContextBus interface.
```
@NodeIdentity(nodeId = "demo_bit_and")
public class DemoBitAndNode extends FlowNodeWithReturn<Integer> {

    @Override
    public Integer doProcess(IContextBus iContextBus) {
        Integer preResult = (Integer) iContextBus.getPreResult();
        if (preResult > 500) {
            System.out.println("DemoBitAndNode: stop flow");
            iContextBus.stopProcess();
        } else {
            Integer result = preResult & 256;
            System.out.println("DemoBitAndNode: " + preResult + "&256=" + result);
            return result;
        }
        return null;
    }
}
```
### Abnormal termination
Terminate the process by throwing an exception in the node, but only the synchronous execution node.
```
@NodeIdentity(nodeId = "demo_bit_or")
public class DemoBitOrNode extends FlowNodeWithReturn<Integer> {

    @Override
    public Integer doProcess(IContextBus iContextBus) {
        Integer preResult = (Integer) iContextBus.getPreResult();
        if (preResult > 500) {
            System.out.println("DemoBitOrNode: throw exception");
            throw new RuntimeException("DemoBitOrNode Exception!");
        } else {
            Integer result = preResult | 128;
            System.out.println("DemoBitOrNode: " + preResult + "|128=" + result);
            return result;
        }
    }
}
```
### Rollback Process
Through the rollback process method of the IContextBus interface, the entire process is actively rolled back, and the rollback method of executed nodes is triggered in reverse order. The rollback method is empty by default, and nodes can be selectively implemented as needed.
```
default <T, R> void rollback(IContextBus<T, R> iContextBus) {}
```
```
@NodeIdentity(nodeId = "demo_bit_xor")
public class DemoBitXorNode extends FlowNodeWithReturn<Integer> {

    @Override
    public Integer doProcess(IContextBus iContextBus) {
        Integer preResult = (Integer) iContextBus.getPreResult();
        if (preResult > 500) {
            System.out.println("DemoBitOrNode: rollback flow");
            iContextBus.rollbackProcess();
        } else {
            Integer result = preResult | 128;
            System.out.println("DemoBitOrNode: " + preResult + "|128=" + result);
            return result;
        }
        return null;
    }

    @Override
    public <T, R> void rollback(IContextBus<T, R> iContextBus) {
        System.out.println("DemoBitOrNode: rollback execute");
    }
}
```
## Thread
### Timeout
The concurrent and wait functions are executed asynchronously. The maximum waiting time can be set through the timeout parameter
```
//Concurrent Add timeout wait parameter, in milliseconds
flowEngine.builder().id("demo_flow_concurrent_timeout")
                .next("demo_add")
                .concurrent(new AddResult(), 10, "demo_reduce", "demo_bit_right")
                .result("demo_division")
                .build();
```
```
//The parallel result processing handle can receive isTimeout and judge whether there is timeout
private static class AddResult implements IResult<Integer> {
        @Override
        public Integer handle(IContextBus iContextBus, boolean isTimeout) {
            System.out.println("AddResult handle isTimeout: " + isTimeout);
		}
}
```
### Thread Pool Configuration
Define thread pool configuration through yml file
```
salt:
  function:
    flow:
      threadpool:
        coreSize: 50
        maxSize: 150
        queueCapacity: 256
        keepAlive: 30
```
### Custom thread pool
#### Redefine beans
Redefine the thread pool bean named flowThreadPool.
```
@Bean
@ConditionalOnMissingBean(name = "flowThreadPool")
public ThreadPoolTaskExecutor flowThreadPool() {
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(50);
    threadPoolTaskExecutor.setMaxPoolSize(150);
    threadPoolTaskExecutor.setQueueCapacity(256);
    threadPoolTaskExecutor.setKeepAliveSeconds(30);
    threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    threadPoolTaskExecutor.setThreadNamePrefix("thread-pool-flow");
    threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        threadPoolTaskExecutor.shutdown();
    }));
    return threadPoolTaskExecutor;
}
```
#### Parameter incoming
Pass the parameter to an independent thread pool.
```
flowEngine.builder().id("demo_flow_concurrent_isolate")
                .next("demo_add")
                .concurrent(new AddResult(), Executors.newFixedThreadPool(3), "demo_reduce", "demo_bit_right")
                .result("demo_division")
                .build();
```
### ThreadLocal processing
ThreadLocal processing transfers the required ThreadLocal information to the asynchronous thread by implementing the IThreadContent interface.
```
//Inherit the IThreadContent interface and define it as a bean
@Component
public class TestThreadContent implements IThreadContent {

    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Override
    public Object getThreadContent() {
        return threadLocal.get();
    }

    @Override
    public void setThreadContent(Object content) {
        threadLocal.set((Map<String, Object>) content);
    }

    @Override
    public void cleanThreadContent() {
        threadLocal.remove();
    }
}

```
## Dynamic construction process
The process can be built dynamically through the builder. buildDynamic method. The dynamic process is not registered and can be built multiple times.
```
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
}
```

## Documentation

[中文](https://blog.csdn.net/fenglingguitar/article/details/128500457)
