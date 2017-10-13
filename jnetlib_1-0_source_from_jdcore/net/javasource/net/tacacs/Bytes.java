package net.javasource.net.tacacs;



public class Bytes
{
  private Bytes() {}
  

  public static byte[] InttoBytes(int v)
  {
    byte[] bytes = new byte[4];
    bytes[0] = ((byte)((v & 0xFF000000) >>> 24));
    bytes[1] = ((byte)((v & 0xFF0000) >>> 16));
    bytes[2] = ((byte)((v & 0xFF00) >>> 8));
    bytes[3] = ((byte)(v & 0xFF));
    return bytes;
  }
  
  public static byte InttoByte(int v) {
    return (byte)(v & 0xFF);
  }
  
  public static byte[] ShorttoBytes(short v) {
    byte[] bytes = new byte[2];
    bytes[0] = ((byte)((v & 0xFF00) >>> 8));
    bytes[1] = ((byte)(v & 0xFF));
    return bytes;
  }
  
  public static int IntBytetoInt(byte[] bytes) {
    if (bytes.length < 4)
      return 0;
    int v = bytes[0] << 24 & 0xFF000000 | bytes[1] << 16 & 0xFF0000 | bytes[2] << 8 & 0xFF00 | bytes[3] & 0xFF;
    


    return v;
  }
  
  public static int IntBytetoInt(byte byte1, byte byte2, byte byte3, byte byte4) {
    int v = byte1 << 24 & 0xFF000000 | byte2 << 16 & 0xFF0000 | byte3 << 8 & 0xFF00 | byte4 & 0xFF;
    


    return v;
  }
  
  public static int ShortBytetoInt(byte[] bytes) {
    if (bytes.length < 2)
      return 0;
    int v = bytes[0] << 8 & 0xFF00 | bytes[1] & 0xFF;
    
    return v;
  }
  
  public static int ShortBytetoInt(byte byte1, byte byte2) {
    int v = byte1 << 8 & 0xFF00 | byte2 & 0xFF;
    
    return v;
  }
  
  public static int BytetoInt(byte byte1) { int v = byte1 & 0xFF;
    return v;
  }
}
