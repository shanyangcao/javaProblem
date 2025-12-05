## problem

#### `basicAck` 的两个参数具体含义

`basicAck` 是**消费者告诉 RabbitMQ**：“我已经成功处理完消息了，你可以把这条 / 这些消息删掉了”，两个参数就是用来「指定要确认哪些消息」。

**1. 参数 1：**`**deliveryTag**`**（长整型）——「消息的唯一快递单号」**

- **核心含义**：RabbitMQ 给每一条发送到消费者的消息，分配了一个**独一无二的数字标签**（类似快递的 “运单号”），且**每个信道（Channel）内的标签从 1 开始递增**（比如第一条消息 tag=1，第二条 tag=2，以此类推）。`deliveryTag` 的作用就是告诉 RabbitMQ：“我要确认的是「运单号 = X」的这条消息”。
- **怎么来的**：消费者接收消息时，会从 `Delivery` 对象（或 `Envelope` 对象）中拿到这个 tag，比如 Java 代码里：

```
// 消费消息时获取 deliveryTag
long tag = envelope.getDeliveryTag(); 
// 确认这条消息
channel.basicAck(tag, false);
```

- **记忆口诀**：tag 是单号，指哪条认哪条。

**2. 参数 2：**`multiple`**（布尔型）——「确认模式：单条 / 批量」**

- **核心含义**：决定是「只确认 `deliveryTag` 这一条消息」，还是「确认 `deliveryTag` 及之前所有未确认的消息」（批量确认）。

| **multiple 值** | **含义（人话版）**                       | **例子（假设 tag=5）**            |
| --------------- | ---------------------------------------- | --------------------------------- |
| `false`         | 只确认「运单号 = 5」的这一条（精准确认） | 仅删掉 tag=5 的消息，其他消息不动 |
| `true`          | 确认「运单号≤5」的所有未确认消息（批量） | 删掉 tag=1、2、3、4、5 的所有消息 |

- **使用场景**：


- - `false`：适合需要精准控制的场景（比如每条消息处理逻辑独立，怕批量确认漏处理）；
  - `true`：适合高并发场景（减少确认次数，提升性能，比如批量处理完一批消息后一次性确认）。


- **记忆口诀**：multiple 看真假，假单条，真批量。

**完整例子（把两个参数串起来）**

假设消费者先后收到了 tag=1、2、3、4、5 的 5 条消息，处理完后执行：

```
// 场景1：只确认 tag=5 的消息
channel.basicAck(5, false); 
// 结果：RabbitMQ 只删掉 tag=5 的消息，1-4 仍未确认

// 场景2：批量确认 1-5 的所有消息
channel.basicAck(5, true);  
// 结果：RabbitMQ 删掉 tag=1-5 的所有消息
```

##### 避坑提醒（易忘点）

1. `deliveryTag` 是「信道级别的递增」：不同 Channel 的 tag 相互独立（比如 Channel1 的 tag=5 和 Channel2 的 tag=5 是两条不同消息）；
2. 不能确认不存在的 tag：比如只有 5 条消息，却传 tag=6，会报错；
3. 批量确认只认 “已接收且未确认” 的消息：如果 tag=3 已经确认过，传 tag=5、multiple=true，只会确认 4、5 两条。

#### basicConsume的四个参数具体含义

`channel.basicConsume` 的作用是：**让消费者 “订阅” 指定队列，告诉 RabbitMQ：“我要从这个队列里拿消息了，拿到后按我的逻辑处理”**。

方法完整格式：

```
channel.basicConsume(
    String queue,          // 队列名
    boolean autoAck,       // 自动确认开关
    DeliverCallback deliverCallback, // 拿到消息后的处理逻辑
    CancelCallback cancelCallback    // 消费被取消时的处理逻辑
)
```

**1. 参数 1：**`queue`**（队列名，String 类型）**

- **含义**：指定要订阅的队列名称（比如你要从 `QUEUE_NAME` 这个队列里收消息）。
- **记忆**：“订哪个队列，就填哪个名”。

**2. 参数 2：**`autoAck`**（自动确认开关，boolean 类型）—— 最关键的参数**

- **核心含义**：决定消息是否「自动确认」（自动告诉 RabbitMQ“我收到消息了，你可以删了”）。

| **autoAck 值** | **行为（人话版）**                                           | **场景**                                 |
| -------------- | ------------------------------------------------------------ | ---------------------------------------- |
| `true`         | 自动确认：消费者一拿到消息，RabbitMQ 就直接删消息            | 对消息可靠性要求低的场景（比如日志打印） |
| `false`        | 手动确认：必须调用 `basicAck` 手动告诉 RabbitMQ “处理完了”，否则消息会留在队列 | 对消息可靠性要求高的场景（比如订单支付） |

- **记忆**：`autoAck=true` = 自动删，`false` = 手动删（要配 `basicAck`）。

**3. 参数 3：**`deliverCallback`**（消息处理逻辑，DeliverCallback 类型）**

- **含义**：这是一个 “回调函数”—— 当消费者从队列拿到消息时，RabbitMQ 会自动调用这个方法，你可以在这里写「消息的业务处理逻辑」（比如解析消息、存数据库等）。
- **代码例子**：

```
DeliverCallback deliverCallback = (consumerTag, delivery) -> {
    String message = new String(delivery.getBody(), "UTF-8");
    System.out.println("收到消息：" + message);
    // 业务逻辑：比如处理订单、更新数据等
    
    // 若 autoAck=false，必须手动确认
    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
};
```

- **记忆**：“拿到消息后，要做啥，写这里”。

**4. 参数 4：**`cancelCallback`**（消费取消逻辑，CancelCallback 类型）**

- **含义**：也是一个回调函数 —— 当消费者的订阅被 “取消” 时（比如队列被删除、连接断开），RabbitMQ 会调用这个方法，你可以在这里写「异常处理 / 重连逻辑」。
- **代码例子**：

```
CancelCallback cancelCallback = (consumerTag) -> {
    System.out.println("消费被取消了，consumerTag：" + consumerTag);
    // 比如记录日志、尝试重新订阅队列
};
```

- **记忆**：“消费断了，要咋处理，写这里”。

**完整例子 + 流程总结**

```
// 1. 订阅队列：autoAck=false（手动确认）
channel.basicConsume(
    "订单队列",          // 订“订单队列”
    false,               // 手动确认（要调用basicAck）
    (tag, delivery) -> { // 拿到消息后处理
        String order = new String(delivery.getBody());
        System.out.println("处理订单：" + order);
        // 处理完手动确认
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    },
    (tag) -> {           // 消费被取消时处理
        System.out.println("订单消费被取消，tag：" + tag);
    }
);
```

##### 流程：

1. 消费者调用 `basicConsume` 订阅队列；
2. 队列有消息时，RabbitMQ 把消息推给消费者，触发 `deliverCallback` 处理业务；
3. 若 `autoAck=false`，处理完调用 `basicAck` 告诉 RabbitMQ “删消息”；
4. 若订阅断了，触发 `cancelCallback` 做异常处理。

## Note

- 一个队列中一个消息只能被消费一次
- 不同的rountingkey路由的方式不一样
- 为了保证消息在发送过程中不丢失，RabbitMQ 引入了消息应答机制
- 队列本质上是一个大的消息缓冲区
- 确保消息不会丢失需要做两件事：我们需要**将队列和消息都标记为持久化**
- 队列把 durable 参数设置为持久化，会有D标识

![img](https://cdn.nlark.com/yuque/0/2025/png/56763514/1764864438050-2d0a3e90-e2a9-448f-8933-29994e13addc.png)

- 发布确认模式最大的好处在于它是异步的
- binding 是 exchange 和 queue 之间的桥梁