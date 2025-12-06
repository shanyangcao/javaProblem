- ## problem

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

  #### 队列设置 TTL和消息设置 TTL的区别

  ##### 队列 TTL（Queue TTL）

  你做了一个 “临时活动通知队列”，只在活动期间（比如 1 天）有消费者订阅。活动结束后，这个队列可能没人用了，但如果忘记手动删除，会一直占用 RabbitMQ 资源 —— 这时候用**队列 TTL**。

  **代码（声明队列时设置 TTL）：**

  ```
  // 声明队列时，给队列设置TTL：闲置1天后自动删除（1天=86400000毫秒）
  Map<String, Object> queueArgs = new HashMap<>();
  queueArgs.put("x-expires", 86400000); // 队列TTL的参数是x-expires

  channel.queueDeclare(
      "临时活动通知队列", // 队列名
      false, // 不持久化
      false, // 不独占
      false, // 不自动删除（这里用x-expires控制自动删除）
      queueArgs // 队列参数（包含x-expires）
  );
  ```

  **实际效果：**

  - 活动期间：队列有消费者订阅、有消息流转 → 队列处于 “活跃状态”，TTL 不生效；
  - 活动结束后：消费者都断开了，队列里没消息，也没人操作 → 队列进入 “闲置状态”，从闲置开始计时，1 天后队列被 RabbitMQ**自动删除**（队列没了，里面的残留消息也会被一起删）。

  ##### 消息 TTL（Message TTL）

  你做了一个 “订单支付提醒队列”，要求 “订单创建后 30 分钟内没支付，提醒消息就失效”—— 这时候用**消息 TTL**。

  **代码（两种设置方式）：**

  **方式 1：给队列所有消息设默认 TTL（队列级默认）**

  ```
  // 声明队列时，设置该队列所有消息的默认TTL：30分钟（1800000毫秒）
  Map<String, Object> queueArgs = new HashMap<>();
  queueArgs.put("x-message-ttl", 1800000); // 消息TTL的队列默认参数是x-message-ttl

  channel.queueDeclare(
      "订单支付提醒队列",
      true, // 持久化
      false,
      false,
      queueArgs
  );

  // 发送消息：不需要额外设置，自动继承队列的默认TTL（30分钟）
  channel.basicPublish(
      "", // 交换机
      "订单支付提醒队列", // 队列名
      null, // 消息属性（不用额外设expiration）
      "订单123请在30分钟内支付".getBytes()
  );
  ```

  **方式 2：给单条消息单独设 TTL（消息级自定义）**

  ```
  // 发送消息时，给这条消息单独设TTL：15分钟（900000毫秒）
  AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
      .expiration("900000") // 单条消息的TTL参数是expiration
      .build();

  channel.basicPublish(
      "",
      "订单支付提醒队列",
      properties, // 消息属性中指定TTL
      "订单456请在15分钟内支付（加急）".getBytes()
  );
  ```

  **实际效果：**

  - 订单 123 的消息：进入队列后，30 分钟内没被消费者处理 → 消息过期，若队列绑定了死信队列，消息会被转到死信队列（后续可以统计 “超时未支付订单”）；
  - 订单 456 的消息：单独设了 15 分钟 TTL → 15 分钟没处理就过期，和队列默认 TTL 无关；
  - 注意：队列本身不会被删，只是**消息过期**。

  #### 生产者中RabbitTemplate方法含义

  RabbitMQ 本身没有 “原生延迟队列”，但可以通过「延迟交换机（Delayed Exchange）」实现延迟消息：发送消息时给消息设置「延迟时长」，交换机收到消息后不立即转发到队列，而是等延迟时间到了再转发。这段代码就是给消息绑定 “延迟属性”，实现延迟发送。

  **逐行拆解代码**

  ```
  rabbitTemplate.convertAndSend(
      DELAY_EXCHANGE_NAME,  // 参数1：延迟交换机名称
      DELAY_ROUTING_KEY,    // 参数2：延迟交换机绑定的路由键
      message,              // 参数3：要发送的消息内容（比如字符串、对象）
      msg -> {              // 参数4：消息后置处理器（对消息属性做自定义配置）
          // 给消息设置延迟时长，单位：毫秒
          msg.getMessageProperties().setDelay(delayTime);
          return msg;       // 返回修改后的消息
      }
  );
  ```

  **每个部分的具体含义：**

  | **代码片段**          | **核心作用**                                                 |
  | --------------------- | ------------------------------------------------------------ |
  | `DELAY_EXCHANGE_NAME` | 指定消息要发送到「延迟交换机」（必须是提前声明的 `x-delayed-type` 类型交换机） |
  | `DELAY_ROUTING_KEY`   | 延迟交换机根据这个路由键，知道延迟时间到后要把消息转发到哪个队列 |
  | `message`             | 实际要发送的业务内容（比如 “订单 123 超时未支付提醒”、用户 ID 等） |
  | `msg -> { ... }`      | 消息后置处理器（Lambda 表达式）：发送消息前，对消息的属性做最后修改 |
  | `setDelay(delayTime)` | 给消息设置「延迟时长」（单位：毫秒），比如 `delayTime=5000` 就是延迟 5 秒 |

  **核心逻辑（人话版）**

  “我要把 `message` 这条消息发送到 `DELAY_EXCHANGE_NAME` 这个延迟交换机，并且告诉交换机：‘先别转发这条消息，等 `delayTime` 毫秒后，再按 `DELAY_ROUTING_KEY` 路由到对应的队列里’”。

  **完整场景举例（更易理解）**

  比如你要实现 “订单创建后 30 分钟提醒用户支付”，代码可以这么写：

  ```
  // 1. 定义常量（提前声明好延迟交换机）
  public static final String DELAY_EXCHANGE_NAME = "order.delay.exchange"; // 延迟交换机名
  public static final String DELAY_ROUTING_KEY = "order.pay.remind";       // 路由键
  // 2. 延迟时长：30分钟 = 30*60*1000 = 1800000 毫秒
  long delayTime = 1800000;
  // 3. 要发送的消息内容（比如订单ID）
  String message = "订单ID：123456，请30分钟内完成支付";

  // 4. 发送延迟消息
  rabbitTemplate.convertAndSend(
      DELAY_EXCHANGE_NAME,
      DELAY_ROUTING_KEY,
      message,
      msg -> {
          msg.getMessageProperties().setDelay((int) delayTime); // 设置30分钟延迟
          return msg;
      }
  );
  ```

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
  - TTL 是 RabbitMQ 中一个消息或者队列的属性
  - @RabbitListener(queues = DELAYED_QUEUE_NAME)的作用是指定监听的队列名称为 DELAYED_QUEUE_NAME，队列有消息时自动调用注解下面的方法
  - 只有队列和消息都设置优先级，才可以进行优先级排序