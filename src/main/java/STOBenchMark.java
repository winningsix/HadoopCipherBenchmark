import cipherBenmarker.*;

import java.io.*;
import java.util.*;

/**
 * Created by root on 12/4/14.
 */
public class STOBenchMark {
  static String defaultConfigPath = "./config.properties";
  static String jceAesCtrCryptoCodec = "org.apache.hadoop.crypto.JceAesCtrCryptoCodec";
  static String openSSLAesCtrCryptoCodec = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec";
  static String defaultExecutionIterations = "123456";
  static String defaultOperationSize = "524288";
  static String defaultWarmupIterations = "1000";
  static String defaultDataSize = "1073134720";

  public static void main(String[] args) throws IOException {
    if (args != null && args.length != 0 && args.length != 4) {
      System.out.println(
        "Usage: java -Djava.library.path=\"$PATH\" -cp STOBenchMark-[version].jar STOBenchMark " +
          " [warmupIterations] [iterations] [dataSize] [operationSize]");
      System.out.println("args[0]: " + args[0]);
      System.exit(1);
    }
    System.out.println("args length : " + args.length);

    Properties prop = new Properties();
    String propFileName = "config.properties";

    File configFile = new File(defaultConfigPath);
    InputStream inputStream;
    if (configFile.exists()) {
      inputStream = new FileInputStream(configFile);
    } else {
      System.out.println("can not find the configuration file under the current path");
      inputStream = STOBenchMark.class.getClassLoader().getResourceAsStream(propFileName);
    }

    if (inputStream != null) {
      prop.load(inputStream);
    } else {
      throw new FileNotFoundException("No configuration file found");
    }

    boolean javaCipherTestEnabled =
      Boolean.valueOf(prop.getProperty("java.cipher.enabled", "false"));
    boolean hadoopJCECipherTestEnabled =
      Boolean.valueOf(prop.getProperty("hadoop.jce.cipher.enabled", "false"));
    boolean hadoopOpenSSLCipherTestEnabled =
      Boolean.valueOf(prop.getProperty("hadoop.openssl.cipher.enabled", "false"));
    boolean hadoopJCECipherOnOneBigArrayTestEnabled =
      Boolean.valueOf(prop.getProperty("hadoop.jce.cipher.onOneBigArray.enabled", "false"));
    boolean hadoopOpenSSLCipherOnOneBigArrayTestEnabled =
      Boolean.valueOf(prop.getProperty("hadoop.openssl.cipher.onOneBigArray.enabled", "false"));

    int iterations, warmupIterations, dataSize, operationSize;

    if (args.length > 0) {
      System.out.println("Use Java args.");
      warmupIterations = Integer.valueOf(args[0]);
      iterations = Integer.valueOf(args[1]);
      dataSize = Integer.valueOf(args[2]);
      operationSize = Integer.valueOf(args[3]);
    } else {
      iterations =
        Integer.valueOf(prop.getProperty("execution.iterations", defaultExecutionIterations));
      dataSize = Integer.valueOf(prop.getProperty("data.size", defaultDataSize));
      operationSize = Integer.valueOf(prop.getProperty("operation.size", defaultOperationSize));
      warmupIterations =
        Integer.valueOf(prop.getProperty("warmup.iterations", defaultWarmupIterations));
    }

    CipherBenchmarkOption option = CipherBenchmarkOption.newBuilder().buildWarmupIterations(
      warmupIterations).buildIterations(iterations).buildDataSize(dataSize).buildOperationSize(
      operationSize).create();

    List<CipherBenchMark> benchMarks = new ArrayList<CipherBenchMark>();

//    if (javaCipherTestEnabled) {
//      benchMarks.add(new JavaCipherBenchMark(operationSize));
//    } else {
//      System.out.println("java cipher test disabled");
//    }
//
    if (hadoopJCECipherTestEnabled) {
      benchMarks.add(
        new HadoopCipherBenchMarkOnMultiArray(jceAesCtrCryptoCodec, option));
    } else {
      System.out.println("hadoop JCE cipher test disabled");
    }

    if (hadoopOpenSSLCipherTestEnabled) {
      benchMarks
        .add(
          new HadoopCipherBenchMarkOnMultiArray(openSSLAesCtrCryptoCodec, option));
    } else {
      System.out.println("hadoop Openssl cipher test disabled");
    }

    System.out
      .println("dataSize: " + dataSize + " and in GB: " + (double) dataSize / (1024 * 1024 * 1024));
    System.out.println("iterations: " + iterations);
    System.out.println("operationSize: " + operationSize);
    System.out.println("warmupIterations: " + warmupIterations);

    if (hadoopJCECipherOnOneBigArrayTestEnabled) {
      benchMarks
        .add(
          new HadoopCipherBenchMarkOnSingleArray(jceAesCtrCryptoCodec, option));
    } else {
      System.out.println("Hadoop JCE cipher in one array test disabled");
    }

    if (hadoopOpenSSLCipherOnOneBigArrayTestEnabled) {
      benchMarks
        .add(
          new HadoopCipherBenchMarkOnSingleArray(openSSLAesCtrCryptoCodec, option));
    } else {
      System.out.println("Hadoop Openssl cipher in one array test disabled");
    }

    System.out.println("begin test suite");
    for (CipherBenchMark benchMark : benchMarks) {
      benchMark.getBenchMarkData();
    }
    System.out.println("end test suite");
  }
}
