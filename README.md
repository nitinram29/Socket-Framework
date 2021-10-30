"# Socket-Framework" 

`Requirments :-
	
	1> Gradle (6.5)

`Sample server side code
	
	// import jar file from server\build\libs\* and common\build\libs\*
	
	import com.sframework.server.*;
	import com.sframework.server.annotations.*;
	@Path("/server")
	public class server
	{
		@Path("/fun")
		public String fun(String p1)
		{
			if(p1.equalsIgnoreCase("What is my name?")) return "Nitin";
			else return "No Data!";
		}

		public static void main(String[] args)
		{
			SFrameworkServer server = new SFrameworkServer();
			server.registerClass(server.class);
			server.start();
		}
	}

`Sample client side code
	
	// import jar file from common\build\libs\* and client\build\libs\*
	
	import com.sframework.client.*;
	import com.sframework.common.exceptions.*;
	public class client
	{
		public static void main(String[] aa)
		{
			try
			{
				SFrameworkClient client = new SFrameworkClient();
				String ans = (String)client.execute("/server/fun",aa[0]); // "/class_annotation/function_annnotation" , parameters for that function
				System.out.println(ans);
			}catch(Throwable t)
			{
				System.out.println(t.getMessage());
			}
		}
	}

**to run your code take respective jar files in classpath**

