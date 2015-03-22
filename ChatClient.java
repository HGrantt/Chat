import ChatApp.*;          // The package containing our stubs
import org.omg.CosNaming.*; // HelloClient will use the naming service.
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;     // All CORBA applications need these classes.
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;

 
class ChatCallbackImpl extends ChatCallbackPOA
{
  private ORB orb;

  public void setORB(ORB orb_val)
  {
    orb = orb_val;
  }

  public void callback(String notification)
  {
    System.out.println(notification);
  }
}

public class ChatClient
{
  static Chat chatImpl;
    
  public static void main(String args[])
  {
    try {
      //Console console = System.console();
      String input;// = console.readLine("Händish? : ");
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // create servant (impl) and register it with the ORB
      ChatCallbackImpl chatCallbackImpl = new ChatCallbackImpl();
      chatCallbackImpl.setORB(orb);

      // get reference to RootPOA and activate the POAManager
      POA rootpoa = 
        POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      rootpoa.the_POAManager().activate();
	    
      // get the root naming context 
      org.omg.CORBA.Object objRef = 
        orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    
      // resolve the object reference in naming
      String name = "Chat";
      chatImpl = ChatHelper.narrow(ncRef.resolve_str(name));
	    
      // obtain callback reference for registration w/ server
      org.omg.CORBA.Object ref = 
        rootpoa.servant_to_reference(chatCallbackImpl);
      ChatCallback cref = ChatCallbackHelper.narrow(ref);

      boolean connected = false;
      String myname = "";
      // Application code goes below
      while (!"q".equals(input = System.console().readLine("")))
      {
        String command="";
        String param="";
        command = input.substring(0, 4);
        if (input.length() > 4)
        {
          param = input.substring(5);
        }
        if (command.equals("join"))
        {
          if (chatImpl.join(cref, param))
          {
            myname = param;
            connected = true;
          }
        }
        else if (command.equals("game"))
        {
          if( connected )
          {
            chatImpl.game(cref, param.charAt(0));
          }
        }
        else if (command.equals("plop"))
        {
          System.out.println(param);
          chatImpl.plop(cref, Integer.parseInt(param));
        }
        else if (command.equals("post") && connected)
        {
          chatImpl.post(myname, param);
        }
        else if (command.equals("list") && connected)
        {
          chatImpl.list(cref);
        }
        else if (command.equals("quit") && connected)
        {
          chatImpl.quit(cref);
          connected = false;
        }
      }
      //String chat = chatImpl.say(cref, "\n  Hello....");
      //System.out.println(chat);
	    
    } catch(Exception e){
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }
  }
}
