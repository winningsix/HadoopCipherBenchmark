package cipherBenmarker;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.crypto.*;

import java.io.*;
import java.util.*;

/**
 * Created by root on 12/5/14.
 */
public class HadoopCipherBenchMarkOnMultiArray extends CipherBenchMark {
  private String keyProviderName = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";

  public HadoopCipherBenchMarkOnMultiArray(String keyProviderName, CipherBenchmarkOption option) {
    this.keyProviderName = keyProviderName;
    this.option = option;
    bufferSize = option.operationSize;
    benchMarkName = HadoopCipherBenchMarkOnMultiArray.class.getName() + " using " + keyProviderName;
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
      int internalIterations = option.dataSize / option.operationSize;
      byte[][] inputData = prepareTwoDimensionsData(internalIterations, option.operationSize);

      ByteArrayOutputStream[] outputStreams = new ByteArrayOutputStream[internalIterations];
      CryptoOutputStream[] cryptoOutputStreams = new CryptoOutputStream[internalIterations];
      for (int i = 0; i < internalIterations; i++) {
        outputStreams[i] = new ByteArrayOutputStream(option.operationSize);
        cryptoOutputStreams[i] =
          new CryptoOutputStream(outputStreams[i], codec, bufferSize, key, iv);
      }

      System.out.println("encryption warming up");
      // warm up
      for (int i = 0; i < option.warmupIterations; i++) {
        int j = 0;
        while (j < internalIterations) {
          cryptoOutputStreams[j].write(inputData[j]);
          outputStreams[j].reset();
          j++;
        }
      }

      System.out.println("encryption start");

      long begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      for (int i = 0; i < iterations; i++) {
        int j = 0;
        while (j < internalIterations) {
          cryptoOutputStreams[j].write(inputData[j]);
          outputStreams[j].reset();
          j++;
        }
      }

      long end = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
      printResult("encryption",
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
      int internalIterations = option.dataSize / option.operationSize;
      byte[][] inputData = prepareTwoDimensionsData(internalIterations, option.operationSize);

      ByteArrayInputStream[] inputStreams = new ByteArrayInputStream[internalIterations];
      CryptoInputStream[] cryptoInputStreams = new CryptoInputStream[internalIterations];

      for (int i = 0; i < internalIterations; i++) {
        inputStreams[i] = new ByteArrayInputStream(inputData[i]);
        cryptoInputStreams[i] =
          new CryptoInputStream(inputStreams[i], codec, bufferSize, key, iv);
      }

      byte[][] outputData = new byte[internalIterations][option.operationSize];

      System.out.println("decryption warming up");
      // warm up
      for (int i = 0; i < option.warmupIterations; i++) {
        int j = 0;
        while (j < internalIterations) {
          int v = cryptoInputStreams[j].read(outputData[j]);
          if (v != option.operationSize) {
            System.out
              .println(
                "===ERROR=== actual read value is " + v + " while expected is " +
                  option.operationSize);
            System.exit(1);
          }
          inputStreams[j].reset();
          j++;
        }
      }

      System.out.println("decryption start");
      long begin = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

      for (int i = 0; i < iterations; i++) {
        int j = 0;
        while (j < internalIterations) {
          int v = cryptoInputStreams[j].read(outputData[j]);
          if (v != option.operationSize) {
            System.out
              .println(
                "===ERROR=== actual read value is " + v + " while expected is " +
                  option.operationSize);
            System.exit(1);
          }
          inputStreams[j].reset();
          j++;
        }
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
