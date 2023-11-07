# NettySocksProxy

## 编译打包

可以直接使用 `mvn clean package` 命令进行打包。得到 `jar` 包后通过 `java` 命令运行。

## 打包 Native image

使用如下命令：

```shell
mvn clean package -Pnative
```

### 运行

```shell
./SocksServer -Dport=10089
```

指定端口运行