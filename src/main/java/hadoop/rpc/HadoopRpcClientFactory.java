package hadoop.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.net.SocketFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtocolProxy;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * The Hadoop RPC client factory.
 */
public class HadoopRpcClientFactory<K> {

  private static final String PATTERN = ".pattern";
  private final Configuration _conf;
  private final Class<K> _clazz;

  public HadoopRpcClientFactory(Configuration conf, Class<K> clazz) {
    _conf = conf;
    _clazz = clazz;
  }

  /**
   * Create a new client.
   * 
   * @param address
   *          of remote server.
   * @return the new client.
   * @throws IOException
   * @throws InterruptedException
   */
  public K getClient(InetSocketAddress address) throws IOException, InterruptedException {
    return getClient(address, _conf, UserGroupInformation.getCurrentUser(), _clazz);
  }

  /**
   * Create a new client.
   * 
   * @param host
   *          of remote server.
   * @param port
   *          of remote server.
   * @return the new client.
   * @throws IOException
   * @throws InterruptedException
   */
  public K getClient(String host, int port) throws IOException, InterruptedException {
    return getClient(new InetSocketAddress(host, port));
  }

  private static <T> T getClient(InetSocketAddress address, Configuration conf, UserGroupInformation ugi,
      Class<T> clazz) throws IOException, InterruptedException {
    setupConfigDefaults(conf, clazz);
    UserGroupInformation.setConfiguration(conf);
    SocketFactory socketFactory = NetUtils.getDefaultSocketFactory(conf);
    long protocolVersion = RPC.getProtocolVersion(clazz);
    ProtocolProxy<T> protocolProxy = RPC.getProtocolProxy(clazz, protocolVersion, address, ugi, conf, socketFactory);
    return protocolProxy.getProxy();
  }

  private static String getServerPrincipal(Class<?> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException("null clazz");
    }
    KerberosInfo anno = clazz.getAnnotation(KerberosInfo.class);
    if (anno != null) {
      return anno.serverPrincipal();
    }
    return null;
  }

  private static <T> void setupConfigDefaults(Configuration configuration, Class<T> clazz) {
    String serverPrincipal = getServerPrincipal(clazz);
    if (configuration.get(serverPrincipal + PATTERN) == null) {
      configuration.set(serverPrincipal + PATTERN, "*");
    }
  }

}
