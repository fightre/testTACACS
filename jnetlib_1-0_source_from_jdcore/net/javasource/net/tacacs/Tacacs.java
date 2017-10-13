package net.javasource.net.tacacs;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

public class Tacacs
{
  private static final byte AUTHEN_PASS = 1;
  private static final byte AUTHEN_FAIL = 2;
  private static final byte AUTHEN_GETDATA = 3;
  private static final byte AUTHEN_GETUSER = 4;
  private static final byte AUTHEN_GETPASS = 5;
  private static final byte AUTHEN_RESTART = 6;
  private static final byte AUTHEN_ERROR = 7;
  private static final byte AUTHEN_FOLLOW = 33;
  private static final byte ZEROBYTE = 0;
  static final byte HEADERFLAG_UNENCRYPT = 1;
  private static final byte HEADERFLAG_SINGLECON = 4;
  public static final byte VERSION_13_0 = -64;
  public static final byte VERSION_13_1 = -63;
  public static final int PORT_STANDARD = 49;
  private byte headerFlags;
  private byte[] sessionID;
  private Integer tacacsSequence;
  private Byte version;
  private Integer port;
  private String hostname;
  private byte[] secretkey;
  private Socket theSocket = null;
  




  public Tacacs()
  {
    headerFlags = 0;
    version = new Byte((byte)-64);
    port = new Integer(49);
    hostname = "";
    secretkey = "".getBytes();
  }
  



  public void setHostname(String Hostname)
  {
    hostname = Hostname;
  }
  


  public void setVersion(byte Version)
  {
    version = new Byte(Version);
  }
  


  public void setKey(String SecretKey)
  {
    secretkey = SecretKey.getBytes();
  }
  






  public void setPortNumber(int PortNumber) { port = new Integer(PortNumber); }
  
  private void Connect() throws IOException {
    tacacsSequence = new Integer(1);
    sessionID = Header.generateSessionID();
    if (theSocket == null)
      theSocket = new Socket(hostname, port.intValue());
  }
  
  private void CloseConnection() throws IOException {
    if (theSocket != null) {
      theSocket.close();
      theSocket = null;
    }
    sessionID = null;
  }
  







  public synchronized boolean isAuthenticated(String Username, String Password)
    throws IOException, NoSuchAlgorithmException
  {
    if (Username.equals("")) {
      return false;
    }
    Connect();
    AuthSTART AS = new AuthSTART(null);
    AS.send(Username, Password);
    AuthREPLY AR = new AuthREPLY(null);
    AR.get();
    boolean exitLoop = false;
    while ((AR.getStatus() != 1) && (AR.getStatus() != 2) && (AR.getStatus() != 7) && (AR.getStatus() != 33) && (exitLoop != true)) {
      synchronized (tacacsSequence) {
        int tmpSeqNum = tacacsSequence.intValue();
        tmpSeqNum++;tmpSeqNum++;
        tacacsSequence = new Integer(tmpSeqNum);
      }
      if (((REPLY_status == 3 ? 1 : 0) | (REPLY_status == 4 ? 1 : 0)) != 0) {
        AuthCONT AC = new AuthCONT(null);
        AC.send(Username);
        AR.get();
      }
      else if (REPLY_status == 5) {
        AuthCONT AC = new AuthCONT(null);
        AC.send(Password);
        AR.get();
      }
      else if (REPLY_status == 6) {
        synchronized (tacacsSequence) {
          tacacsSequence = new Integer(1);
        }
        AS.send(Username, Password);
        AR.get();
      }
      else if (tacacsSequence.intValue() > 5) {
        exitLoop = true;
      }
      else {
        exitLoop = true;
      }
    }
    CloseConnection();
    if (REPLY_status == 1) {
      return true;
    }
    
    return false;
  }
  
  private class AuthSTART { AuthSTART(Tacacs.1 x1) { this(); }
    
    private byte ACTION_LOGIN = 1;
    private byte ACTION_CHPASS = 2;
    private byte ACTION_SENDAUTH = 4;
    private byte AUTHTYPE_ASCII = 1;
    private byte AUTHTYPE_PAP = 2;
    private byte AUTHTYPE_CHAP = 3;
    private byte AUTHTYPE_ARAP = 4;
    private byte AUTHTYPE_MSCHAP = 5;
    private byte PRIVLVL_MAX = 15;
    private byte PRIVLVL_MIN = 0;
    private byte SERVICE_NONE = 0;
    private byte SERVICE_LOGIN = 1;
    private byte SERVICE_ENABLE = 2;
    private byte SERVICE_PPP = 3;
    
    private byte action = ACTION_LOGIN;
    private byte authtype = AUTHTYPE_ASCII;
    private byte privlvl = PRIVLVL_MIN;
    private byte service = SERVICE_NONE;
    
    private void send(String User, String Pass)
      throws IOException, NoSuchAlgorithmException
    {
      byte[] Username = User.getBytes();
      byte[] Data = Pass.getBytes();
      byte[] Port = "JAVA".getBytes();
      byte[] RemoteAdd = "Somewhere".getBytes();
      byte User_Len = (byte)Username.length;
      byte Port_Len = (byte)Port.length;
      byte Data_Len = (byte)Data.length;
      byte RemoteAdd_Len = (byte)RemoteAdd.length;
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      baos.write(action);
      baos.write(privlvl);
      baos.write(authtype);
      baos.write(service);
      baos.write(User_Len);
      baos.write(Port_Len);
      baos.write(RemoteAdd_Len);
      baos.write(Data_Len);
      baos.write(Username);
      baos.write(Port);
      baos.write(RemoteAdd);
      baos.write(Data);
      byte[] body = Header.crypt(version.byteValue(), tacacsSequence.byteValue(), baos.toByteArray(), headerFlags, sessionID, secretkey);
      
      baos.reset();
      byte[] header = Header.makeHeader(body, version, Header.TYPE_AUTHENTIC, tacacsSequence, headerFlags, sessionID);
      
      baos.write(header);
      baos.write(body);
      baos.writeTo(theSocket.getOutputStream()); }
    
    private AuthSTART() {} }
  private class AuthCONT { AuthCONT(Tacacs.1 x1) { this(); }
    private byte FLAG_ABORT = 1;
    
    private void send(String UserMsgData)
      throws IOException, NoSuchAlgorithmException
    {
      byte[] UserMsg = UserMsgData.getBytes();
      byte[] CONT_data = "NONE".getBytes();
      byte CONT_Flags = 0;
      byte[] UserMsg_Len = Bytes.ShorttoBytes((short)UserMsg.length);
      byte[] CONT_data_Len = Bytes.ShorttoBytes((short)CONT_data.length);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      baos.write(UserMsg_Len);
      baos.write(CONT_data_Len);
      baos.write(CONT_Flags);
      baos.write(UserMsg);
      baos.write(CONT_data);
      byte[] body = Header.crypt(version.byteValue(), tacacsSequence.byteValue(), baos.toByteArray(), headerFlags, sessionID, secretkey);
      
      byte[] header = Header.makeHeader(body, version, Header.TYPE_AUTHENTIC, tacacsSequence, headerFlags, sessionID);
      
      baos.reset();
      baos.write(header);
      baos.write(body);
      baos.writeTo(theSocket.getOutputStream()); }
    
    private AuthCONT() {} }
  private class AuthREPLY { AuthREPLY(Tacacs.1 x1) { this(); }
    private byte FLAG_NOECHO = 1;
    private byte REPLY_status;
    private byte REPLY_flags;
    private byte[] servermsgLen = new byte[2];
    private byte[] dataLen = new byte[2];
    
    private void get()
      throws IOException, SocketException, NoSuchAlgorithmException
    {
      DataInputStream dis = null;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] body = null;
      byte[] header = null;
      dis = new DataInputStream(theSocket.getInputStream());
      for (int i = 0; i < 12; i++) {
        baos.write(dis.readByte());
      }
      header = baos.toByteArray();
      baos.reset();
      int Body_Len = Header.extractBodyLen(header);
      for (int i = 0; i < Body_Len; i++) {
        baos.write(dis.readByte());
      }
      byte[] tempBody = baos.toByteArray();
      byte headerVersionNumber = Header.extractVersionNumber(header);
      byte headerFlags = Header.extractFlags(header);
      byte headerSequenceNumber = Header.extractSeqNum(header);
      body = Header.crypt(headerVersionNumber, headerSequenceNumber, tempBody, headerFlags, sessionID, secretkey);
      
      REPLY_status = body[0];
      REPLY_flags = body[1];
    }
    
    private byte getStatus() { return REPLY_status; }
    
    private byte getFlags() {
      return REPLY_flags;
    }
    
    private AuthREPLY() {}
  }
}
