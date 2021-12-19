package com.github.source.hot.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {
  private static final Logger logger = LoggerFactory.getLogger(BioServer.class);

  public static void main(String[] args) throws IOException {
    ExecutorService executorService = Executors.newCachedThreadPool();
    ServerSocket serverSocket = new ServerSocket(9090);
    System.out.println("启动");
    logger.info("socket 服务启动");
    while (true) {
      final Socket accept = serverSocket.accept();
      logger.info("客户端已连接");

      executorService.execute(new Runnable() {
        @Override public void run() {
          handlerSocket(accept);
        }
      });
    }
  }

  public static void handlerSocket(Socket socket) {
    byte[] bytes = new byte[1024];
    try {
      InputStream inputStream = socket.getInputStream();
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      String s = result.toString("UTF-8");
      logger.info("客户端输入 = {}", s);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}
