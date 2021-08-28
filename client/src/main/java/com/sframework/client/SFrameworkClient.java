package com.sframework.client;
import com.sframework.common.*;
import com.sframework.common.exceptions.*;
import java.nio.charset.*;
import java.net.*;
import java.io.*;

public class SFrameworkClient 
{
public Object execute(String servicePath,Object ...arguments) throws Throwable
{
try
{
Request request = new Request();
request.setServicePath(servicePath);
request.setArguments(arguments);
String requestJSONString = JSONUtil.toJSON(request);

byte objectBytes[] = requestJSONString.getBytes(StandardCharsets.UTF_8);
int requestLength  = objectBytes.length;
byte header[] = new byte[1024];
int x = requestLength;
int i = 1023;
while(x>0)
{
header[i] = (byte)(x%10);
x = x/10;
i--;
}
Socket socket =  new Socket("localhost",5500);
OutputStream os = socket.getOutputStream();
os.write(header,0,1024); // kya bhejna h, kha se bhejna h, kitna bhejna h
os.flush();

//to get confirmation of sending header to server
InputStream is = socket.getInputStream();
byte ack[] = new byte[1]; // to get confirmation of geting header from server
int bytesReadCount;
while(true)
{
bytesReadCount = is.read(ack); 
if(bytesReadCount == -1) continue;
break;
}

// sending data to server
int bytesToSend = requestLength;
int chunkSize = 1024;
int j=0;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize = bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j = j + chunkSize;
}

// header from server which have value of size of response from server
int bytesToReceive = 1024;
i=0;
byte tmp[] = new byte[1024];
j = 0;
while(j<bytesToReceive)
{
bytesReadCount = is.read(tmp); // jo bhi .read() method se aaega to tmp[] m index wise save hota rhega
if(bytesReadCount == -1) continue;
for(int k=0;k<bytesReadCount;k++)
{
header[i] = tmp[k];
i++;
}
j = j + bytesReadCount;
}

int responseLength = 0; 
i = 1; 
j = 1023;
while(j>=0)
{
responseLength = (header[j]*i) + responseLength;
i = i*10;
j--;
}

// sending confirmation to server for reciving header
ack[0] = 1;
os.write(ack);
os.flush();

// response from server
bytesToReceive = responseLength;
i=0;
byte response[] = new byte[responseLength];
j = 0;
while(j<bytesToReceive)
{
bytesReadCount = is.read(tmp); // jo bhi .read() method se aaega vo tmp[] m index wise save hota rhega
if(bytesReadCount == -1) continue;
for(int k=0;k<bytesReadCount;k++)
{
response[i] = tmp[k];
i++;
}
j = j + bytesReadCount;
}

// send confirmation of receiving response
ack[0] = 1;
os.write(ack);
os.flush();
socket.close();
String responseJSONString = new String(response,StandardCharsets.UTF_8);
Response responseObject = JSONUtil.fromJSON(responseJSONString,Response.class);

if(responseObject.getSuccess())
{
return responseObject.getResult();
}
else
{
throw responseObject.getException();
}
}catch(Exception e)
{
System.out.println(e);
}
return null;
}
}