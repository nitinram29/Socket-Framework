package com.sframework.common.exceptions;
public class NetworkException extends Exception // Exception class is Serializable
{
public NetworkException()
{
// it is imp because jo Matter ko Deserialize krne ka code h vo parametrised constructor thodi call krega
// do nothing
}
public NetworkException(String message)
{
super(message);
}

}