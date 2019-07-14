package hadoop.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.RpcKind;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.security.authorize.Service;

/**
 * The Hadoop RPC server factory.
 */
public class HadoopRpcServerFactory<K> {

  private final Configuration _conf;
  private final Class<K> _clazz;

  public HadoopRpcServerFactory(Configuration conf, Class<K> clazz) {
    _conf = conf;
    _clazz = clazz;
  }

  /**
   * Create a Hadoop RPC server.
   * 
   * @param bindAddress
   *          the local binding ipaddress.
   * @param port
   *          the local binding port.
   * @param numHandlers
   *          the number of thread handlers.
   * @param instance
   *          the RPC instance implementation.
   * @return the server.
   * @throws Exception
   */
  public Server createServer(String bindAddress, int port, int numHandlers, K instance) throws Exception {
    RPC.Server server = new RPC.Builder(_conf).setBindAddress(bindAddress)
                                              .setInstance(instance)
                                              .setProtocol(_clazz)
                                              .setPort(port)
                                              .setNumHandlers(numHandlers)
                                              .build();
    server.addProtocol(RpcKind.RPC_WRITABLE, _clazz, instance);
    server.getServiceAuthorizationManager()
          .refresh(_conf, new PolicyProvider() {
            @Override
            public Service[] getServices() {
              return new Service[] { createNewService(_clazz) };
            }
          });
    return server;
  }

  private static <T> Service createNewService(Class<T> clazz) {
    return new Service(clazz.getName(), clazz) {
    };
  }

}
