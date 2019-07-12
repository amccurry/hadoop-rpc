package hadoop.rpc.example;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hadoop.rpc.HadoopRpcServer;

public class ExampleRpcServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleRpcApi.class);

  public static void main(String[] args) throws Exception {

    ExampleRpcApi instance = new ExampleRpcApi() {
      @Override
      public String echo(String s) throws IOException {
        LOGGER.info("echo {} from {} at {}", s, UserGroupInformation.getCurrentUser(), Server.getRemoteIp());
        return s;
      }
    };

    String user = args[0];
    String path = args[1];

    Configuration configuration = new Configuration();
    UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(user, path);
    ugi.doAs((PrivilegedExceptionAction<Void>) () -> {
      LOGGER.info("Creating server with ugi {}", UserGroupInformation.getCurrentUser());
      Server server = HadoopRpcServer.createServer(configuration, "0.0.0.0", 9000, 10, ExampleRpcApi.class, instance);
      server.start();
      server.join();
      return null;
    });
  }
}
