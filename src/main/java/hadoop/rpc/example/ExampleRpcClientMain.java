package hadoop.rpc.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import hadoop.rpc.HadoopRpcClientFactory;

public class ExampleRpcClientMain {

  public static void main(String[] args) throws IOException, InterruptedException {
    Configuration conf = new Configuration();
    HadoopRpcClientFactory<ExampleRpcApi> rpcClient = new HadoopRpcClientFactory<>(conf, ExampleRpcApi.class);
    ExampleRpcApi client = rpcClient.getClient("localhost", 9000);
    System.out.println(client.echo("test"));
  }

}
