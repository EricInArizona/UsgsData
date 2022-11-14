// Copyright Eric Chauvin 2021.


/*

import javax.net.ssl.SSLSocket;


public class TLSSocket extends SSLSocket
  {

  public boolean getEnableSessionCreation()
    {
    return true;
    }


  public void setEnableSessionCreation(
                                   boolean flag )
    {

    }


abstract void addHandshakeCompletedListener(
            HandshakeCompletedListener listener )

// String getApplicationProtocol()
Returns the most recent application protocol value negotiated for this connection.

abstract String[] getEnabledCipherSuites()
Returns the names of the SSL cipher suites which are currently enabled for use on this connection.
abstract String[] getEnabledProtocols()
Returns the names of the protocol versions which are currently enabled for use on this connection.
abstract boolean getEnableSessionCreation()
Returns true if new SSL sessions may be established by this socket.
String getHandshakeApplicationProtocol()
Returns the application protocol value negotiated on a SSL/TLS handshake currently in progress.
BiFunction<SSLSocket,List<String>,String> getHandshakeApplicationProtocolSelector()
Retrieves the callback function that selects an application protocol value during a SSL/TLS/DTLS handshake.
SSLSession getHandshakeSession()
Returns the SSLSession being constructed during a SSL/TLS handshake.
abstract boolean getNeedClientAuth()
Returns true if the socket will require client authentication.
abstract SSLSession getSession()
Returns the SSL Session in use by this connection.
SSLParameters getSSLParameters()
Returns the SSLParameters in effect for this SSLSocket.
abstract String[] getSupportedCipherSuites()
Returns the names of the cipher suites which could be enabled for use on this connection.
abstract String[] getSupportedProtocols()
Returns the names of the protocols which could be enabled for use on an SSL connection.
abstract boolean getUseClientMode()
Returns true if the socket is set to use client mode when handshaking.
abstract boolean getWantClientAuth()
Returns true if the socket will request client authentication.
abstract void removeHandshakeCompletedListener(HandshakeCompletedListener listener)
Removes a previously registered handshake completion listener.
abstract void setEnabledCipherSuites(String[] suites)
Sets the cipher suites enabled for use on this connection.
abstract void setEnabledProtocols(String[] protocols)
Sets the protocol versions enabled for use on this connection.
abstract void setEnableSessionCreation(boolean flag)
Controls whether new SSL sessions may be established by this socket.
void setHandshakeApplicationProtocolSelector(BiFunction<SSLSocket,List<String>,String> selector)
Registers a callback function that selects an application protocol value for a SSL/TLS/DTLS handshake.
abstract void setNeedClientAuth(boolean need)
Configures the socket to require client authentication.
void setSSLParameters(SSLParameters params)
Applies SSLParameters to this socket.
abstract void setUseClientMode(boolean mode)
Configures the socket to use client (or server) mode when handshaking.
abstract void setWantClientAuth(boolean want)
Configures the socket to request client authentication.
abstract void startHandshake()
Starts an SSL handshake on this connection.


// From java.net.Socket
// bind, close, connect, connect, getChannel,
// getInetAddress, getInputStream, getKeepAlive,
// getLocalAddress, getLocalPort,
// getLocalSocketAddress, getOOBInline,
// getOutputStream, getPort, getReceiveBufferSize,
// getRemoteSocketAddress, getReuseAddress,
// getSendBufferSize, getSoLinger, getSoTimeout,
// getTcpNoDelay, getTrafficClass, isBound,
// isClosed, isConnected, isInputShutdown,
// isOutputShutdown, sendUrgentData,
// setKeepAlive, setOOBInline,
// setPerformancePreferences,
// setReceiveBufferSize, setReuseAddress,
// setSendBufferSize, setSocketImplFactory,
// setSoLinger, setSoTimeout, setTcpNoDelay,
// setTrafficClass, shutdownInput,
// shutdownOutput, toString


  }
*/
