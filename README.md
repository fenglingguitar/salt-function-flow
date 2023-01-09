# Salt Function Flow

The Salt Function Flow is a lightweight flow orchestration component at the memory level, which uses function APIs to implement orchestration.

## Quick Start

Including flow node implementation, flow orchestration and flow execution

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
Inherit the FlowNodeWithReturn class, implement the doProcess method, declare @ NodeIdentity, and specify the node ID
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
Inject FlowEngine type bean with the registered ID of demo_flow process, use API to arrange nodes for sequential execution
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

## Documentation

[中文](https://blog.csdn.net/fenglingguitar/article/details/128500457)
