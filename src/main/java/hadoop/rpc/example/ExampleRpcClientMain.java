package hadoop.rpc.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import hadoop.rpc.HadoopRpcClient;

public class ExampleRpcClientMain {

  public static void main(String[] args) throws IOException, InterruptedException {
    Configuration conf = new Configuration();
    HadoopRpcClient<ExampleRpcApi> rpcClient = new HadoopRpcClient<>(conf, ExampleRpcApi.class);
    ExampleRpcApi client = rpcClient.getClient("localhost", 9000);
    System.out.println(client.echo("test"));
  }

}
