![img](https://cdn.nlark.com/yuque/0/2026/png/56763514/1769058694510-00eca766-a1a5-4bf2-ade4-4121f93cec68.png)

#### @cacheput的特性

![添加缓存](https://cdn.nlark.com/yuque/0/2026/png/56763514/1769089508875-a5ecf74a-d90c-4012-9522-4b2154cfe2fb.png?x-oss-process=image%2Fcrop%2Cx_0%2Cy_0%2Cw_344%2Ch_108)![Id在redis结构 图一](https://cdn.nlark.com/yuque/0/2026/png/56763514/1769089761929-9d6ead43-9654-4983-aa2c-e73bbd776df4.png)

当添加这个注解后就往redis自动添加该id保存信息

usercache::2在redis的结果是usercache包下的[empty]包下的usercache::2

#### @cacheable的特性

在方法执行前先查询缓存中是否有数据，如果有数据，则直接返回redis缓存数据

![idea代码 图一](https://cdn.nlark.com/yuque/0/2026/png/56763514/1769088410128-b5646ebe-e5c0-4e4c-bef8-c4986cc75717.png)![redis页面 图二](https://cdn.nlark.com/yuque/0/2026/png/56763514/1769088443683-0de748d1-e613-4788-9ef5-91e4d5e56aac.png)

如图，如果发送查询id=1的请求（图二已经有过他的缓存），则不会进入getbyid方法，不会去数据库查询直接在缓存返回数据

![删除id 图三](https://cdn.nlark.com/yuque/0/2026/png/56763514/1769088621171-cddd52da-bffb-47bc-a755-bafb1ffaa3f6.png)

如果没有缓存数据（例如图三将id删除），则调用getbyid（去数据库查询）方法并将方法返回值放到缓存中

#### @cacheevict的特性

![删除一个缓存](https://cdn.nlark.com/yuque/0/2026/png/56763514/1769089103841-22c74325-9c58-4879-9d9e-b0093d05d18f.png)![删除多个缓存](https://cdn.nlark.com/yuque/0/2026/png/56763514/1769088986692-31531054-60a6-4ff9-8d66-fee909efad12.png)

这个注解都是方法执行完后才执行，比如当发送删除id请求，先执行usermapper的deletebyid方法，将该id在数据库中删除，然后等整个方法执行完毕，就去缓存中删除该id