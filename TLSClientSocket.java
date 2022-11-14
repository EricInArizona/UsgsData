/*
// Copyright Eric Chauvin 2021.



// import java.net.Socket;
// import javax.net.ssl.SSLSocket;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;



public class TLSClientSocket
  {
  private TLSSocket ClientSocket;
  private String ErrorString = "";
  private ECTime LastReadWriteTime;
  private InputStream InStream = null;
  private OutputStream OutStream = null;
  private String InBufferS = "";
  private HashMap<String, String> LinesMap;


  protected TLSClientSocket()
    {
    ClientSocket = new TLSSocket();
    LinesMap = new HashMap<String, String>();

    LastReadWriteTime = new ECTime(); // SetToNow();
    }



  protected void FreeEverything()
    {
    if( ClientSocket != null )
      {
      try
      {
      ClientSocket.close();
      }
      catch( Exception e )
        {
        //
        }

      ClientSocket = null;
      }
    }


  protected String GetErrorString()
    {
    String Result = ErrorString;
    ErrorString = "";
    return Result;
    }



  protected double GetLastReadWriteTimeSeconds()
    {
    return LastReadWriteTime.GetSecondsToNow();
    }



  protected boolean IsShutDown()
    {
    if( ClientSocket == null )
      return true;

    if( InStream == null )
      return true;

    if( OutStream == null )
      return true;

    try
    {
    if( ClientSocket.isClosed() )
      {
      FreeEverything();
      return true;
      }

    if( !ClientSocket.isConnected() )
      {
      FreeEverything();
      return true;
      }

    if( ClientSocket.isInputShutdown())
      {
      FreeEverything();
      return true;
      }

    if( ClientSocket.isOutputShutdown())
      {
      FreeEverything();
      return true;
      }

    return false;
    }
    catch( Exception e )
      {
      ClientSocket = null;
      return true;
      }
    }



  protected boolean ConnectToServer( String ServerIP, int Port )
    {
    if( ClientSocket == null )
      return false;

    // 54.201.164.92
    try
    {
    InetSocketAddress SockAddress = new InetSocketAddress( ServerIP, Port );
    ClientSocket.connect( SockAddress, 10000 ); // 30 second time out.
    }
    catch( Exception e )
      {
      ErrorString += "Could not connect to: " + ServerIP + "\r\n";
      ErrorString += e.getMessage() + "\r\n";
      return false;
      }


    // Most clients should wrap their input stream with
    // BufferedInputStream. Callers that do only bulk reads may
    // omit buffering.

    try
    {
    InStream = ClientSocket.getInputStream();
    OutStream = ClientSocket.getOutputStream();
    }
    catch( Exception e )
      {
      ErrorString += "Error: Could not get In/Out Stream.\r\n";
      ErrorString += e.getMessage() + "\r\n";
      // If there was a problem it's very likely that it didn't open
      // either stream.  (As opposed to opening the InStream but
      // being unable to open the OutStream.)
      InStream = null;
      OutStream = null;
      return false;
      }

    return true;
    }




  protected boolean SendString( String Data )
    {
    try
    {
    if( IsShutDown())
      return false;

    byte[] RawData = UTF8Strings.StringToBytes( Data );
    if( RawData == null )
      return false;

    OutStream.write( RawData, 0, RawData.length );
    LastReadWriteTime.SetToNow();
    return true;
    }
    catch( Exception e )
      {
      ErrorString += "Could not send string exception.";
      return false;
      }
    }



  protected void FillIncomingLines()
    {
    ReadToInBuffer();

    for( int Count = 0; Count < 100; Count++ )
      {
      if( !AddNextStringFromInBuffer())
        break;

      }
    }




  protected void ReadToInBuffer()
    {
    if( IsShutDown())
      return;

    try
    {
    // IsShutDown already checked if InStream is null.
    // available() sounds flaky.
    if( 0 == InStream.available())
      return;

    byte[] RawData = new byte[1024 * 64];

    // int TotalRead = 0;
    int BytesRead = InStream.read( RawData, 0, RawData.length );
    if( BytesRead == 0 )
      return;

    // It's possible that this RadData can end in the middle of a UTF8
    // sequence so that it loses a character.  But only if it's sending
    // non-ASCII characters directly here.  Which it's not.
    // It's only sending strings.
    // InMessage.AddToInBuffer( Encoder.GetString( RawData, 0, BytesRead ));
    InBufferS += UTF8Strings.BytesToString( RawData, BytesRead );

    LastReadWriteTime.SetToNow();
    }
    catch( Exception e )
      {
      ErrorString += "Exception in ReadToInBuffer():\r\n";
      ErrorString += e.getMessage() + "\r\n";
      FreeEverything();
      }
    }




  private int InBufferLineEndPosition()
    {
    if( InBufferS.length() < 2 )
      return -1;

    return InBufferS.indexOf( "\r\n" );
    }




  protected boolean AddNextStringFromInBuffer()
    {
    int Where = InBufferLineEndPosition();
    if( Where < 0 )
      {
      /////////////////
      // Don't deal with this limitation here.
      // if( InBufferS.length() > (4096 + 1024) )
      //   {
      //   ErrorString += "In AddNextStringFromInBuffer(). Line is too long for the protocol.\r\n";
      //   FreeEverything(); // Close down the connection.
      //   }
      ///////////////////////

      return false; // No more lines to read.
      }



//////////////
    Lines come in like this:
    GET /StationIdentity.htm HTTP/1.1
    Host: www.radiationnetwork.com
    X-RadNetStation: " + WInfo.StationName
    User-agent: Rad Net Version " + ShowVersion.ToString( "N2" )
    Cache-Control: no-cache
    HTTP_CONNECTION: keep-alive
    // Encrypted lines:
    X-RadNet1: " + WInfo.RadNet1
    X-RadNet2: " + WInfo.RadNet2
    X-RadNet3: " + WInfo.RadNet3
    X-RadNet4: " + WInfo.RadNet4
    X-RadNetLast: Nada"
    ////////////



    if( Where == 0 )
      {
      // Marker string for end of header.
      InBufferS = InBufferS.substring( 2,
                            InBufferS.length() );
      return true;
      }

    // Copy on write.
    String Line = InBufferS;
    Line = Line.substring( 0, Where );
    InBufferS = InBufferS.substring( Where + 2,
                             InBufferS.length() );

    if( !Line.contains( "X-RadNet" ))
      {
      // Only interested in the X-RadNet lines.
      return true; // Maybe more lines to get.
      }

    String[] SplitS = Line.split( ":" );
    if( SplitS.length < 2 )
      {
      return true; // Maybe more lines to read,
                   // but this line is not useful.
      }

    String KeyWord = SplitS[0].trim().
                        toLowerCase( Locale.US );
    String Value = SplitS[1].trim();
    // This trim() has to be here because it's
    // usually sending an encrypted string and
    // the leading space has to be removed.

    if( KeyWord.length() < 2 )
      {
      ErrorString += "In AddNextStringFromInBuffer()" +
                      " KeyWord.Length < 2.\r\n";
      FreeEverything(); // Close the connection.
      return false; // No more lines to read.
      }

    if( KeyWord.length() > 30 )
      {
      ErrorString += "In AddNextStringFromInBuffer()." +
          " KeyWord is too long for protocol.\r\n";
      FreeEverything(); // Close the connection.
      return false; // No more lines to read.
      }

    // LinesMap.containsKey()
    LinesMap.put( KeyWord, Value );
    // StatusString += "Adding: " +
          // KeyWord + ": " + Value + "\r\n";

    return true;
    }




  protected boolean DataIsReady()
    {
    if( IsShutDown())
      return false;

    String SName = GetLineByName( "X-RadNetStation" );
    if( SName.length() < 2 )
      return false;

    SName = GetLineByName( "X-RadNetLast" );
    if( SName.length() < 2 )
      return false;

    return true;
    }



  protected String GetLineByName( String Name )
    {
    Name = Name.trim().toLowerCase( Locale.US );
    if( !LinesMap.containsKey( Name ))
      return "";

    return LinesMap.get( Name );
    }


  }
*/
