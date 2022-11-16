// Copyright Eric Chauvin 2022.



// This is licensed under the GNU General
// Public License (GPL).  It is the
// same license that Linux has.
// https://www.gnu.org/licenses/gpl-3.0.html



public class URLParse
  {
  private MainApp mApp;
  private StrABld rawTagBld;
  private StrArray badLinkArray;
  private StrA linkText = StrA.Empty;
  private StrA link = StrA.Empty;
  private StrA baseDomain = StrA.Empty;
  private StrA baseURL = StrA.Empty;


  private static final StrA HrefStart = new
                                  StrA( "href=" );



  private URLParse()
    {
    }


  public URLParse( MainApp appToUse,
                   StrA useBaseURL )
    {
    mApp = appToUse;
    baseURL = useBaseURL;

    baseDomain = getDomainFromLink( baseURL );
    StrA baseHttpS = new StrA( "https://" );
    baseDomain = baseHttpS.concat( baseDomain );

    rawTagBld = new StrABld( 1024 * 4 );
    setupBadLinkArray();
    }


  public StrA getLink()
    {
    return link;
    }



  public StrA getLinkText()
    {
    return linkText;
    }



  public void addRawText( StrA in )
    {
    rawTagBld.appendStrA( in );
    }



  public void clear()
    {
    rawTagBld.clear();
    linkText = StrA.Empty;
    link = StrA.Empty;
    }



  public boolean processLink()
    {
    StrA text = rawTagBld.toStrA();
    if( text.length() == 0 )
      {
      // mApp.showStatusAsync( "Raw text length is zero." );
      return false;
      }

    if( !text.containsStrA( HrefStart ))
      return false;

    if( text.containsStrA( new StrA( "href=\"\"" )))
      return false;

    if( text.containsStrA( new StrA( "onclick" )))
      return false;

    // mApp.showStatusAsync( "\n\nRaw: " + text );

    StrArray lineParts = text.splitChar( '>' );
    final int lastPart = lineParts.length();
    if( lastPart == 0 )
      {
      mApp.showStatusAsync(
           "The anchor tag doesn't have any parts." );
      mApp.showStatusAsync( "Text: " + text );
      return false;
      }

    if( lastPart > 2 )
      {
      mApp.showStatusAsync(
                     "Anchor tag lastPart > 2." );
      mApp.showStatusAsync( "text: " + text );
      return false;
      }

    linkText = StrA.Empty;
    if( lastPart >= 2 )
      linkText = lineParts.getStrAt( 1 );

    linkText = linkText.cleanUnicodeField().trim();
    linkText = HtmlFile.fixAmpersandChars(
                                       linkText );

    mApp.showStatusAsync( "\nlinkText: " +
                                linkText );

    StrA insideTag = lineParts.getStrAt( 0 );

    // mApp.showStatusAsync( "insideTag: " +
    //                              insideTag );

    StrArray tagAttr = insideTag.splitChar( ' ' );
    final int lastAttr = tagAttr.length();
    if( lastAttr == 0 )
      {
      mApp.showStatusAsync(
                   "URLParse: lastAttr is zero." );
      return false;
      }

    link = StrA.Empty;
    for( int count = 0; count < lastAttr; count++ )
      {
      StrA attr = tagAttr.getStrAt( count );
      if( attr.containsStrA( HrefStart ))
        {
        link = attr;
        break;
        }
      }

    link = link.replace( HrefStart, StrA.Empty );
    link = link.replaceChar( '"', ' ' );
    link = link.cleanUnicodeField().trim();
    link = fixupLink( link );
    if( link.length() == 0 )
      return false;

    if( isBadLink( link ))
      {
      // mApp.showStatusAsync( "Bad Link: " + link );
      return false;
      }

    // Don't add new Spanish links.
    // if( isSpanish( link ))
      // return false;

    mApp.showStatusAsync( "Link: " + link );

    return true;
    }




  private StrA getDomainFromLink( StrA link )
    {
    if( link.length() == 0 )
      return StrA.Empty;

    StrA dotCom = new StrA( ".com" );
    StrA dotMex = new StrA( ".mx" );
    StrA dotOrg = new StrA( ".org" );
    StrA dotGov = new StrA( ".gov" );

    StrArray linkParts = link.splitChar( '/' );
    final int last = linkParts.length();
    for( int count = 0; count < last; count++ )
      {
      StrA part = linkParts.getStrAt( count );
      if( (part.containsStrA( dotCom )) ||
          (part.containsStrA( dotMex )) ||
          (part.containsStrA( dotOrg )) ||
          (part.containsStrA( dotGov )) )
        {
        return part;
        }
      }

    return StrA.Empty;
    }



  private StrA fixupLink( StrA in )
    {
    if( in.length() < 2 )
      return StrA.Empty;

    // if( base.endsWithChar( '/' ))
      // base = base.substring( 0,
       //         base.length() - 2 );

    StrA result = in;

    StrArray paramParts = result.splitChar( '?' );
    final int lastParam = paramParts.length();
    if( lastParam == 0 )
      {
      mApp.showStatusAsync(
                 "URLParse: lastParam is zero." );
      return StrA.Empty;
      }

    result = paramParts.getStrAt( 0 );

    StrA twoSlashes = new StrA( "//" );
    StrA httpS = new StrA( "https:" );

    if( result.startsWith( twoSlashes ))
      result = httpS.concat( result );

    if( result.startsWithChar( '/' ))
      result = baseDomain.concat( result );

    return result;
    }



  private void setupBadLinkArray()
    {
    badLinkArray = new StrArray();


    badLinkArray.append( new StrA(
             ".instagram.com" ));

    badLinkArray.append( new StrA(
             ".youtube.com" ));

    badLinkArray.append( new StrA(
             "twitter.com" ));

    // badLinkArray.append( new StrA( "" ));
    }



  private boolean hasValidDomain( StrA link )
    {
    if( link.containsStrA( new StrA(
                              ".usgs.gov" )))
      return true;

    if( link.containsStrA( new StrA(
                              ".sciencebase.gov" )))
      return true;

    // Department of the Interior.
    if( link.containsStrA( new StrA(
                              ".doi.gov" )))
      return true;


    // if( isSpanish( link ))
      // return true;

    return false;
    }




  private boolean isBadLink( StrA link )
    {
    // wa.me is WhatsApp.
    // Messaging app owned by Facebook.

    if( link.containsStrA( new StrA(
                             "https://wa.me/" )))
      return true;

    if( link.containsStrA( new StrA( "mailto:" )))
      return true;

    // if( link.containsStrA( new StrA( "ftp://" )))
      // return true;

    if( link.containsStrA( new StrA( "sms:" )))
      return true;

    if( link.endsWith( new StrA( ".pdf" )))
      return true;

    final int last = badLinkArray.length();
    for( int count = 0; count < last; count++ )
      {
      StrA text = badLinkArray.getStrAt( count );
      if( link.containsStrA( text ))
        return true;

      }

    // Do this last.
    if( !hasValidDomain( link ))
      {
      mApp.showStatusAsync(
                 "Not valid domain: " + link );
      return true;
      }

    return false;
    }



  }
