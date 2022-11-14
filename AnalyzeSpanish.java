// Copyright Eric Chauvin 2020 - 2021.



public class AnalyzeSpanish implements Runnable
  {
  private MainApp mApp;
  private URLFileDictionary urlDictionary;



  private AnalyzeSpanish()
    {
    }


  public AnalyzeSpanish( MainApp appToUse,
                         URLFileDictionary
                         urlDictionaryToUse )
    {
    mApp = appToUse;

    // Just don't use the dictionary while it's
    // being used in here in this thread.
    urlDictionary = urlDictionaryToUse;
    }



  @Override
  public void run()
    {
    // UTF8StrA.doTest( mApp );
    // showCharacters();
    getWords();
    }



  private void getWords()
    {
    mApp.showStatusAsync( "Getting words..." );
    StrA fileS = urlDictionary.makeKeysValuesStrA();

    StrABld textBld = new StrABld( 10 );
    StrABld stringBld = new StrABld( 1000 );

    StrArray titleArray = new StrArray();
    StrArray linesArray = fileS.splitChar( '\n' );
    int howMany = 0;
    final int last = linesArray.length();
    for( int count = 0; count < last; count++ )
      {
      if( (count % 100) == 0 )
        {
        mApp.showStatusAsync( "URL Records: " +
                                         count );
        }

      StrA line = linesArray.getStrAt( count );

      URLFile uFile = new URLFile( mApp );
      uFile.setFromStrA( line );

      // StrA pulled = uFile.getAnchorsPulled();
      // if( pulled.startsWithChar( 't' ))
        // continue;

      StrA fileName = uFile.getFileName();
      // StrA title = uFile.getTitle();
      // mApp.showStatusAsync( "\nLinks not pulled: " + title );
      // mApp.showStatusAsync( "" + fileName );

      // mApp.showStatusAsync( "" + line );
      StrA filePath = new StrA( "\\ALang\\URLFiles\\" );
      filePath = filePath.concat( fileName );
      // mApp.showStatusAsync( "filePath: " + filePath );

      if( !FileUtility.exists( filePath ))
        {
        // This doesn't happen below:
        // setAnchorsPulledTrue();
        continue;
        }

      if( !URLParse.isSpanish( uFile.getUrl()))
        continue;

      howMany++;

      HtmlFileSpanish hFileSpanish = new HtmlFileSpanish(
                               mApp,
                               urlDictionary,
                               uFile.getUrl(),
                               filePath );

      // Remove Javascript and comments and all.
      if( !hFileSpanish.markUpFile())
        {
        return;
        }

      // StrA title = hFileSpanish.getTitle();
      // title = title.replace( new StrA( "&#x27;" ),
      //                       new StrA( "'" ));

      // Don't get this in the statistics:
      // title = title.replace( new StrA( "- La Prensa" ),
        //                      new StrA( " " ));

      // title = title.trim();

      // if( title.length() > 0 )
        // mApp.showStatusAsync( title.toString() );

      StrA wordsLine = hFileSpanish.getWordsLine();
      wordsLine = HtmlFile.fixAmpersandChars(
                                      wordsLine );

      wordsLine = wordsLine.toLowerCase();
      wordsLine = removeBadText( wordsLine );

      textBld.appendStrA( wordsLine );
      textBld.appendChar( ' ' );


      // titleArray.append( title );

      }

    mApp.showStatusAsync( "\ntextBld.length(): " + textBld.length() );

    CharDictionary charDct = new CharDictionary(
                                           mApp );

    StringDictionary stringDct = new
                         StringDictionary( mApp );

    String previousWord = "";
    String previousWord2 = "";
    String previousWord3 = "";
    String previousWord4 = "";
    String previousWord5 = "";

    int maxWordLength = 8;
    final int lastLetter = textBld.length();
    for( int count = 0; count < lastLetter; count++ )
      {
      char letter = textBld.charAt( count );

      // Dingbats (2700 27BF)
      if( (letter > ' ') && (letter < (char)0x2700) )
        charDct.setValue( letter );

      // Check sentence break before checking word
      // break.
      if( isSentenceBreak( letter ))
        {
        StrA wordA = stringBld.toStrA();
        String word = wordA.toString();
        stringBld.clear();

        // word = word.trim();
        // word = word.toLowerCase();

        if( word.length() > maxWordLength )
          {
          // It breaks up a sequence of words.
          previousWord = "";
          previousWord2 = "";
          previousWord3 = "";
          previousWord4 = "";
          previousWord5 = "";
          continue;
          }

        if( word.length() < 1 )
          {
          // It breaks up a sequence of words.
          previousWord = "";
          previousWord2 = "";
          previousWord3 = "";
          previousWord4 = "";
          previousWord5 = "";
          continue;
          }


        String setWord = previousWord5 + " " +
                         previousWord4 + " " +
                         previousWord3 + " " +
                         previousWord2 + " " +
                         previousWord +  " " +
                         word;
        // String setWord = word;

        setWord = setWord.trim();

        if( setWord.contains( " " ))
          stringDct.setValue( setWord );

        // A sentence-like thing breaks up a
        // series of words.
        previousWord5 = "";
        previousWord4 = "";
        previousWord3 = "";
        previousWord2 = "";
        previousWord = "";
        continue;
        }

      if( isWordBreak( letter ))
        {
        StrA wordA = stringBld.toStrA();
        String word = wordA.toString();
        stringBld.clear();

        // word = word.trim();
        // word = word.toLowerCase();

        if( word.length() > maxWordLength )
          {
          // It breaks up a sequence of words.
          previousWord = "";
          previousWord2 = "";
          previousWord3 = "";
          previousWord4 = "";
          previousWord5 = "";
          continue;
          }

        if( word.length() < 1 )
          {
          // It breaks up a sequence of words.
          previousWord = "";
          previousWord2 = "";
          previousWord3 = "";
          previousWord4 = "";
          previousWord5 = "";

          continue;
          }


        String setWord = previousWord5 + " " +
                         previousWord4 + " " +
                         previousWord3 + " " +
                         previousWord2 + " " +
                         previousWord +  " " +
                         word;
        // String setWord = word;

        setWord = setWord.trim();

        if( setWord.contains( " " ))
          stringDct.setValue( setWord );

        previousWord5 = previousWord4;
        previousWord4 = previousWord3;
        previousWord3 = previousWord2;
        previousWord2 = previousWord;
        previousWord = word;
        continue;
        }


      stringBld.appendChar( letter );

      // C0 Controls and Basic Latin (Basic Latin)
      //                                 (0000 007F)
      // C1 Controls and Latin-1 Supplement (0080 00FF)
      // Latin Extended-A (0100 017F)
      // Latin Extended-B (0180 024F)

      // IPA Extensions (0250 02AF)
      // Spacing Modifier Letters (02B0 02FF)
      // Combining Diacritical Marks (0300 036F)
      // General Punctuation (2000 206F)
      // Superscripts and Subscripts (2070 209F)
      // Currency Symbols (20A0 20CF)
      // Combining Diacritical Marks for Symbols
      //                                (20D0 20FF)
      // Letterlike Symbols (2100 214F)
      // Number Forms (2150 218F)
      // Arrows (2190 21FF)
      // Mathematical Operators (2200 22FF)
      // Box Drawing (2500 257F)
      // Geometric Shapes (25A0 25FF)
      // Miscellaneous Symbols (2600 26FF)
      // Dingbats (2700 27BF)
      // Miscellaneous Symbols and Arrows (2B00 2BFF)
      }

    // mApp.showStatusAsync( "\nHow many: " + howMany );


    // stringDct.sort();
    // stringDct.sortByCount();
    // StrA allWords = stringDct.makeKeysValuesStrA();
    // mApp.showStatusAsync( "\nAllWords:" );
    // mApp.showStatusAsync( "\n" + allWords );


    mApp.showStatusAsync( "Making StringArrayByCount." );

    StringArrayByCount saByCount = stringDct.
                        makeStringArrayByCount();

    if( saByCount == null )
      {
      mApp.showStatusAsync( "saByCount was null." );
      return;
      }

    mApp.showStatusAsync( "Sorting saByCount." );
    saByCount.sort();
    mApp.showStatusAsync( "Finished sorting." );

    saByCount.showKeysValuesStrings();



    charDct.sort();
    // charDct.sortByCount();
    StrA allChars = charDct.makeKeysValuesStrA();
    mApp.showStatusAsync( "\nAllChars:" );
    mApp.showStatusAsync( "\n" + allChars );



    mApp.showStatusAsync( "\nDone processing." );
    }


  private StrA removeBadText( StrA result )
    {
    // "la prensaacerca de las cookies en este sitio.utilizamos cookies para personalizar y mejorar su experiencia en nuestro sitio y optimizar la publicidad que le ofrecemos.";
       // press about cookies on this site.
       // we use cookies to personalize
       // and improve your experience on
       // our site and optimize the advertising
       // we serve you.

    String badS = "de las cookies";

    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    // on this site
    badS = "en este sitio";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "hacer clic en";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));



    badS = "utilizamos cookies para personalizar";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "experiencia en nuestro sitio";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "para personalizar y mejorar";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "publicidad que le ofrecemos";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "sitio y optimizar la";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "visite nuestrapolítica de cookies";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "nuestrapolítica de cookies para";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "aceptar cookiesleer";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));

    badS = "cookies para saber más";
    result = result.replace(
                            new StrA( badS ),
                            new StrA( " " ));


    return result;
    }



  private boolean isSentenceBreak( char in )
    {
    // If it is a sentence-like break, meaning
    // it breaks up a sequence of words.
    // More like a common phrase than a sentence.
    if( in == '.' )
      return true;

    if( in == ',' ) // For a phrase.
      return true;

    if( in == '!' )
      return true;

    if( in == (char)8505 ) // Upside down exclamation.
      return true;

    if( in == (char)161 ) // Upside down exclamation.
      return true;

    if( in == ':' )
      return true;

    if( in == ';' )
      return true;

    if( in == '?' )
      return true;

    if( in == (char)191 ) // Upside question mark.
      return true;

    return false;
    }



  private boolean isWordBreak( char in )
    {
    // if( in <= ' ' )
      // return true;

    // if( (in >= '0') && (in <= '9') )
      // return true;

    if( in < 'A' )
      return true;

    if( (in > 'Z') && (in < 'a') )
      return true;

    if( (in > 'z') && (in < 193) )
      return true;

    // (char)8220 and 8221 are those leaning
    // quote marks.
    if( in > (char)333 )
      return true;

    // if( Markers.isMarker( in ))
      // return true;

    return false;
    }



  }
