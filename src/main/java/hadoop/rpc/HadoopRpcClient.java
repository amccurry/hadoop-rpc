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

public class HadoopRpcClient<K> {

  private final Configuration _conf;
  private final Class<K> _clazz;

  public HadoopRpcClient(Configuration conf, Class<K> clazz) {
    _conf = conf;
    _clazz = clazz;
  }

  public K getClient(InetSocketAddress address) throws IOException, InterruptedException {
    return getClient(address, _conf, UserGroupInformation.getCurrentUser(), _clazz);
  }

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
    if (configuration.get(serverPrincipal + ".pattern") == null) {
      configuration.set(serverPrincipal + ".pattern", "*");
    }
  }

}
