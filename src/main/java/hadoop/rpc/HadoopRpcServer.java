package hadoop.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.RpcKind;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.security.authorize.Service;

public class HadoopRpcServer<K> {

  public static <T> Server createServer(Configuration configuration, String bindAddress, int port, int numHandlers,
      Class<T> clazz, T instance) throws Exception {
    RPC.Server server = new RPC.Builder(configuration).setBindAddress(bindAddress)
                                                      .setInstance(instance)
                                                      .setProtocol(clazz)
                                                      .setPort(port)
                                                      .setNumHandlers(numHandlers)
                                                      .build();
    server.addProtocol(RpcKind.RPC_WRITABLE, clazz, instance);
    server.getServiceAuthorizationManager()
          .refresh(configuration, new PolicyProvider() {
            @Override
            public Service[] getServices() {
              return new Service[] { createNewService(clazz) };
            }
          });
    return server;
  }

  private static <T> Service createNewService(Class<T> clazz) {
    return new Service(clazz.getName(), clazz) {
    };
  }

}
