// Copyright Eric Chauvin 2022.


// This is licensed under the GNU General
// Public License (GPL).  It is the
// same license that Linux has.
// https://www.gnu.org/licenses/gpl-3.0.html



// USGS Data Project.

// This is being adapted from my News project
// and so it still looks a lot like the news
// project.

// "\\USGSDatabase\\UrlFiles\\"



import javax.swing.SwingUtilities;



class MainApp implements Runnable
  {
  public static final String versionDate =
                                 "11/14/2022";
  private MainWindow mainWin;
  // public ConfigureFile mainConfigFile;
  private String[] argsArray;
  private StrA programDirectory;



  public static void main( String[] args )
    {
    MainApp mApp = new MainApp( args );
    SwingUtilities.invokeLater( mApp );
    }



  @Override
  public void run()
    {
    setupProgram();
    }



  private MainApp()
    {
    }


  public MainApp( String[] args )
    {
    argsArray = args;
    }



  private void setupProgram()
    {
    // checkSingleInstance()

     // All programs need to have a batch file give
    // it the program directory so they're not stuck
    // in that directory.
    programDirectory = new StrA(
                       "\\Eric\\Main\\UsgsData" );

    int length = argsArray.length;
    if( length > 0 )
      programDirectory = new StrA( argsArray[0] );

/*
    String mainConfigFileName = programDirectory +
                              "MainConfigure.txt";

    mainConfigFile = new ConfigureFile( this,
                             mainConfigFileName );
    */

    mainWin = new MainWindow( this, "USGS Data" );
    mainWin.initialize();

    /*
    showStatus( " " );
    showStatus( "argsArray length: " + length );
    for( int count = 0; count < length; count++ )
      showStatus( argsArray[count] );
    */

    // showStatus( " " );
    }



  public void showStatusAsync( String toShow )
    {
    if( mainWin == null )
      return;

    mainWin.showStatusAsync( toShow );
    }



  public void clearStatus()
    {
    if( mainWin == null )
      return;

    mainWin.clearStatus();
    }




  }
