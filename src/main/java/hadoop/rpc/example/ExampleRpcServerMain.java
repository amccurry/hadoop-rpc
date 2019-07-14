package hadoop.rpc.example;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hadoop.rpc.HadoopRpcServerFactory;

public class ExampleRpcServerMain {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleRpcApi.class);

  public static void main(String[] args) throws Exception {

    ExampleRpcApi instance = new ExampleRpcApi() {
      @Override
      public String echo(String s) throws IOException {
        LOGGER.info("echo {} from {} at {}", s, UserGroupInformation.getCurrentUser(), Server.getRemoteIp());
        return s;
      }
    };

    Configuration configuration = new Configuration();
    UserGroupInformation.setConfiguration(configuration);
    UserGroupInformation ugi;
    if (args.length >= 2) {
      String user = args[0];
      String path = args[1];
      ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(user, path);
    } else {
      ugi = UserGroupInformation.getCurrentUser();
    }
    ugi.doAs((PrivilegedExceptionAction<Void>) () -> {
      LOGGER.info("Creating server with ugi {}", UserGroupInformation.getCurrentUser());
      HadoopRpcServerFactory<ExampleRpcApi> factory = new HadoopRpcServerFactory<>(configuration, ExampleRpcApi.class);
      Server server = factory.createServer("0.0.0.0", 9000, 10, instance);
      server.start();
      server.join();
      return null;
    });
  }
}
