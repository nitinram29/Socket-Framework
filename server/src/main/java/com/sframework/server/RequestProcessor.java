package com.sframework.server;
import com.sframework.common.*;
import java.nio.charset.*;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;

class  RequestProcessor extends Thread
{
private SFrameworkServer server;
private Socket socket;
RequestProcessor(SFrameworkServer server,Socket socket)
{
this.server = server;
this.socket = socket;
start();
}
public void run()
{
try
{
InputStream is;
OutputStream os;
StringBuffer sb;
int x;

// receiving header from client
is = socket.getInputStream();
byte tmp[] = new byte[1024];
byte header[] = new byte[1024];
int i=0;
int j=0;
int bytesToReceive = 1024;
int byteReadCount = 0;
while(j<bytesToReceive)
{
byteReadCount = is.read(tmp);
if(byteReadCount == -1) continue;
for(int k=0;k<byteReadCount;k++)
{
header[i] = tmp[k];
i++;
}
j = j + byteReadCount;
}

int requestLenght = 0;
j=1023;
i = 1;
while(j>=0)
{
requestLenght = requestLenght + (header[j]*i);
j--;
i *= 10;
}

//sending confirmation to server of receiving header
byte ack[] = new byte[1];
ack[0] = 1;
os = socket.getOutputStream();
os.write(ack,0,1);
os.flush();

// receiving data from cilent 
byte request[] = new byte[requestLenght];
bytesToReceive = requestLenght;
i=0;
j=0;

while(j<bytesToReceive)
{
byteReadCount = is.read(tmp);
if(byteReadCount == -1) continue;
for(int k=0;k<byteReadCount;k++)
{
request[i] = tmp[k];
i++;
}
j = j + byteReadCount;
}
String requestJSONString = new String(request,StandardCharsets.UTF_8);
Request requestObject = JSONUtil.fromJSON(requestJSONString,Request.class);
// the requestObject contains serivsePath and arguments
// we want the reference of the TCPService that contains the
// Class ref and method ref
TCPService tcpService = this.server.getTCPService(requestObject.getServicePath());
Response responseObject = new Response();
if(tcpService == null)
{
responseObject.setSuccess(false);
responseObject.setResult("");
responseObject.setException(new RuntimeException("Invalid path : " + requestObject.getServicePath()));
}
else
{
Class c = tcpService.c;
Method m = tcpService.method;
try
{
Object serviceObject = c.newInstance();
Object result = m.invoke(serviceObject,requestObject.getArguments());
responseObject.setSuccess(true);
responseObject.setResult(result);
responseObject.setException(null);
}catch(InstantiationException instantiationException)
{
responseObject.setSuccess(false);
responseObject.setResult("");
responseObject.setException(new RuntimeException("Unable to create object of service class associated with path : " + requestObject.getServicePath()));
}
catch(IllegalAccessException illegalAccessException)
{
responseObject.setSuccess(false);
responseObject.setResult("");
responseObject.setException(new RuntimeException("Unable to create object of service class associated with path : " + requestObject.getServicePath()));
}
catch(InvocationTargetException invocationTargetException)
{
Throwable t = invocationTargetException.getCause();
responseObject.setSuccess(false);
responseObject.setResult("");
responseObject.setException(t);
}
}

//sending response header
String responseJSONString = JSONUtil.toJSON(responseObject);
byte objectBytes[] = responseJSONString.getBytes(StandardCharsets.UTF_8);
header = new byte[1024];
int responseLength = objectBytes.length;
x = responseLength;
i = 1023;
while(x>0)
{
header[i] = (byte)(x%10);
x = x/10;
i--;
}
os.write(header,0,1024);
os.flush();

// getting confirmation from client of sending header
while(true)
{
byteReadCount = is.read();
if(byteReadCount == -1) continue;
break;
}
  
// sending response
int bytesToSend = responseLength;
int chunkSize = 1024;
j = 0;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize = bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j = j + chunkSize;
}

// getting confirmation of sending response
while(true)
{
byteReadCount = is.read(ack);
if(byteReadCount == -1) continue;
break;
}
socket.close();

}catch(IOException e)
{
System.out.println(e);
}
} 
}