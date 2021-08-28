"# Socket-Framework" 

`Requirments :-
1> Gradle (6.5)

`Sample server side code
	// import jar file from server\build\libs\* and common\build\libs\*
	@Path("/eg1")
	class eg1
	{
		@Path("/fun")
		public int fun(int p1)
		{
			return p1*p1;
		}
	}

	public static void main(String[] args)
	{
		SFrameworkServer server = new SFrameworkServer();
		server.registerClass(eg1.class);
		server.start();
	}

`Sample client side code
	// import jar file from server\build\libs\* and client\build\libs\*
	class client
	{
		public static void main(String[] aa)
		{
			try
			{
				SFrameworkClient client = new SFrameworkClient();
				int ans = (String)client.execute("/eg1/fun",aa[0]); // "/class_annotation/function_annnotation" , parameters for that function
				System.out.println(ans);
			}catch(Throwable t)
			{
				System.out.println(t.getMessage());
			}
		}
	}

**to run your code take respective jar files in classpath**

