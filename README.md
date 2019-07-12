# hadoop-rpc

This project simplifies the setup of Hadoop RPC client and servers while using Kerberos.  See the example package for more information.

## RPC Interface

```
@KerberosInfo(serverPrincipal = "hadoop.rpc.example.ExampleRpcApi")
@ProtocolInfo(protocolName = "hadoop.rpc.example.ExampleRpcApi", protocolVersion = 1)
public interface ExampleRpcApi {
  String echo(String s) throws IOException;
}
```

## RPC Client

```
Configuration conf = new Configuration();
HadoopRpcClient<ExampleRpcApi> rpcClient = new HadoopRpcClient<>(conf, ExampleRpcApi.class);
ExampleRpcApi client = rpcClient.getClient("localhost", 9000);
```

## RPC Server

```
ExampleRpcApi instance = new ExampleRpcApi() {
  @Override
  public String echo(String s) throws IOException {
    LOGGER.info("echo {} from {} at {}", s, UserGroupInformation.getCurrentUser(), Server.getRemoteIp());
    return s;
  }
};

Configuration configuration = getConf();
UserGroupInformation ugi = getUgi();
ugi.doAs((PrivilegedExceptionAction<Void>) () -> {
  LOGGER.info("Creating server with ugi {}", UserGroupInformation.getCurrentUser());
  Server server = HadoopRpcServer.createServer(configuration, "0.0.0.0", 9000, 10, ExampleRpcApi.class, instance);
  server.start();
  server.join();
  return null;
});
```
