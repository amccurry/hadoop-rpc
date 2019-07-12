package hadoop.rpc.example;

import java.io.IOException;

import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.security.KerberosInfo;

@KerberosInfo(serverPrincipal = "hadoop.rpc.example.ExampleRpcApi")
@ProtocolInfo(protocolName = "hadoop.rpc.example.ExampleRpcApi", protocolVersion = 1)
public interface ExampleRpcApi {

  String echo(String s) throws IOException;

}
