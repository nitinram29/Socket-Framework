package com.sframework.common;
import com.google.gson.*;

public class JSONUtil
{
private JSONUtil(){}

public static String toJSON(java.io.Serializable serializable)
{
try
{
Gson gson = new Gson();
return gson.toJson(serializable);
}catch(Exception e)
{
return "{}";
}
}// toJSON



//Bulb b = JSONUtil.fromJSON(jsonString,Bulb.Class)
//T = Bulb
public static <T> T fromJSON(String jsonString,Class<T> c)
{
try
{
Gson gson = new Gson();
return gson.fromJson(jsonString,c);
}catch(Exception e)
{
return null;
}
}// fromGSON
}