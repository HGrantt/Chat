import ChatApp.*;          // The package containing our stubs. 
import org.omg.CosNaming.*; // HelloServer will use the naming service. 
import org.omg.CosNaming.NamingContextPackage.*; // ..for exceptions. 
import org.omg.CORBA.*;     // All CORBA applications need these classes. 
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;
import java.util.*;
 
class ChatImpl extends ChatPOA
{
  private ORB orb;
  List players = new Vector();
  List clients = new Vector();
  List gameplayers = new Vector();
  List colors = new Vector();
  char[][] board = new char[10][];
  
  public ChatImpl()
  {
    board[0] = new char[10];
    board[1] = new char[10];
    board[2] = new char[10];
    board[3] = new char[10];
    board[4] = new char[10];
    board[5] = new char[10];
    board[6] = new char[10];
    board[7] = new char[10];
    board[8] = new char[10];
    board[9] = new char[10];
    for (int i=0; i<10; ++i)
    {
      for (int j=0; j<10; ++j)
      {
        board[i][j] = ' ';
      }
    }
  }

  public void setORB(ORB orb_val)
  {
    orb = orb_val;
  }

  public boolean post(String name, String message)
  {
    broadcast(name + ": " + message);
    return true;
  }
  public boolean join(ChatCallback callobj, String name)
  {
    for( int i=0; i < players.size(); ++i)
    {
      if( name.equals((String)players.get(i)) ||  callobj.equals((ChatCallback)clients.get(i)))
      {
        callobj.callback("We already have a " + name);
        return false;
      }
    }
    clients.add(callobj);
    players.add(name);
    if(name.equals("Harald") || name.equals("Henrik"))
    {
      broadcast("Kingen e här! välkommen " + name + "!");
    }
    else
    {
      broadcast("Välkommen " + name);
    }
    return true;
  }
  public boolean list(ChatCallback callobj)
  {
    for( int i=0; i < players.size(); ++i)
    {
      callobj.callback((String)players.get(i));
    }
    return true;
  }
  public boolean quit(ChatCallback callobj)
  {
    for (int i=0; i<players.size(); ++i)
    {
      if( callobj.equals((ChatCallback)clients.get(i)) )
      {
        broadcast("Goodbye " + (String)players.get(i));
        players.remove(i);
        clients.remove(i);
        return true;
      }
    }
    return false;
  }
  
  public boolean game( ChatCallback callobj, char color )
  {
    for( int i=0; i < gameplayers.size(); ++i)
    {
      if( callobj.equals((ChatCallback)gameplayers.get(i)) && callobj.equals((ChatCallback)players.get(i)) )
      {
        callobj.callback("You cannot join");
        return false;
      }
    }
    callobj.callback("You have joined");
    gameplayers.add(callobj);
    colors.add(color);
    return true;
  }
  
  public boolean plop( ChatCallback callobj, int row )
  {
    int color=-1;
    for( int i=0; i< gameplayers.size(); ++i )
    {
      if( callobj.equals((ChatCallback)gameplayers.get(i)) )
      {
        color = i;
        break;
      }
    }
    if( color == -1 )
    {
      callobj.callback("You have not joined");
      return false;
    }
    for( int i=0; i<10; ++i)
    {
      if( board[row][i] == ' ')
      {
        board[row][i] = (char)colors.get(color);
        break;
      }
    }
    if( check_lod()
       || check_wave()
       || check_dn()
       || check_dnr()
       || check_da()
       || check_dar())
    {
      clear_board();
      gameplayers.clear();
      colors.clear();
    }
    print_board();
    return true;
  }

  private boolean check_lod()
  {
    char now = ' ';
    for( int i=0; i<10; ++i )
    {
      int count = 0;
      for( int j=0; j<10; ++j )
      {
        if( board[i][j] == now && now != ' ' )
        {
          count += 1;
        }
        else if( board[i][j] != now )
        {
          count = 1;
          now = board[i][j];
        }
        if( count == 5 )
        {
          broadcast("Vinnaren är : " + now + " L");
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean check_wave()
  {
    char now = ' ';
    for( int i=0; i<10; ++i )
    {
      int count = 0;
      for( int j=0; j<10; ++j )
      {
        if( board[j][i] == now && now != ' ' )
        {
          count += 1;
        }
        else if( board[j][i] != now )
        {
          count = 1;
          now = board[j][i];
        }
        if( count == 5 )
        {
          broadcast("Vinnaren är : " + now + " W");
          return true;
        }
      }
    }
    return false;
  }
  private boolean check_dn()
  {
    char now = ' ';
    for( int i=0; i<=9; ++i )
    {
      int count = 0;
      for( int j=0; j<=i; ++j )
      {
        if( board[i-j][j] == now && now != ' ' )
        {
          count += 1;
        }
        else if( board[i-j][j] != now )
        {
          count = 1;
          now = board[i-j][j];
        }
        if( count == 5 )
        {
          broadcast("Vinnaren är : " + now + " dia");
          return true;
        }
      }
    }
    return false;
  }

  
  private boolean check_dnr()
  {
    char now = ' ';
    for( int i=0; i<=9; ++i )
    {
      int count = 0;
      for( int j=0; j<=i; ++j )
      {
        if( board[9-i+j][9-j] == now && now != ' ' )
        {
          count += 1;
        }
        else if( board[9-i+j][9-j] != now )
        {
          count = 1;
          now = board[9-i+j][9-j];
        }
        if( count == 5 )
        {
          broadcast("Vinnaren är : " + now + " diar");
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean check_da()
  {
    char now = ' ';
    for( int i=0; i<=9; ++i )
    {
      int count = 0;
      for( int j=0; j<=i; ++j )
      {
        if( board[i-j][9-j] == now && now != ' ' )
        {
          count += 1;
        }
        else if( board[i-j][9-j] != now )
        {
          count = 1;
          now = board[i-j][9-j];
        }
        if( count == 5 )
        {
          broadcast("Vinnaren är : " + now + " N");
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean check_dar()
  {
    char now = ' ';
    for( int i=0; i<=9; ++i )
    {
      int count = 0;
      for( int j=0; j<=i; ++j )
      {
        if( board[9-i+j][j] == now && now != ' ' )
        {
          count += 1;
        }
        else if( board[9-i+j][j] != now )
        {
          count = 1;
          now = board[9-i+j][j];
        }
        if( count == 5 )
        {
          broadcast("Vinnaren är : " + now + " Nr");
          return true;
        }
      }
    }
    return false;
  }

  
  private boolean print_board()
  {
    for( int i=9; i>=0; --i )
    {
      String row="";
      for( int j=0; j<10; ++j )
      {
        row += board[j][i];
      }
      broadcast(row);
    }
    broadcast("0123456789");
    return true;
  }

  private void clear_board()
  {
    for( int i=0; i<=9; ++i )
    {
      for( int j=0; j<=9; ++j)
      {
        board[i][j] = ' ';
      }
    }
  }
  
  private void broadcast(String message)
  {
    for (int i=0; i<clients.size(); ++i)
    {
      ChatCallback cc = (ChatCallback)clients.get(i);
      cc.callback(message);
    }
  }
}

public class ChatServer 
{
  public static void main(String args[]) 
  {
    try { 
      // create and initialize the ORB
      ORB orb = ORB.init(args, null); 

      // create servant (impl) and register it with the ORB
      ChatImpl chatImpl = new ChatImpl();
      chatImpl.setORB(orb); 

      // get reference to rootpoa & activate the POAManager
      POA rootpoa = 
        POAHelper.narrow(orb.resolve_initial_references("RootPOA"));  
      rootpoa.the_POAManager().activate(); 

      // get the root naming context
      org.omg.CORBA.Object objRef = 
        orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      // obtain object reference from the servant (impl)
      org.omg.CORBA.Object ref = 
        rootpoa.servant_to_reference(chatImpl);
      Chat cref = ChatHelper.narrow(ref);

      // bind the object reference in naming
      String name = "Chat";
      NameComponent path[] = ncRef.to_name(name);
      ncRef.rebind(path, cref);

      // Application code goes below
      System.out.println("ChatServer ready and waiting ...");
	    
      // wait for invocations from clients
      orb.run();
    }
	    
    catch(Exception e) {
      System.err.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }

    System.out.println("ChatServer Exiting ...");
  }

}
