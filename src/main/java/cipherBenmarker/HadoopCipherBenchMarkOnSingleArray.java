package cipherBenmarker;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.crypto.*;

import java.io.*;
import java.util.*;

/**
 * Created by root on 12/5/14.
 */
public class HadoopCipherBenchMarkOnSingleArray extends CipherBenchMark {
  private String keyProviderName = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";


  public HadoopCipherBenchMarkOnSingleArray(String keyProviderName, CipherBenchmarkOption option) {
    this.keyProviderName = keyProviderName;
    this.option = option;
    bufferSize = option.operationSize;
    benchMarkName =
      HadoopCipherBenchMarkOnSingleArray.class.getName() + " using " + keyProviderName;
  }

  @Override
  public void getBenchMarkData() {
    System.out.println("Encryption starts.");
    testEncryption(option.iterations);
    System.out.println("Encryption ends.");

    System.out.println("Decryption starts.");
    testDecryption(option.iterations);
    System.out.println("Decryption ends.");
  }

  public void testEncryption(int iterations) {
    Configuration conf = new Configuration();
    conf.set("hadoop.security.crypto.codec.classes.aes.ctr.nopadding", getProvider());
    CryptoCodec codec = CryptoCodec.getInstance(conf, CipherSuite.AES_CTR_NOPADDING);
    Random r = new Random();
    byte[] iv = new byte[16];
    byte[] key = new byte[16];
    r.nextBytes(iv);
    r.nextBytes(key);

    try {
      byte[] inputData = prepareData(option.dataSize);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(option.dataSize);
      CryptoOutputStream cryptoOutputStream =
        new CryptoOutputStream(outputStream, codec, bufferSize, key, iv);

      System.out.println("warming up");
      // warm up
      for (int i = 0; i < option.warmupIterations; i++) {
        int offset = 0;
        int remaining = option.dataSize;
        while (remaining > 0) {
          int len = (option.operationSize < remaining) ? option.operationSize : remaining;
          cryptoOutputStream.write(inputData, offset, len);
          offset += len;
          remaining -= len;
        }
        outputStream.reset();
      }

      System.out.println("warming up complete.");

      long begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      for (int i = 0; i < iterations; i++) {
        int offset = 0;
        int remaining = option.dataSize;
        while (remaining > 0) {
          int len = (option.operationSize < remaining) ? option.operationSize : remaining;
          cryptoOutputStream.write(inputData, offset, len);
          offset += len;
          remaining -= len;
        }
        outputStream.reset();
      }

      long end = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      printResult("===encryption",
        1000.0 * option.dataSize * iterations / ((end - begin) * 1024.0 * 1024.0));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testDecryption(int iterations) {
    Configuration conf = new Configuration();
    conf.set("hadoop.security.crypto.codec.classes.aes.ctr.nopadding", getProvider());
    CryptoCodec codec = CryptoCodec.getInstance(conf, CipherSuite.AES_CTR_NOPADDING);
    Random r = new Random();
    byte[] iv = new byte[16];
    byte[] key = new byte[16];
    r.nextBytes(iv);
    r.nextBytes(key);

    try {
      byte[] inputData = prepareData(option.dataSize);

      ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData);
      CryptoInputStream cryptoInputStream =
        new CryptoInputStream(inputStream, codec, bufferSize, key, iv);

      byte[] outputData = new byte[option.dataSize];

      System.out.println("Warming up.");
      // warm up
      for (int i = 0; i < option.warmupIterations; i++) {
        int remaining = option.dataSize;
        int offset = 0;
        while (remaining > 0) {
          int len = (remaining < option.operationSize) ? remaining : option.operationSize;
          int v = cryptoInputStream.read(outputData, offset, len);
          offset += v;
          remaining -= v;
        }
        inputStream.reset();
      }

      System.out.println("warming up complete.");
      long begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

      for (int i = 0; i < iterations; i++) {
        int remaining = option.dataSize;
        int offset = 0;
        while (remaining > 0) {
          int len = (remaining < option.operationSize) ? remaining : option.operationSize;
          int v = cryptoInputStream.read(outputData, offset, len);
          offset += v;
          remaining -= v;
        }
        inputStream.reset();
      }

      long end = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      printResult("=== decryption",
        1000.0 * option.dataSize * iterations / ((end - begin) * 1024.0 * 1024.0));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getProvider() {
    return keyProviderName;
  }
}
