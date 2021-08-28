package com.sframework.server;
import com.sframework.server.annotations.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;

public class SFrameworkServer
{
private ServerSocket serverSocket;
private Set<Class> tcpNetworkServiceClasses;
private Map<String,TCPService> services;

public SFrameworkServer()
{
tcpNetworkServiceClasses = new HashSet<>();
services = new HashMap<>();
}

public void registerClass(Class c)
{
Path pathOnType;
Path pathOnMethod;
Method methods[];
String fullPath="";
TCPService tcpService=null;
pathOnType = (Path)c.getAnnotation(Path.class);
if(pathOnType==null) return;
methods = c.getMethods();
int methodWithPathAnnoation = 0;
for(Method m:methods)
{
pathOnMethod = (Path)m.getAnnotation(Path.class);
if(pathOnMethod==null) continue;
methodWithPathAnnoation++;
fullPath = pathOnType.value()+pathOnMethod.value();

tcpService = new TCPService();
tcpService.c = c;
tcpService.method = m;
tcpService.path = fullPath;
services.put(fullPath,tcpService);
}
if(methodWithPathAnnoation>0) tcpNetworkServiceClasses.add(c);

}

//TCPService is a wapper that we create
public TCPService getTCPService(String path)
{
if(services.containsKey(path)) return services.get(path);
else return null;
}

public void start()
{
try
{
serverSocket = new ServerSocket(5500);
Socket socket;
RequestProcessor requestProcessor;
while(true)
{
System.out.println("Server is listening on port 5500");
socket = serverSocket.accept();
requestProcessor = new RequestProcessor(this,socket);
}
}catch(Exception e)
{
System.out.println(e);
}
}//start
}
