package com.github.source.hot.bio;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BioClient {
  public static void main(String[] args) throws Exception {
    Socket socket = new Socket("127.0.0.1", 9090);


    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    byte[] content = "hello".getBytes("UTF-8");
    String tmp = ("00000000" + content.length);
    String length = tmp.substring(tmp.length() - 8);
    baos.write(length.getBytes());
    baos.write(content);

    writeStream(socket.getOutputStream(), baos.toByteArray());

    Object result = readStream(socket.getInputStream());
  }

  private static Object readStream(InputStream input) throws Exception {
    int headLength = 8;
    byte[] headBuffer = new byte[headLength];
    for (int offset = 0; offset < headLength; ) {
      int length = input.read(headBuffer, offset, headLength - offset);
      if (length < 0) {
        throw new RuntimeException("invalid_packet_head");
      }
      offset += length;
    }
    int totalLength = Integer.parseInt(new String(headBuffer, "UTF-8"));
    byte[] resultBuffer = new byte[totalLength];
    int offset = 0;
    while (offset < totalLength) {
      int realLength = input.read(resultBuffer, offset, totalLength - offset);
      if (realLength >= 0) {
        offset += realLength;
      } else {
        System.err.println(
            "the length of packet should be :" + totalLength + " but encounter eof at offset:"
                + offset);
        throw new RuntimeException("invalid_packet_data");
      }
    }
    String recvStr = new String(resultBuffer, "UTF-8");
    System.out.println(recvStr);
    return recvStr.getBytes();
  }

  private static void writeStream(OutputStream outputStream, byte[] toByteArray) throws Exception {
    outputStream.write(toByteArray);
    outputStream.flush();
  }
}
