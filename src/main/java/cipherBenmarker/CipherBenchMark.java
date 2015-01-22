package cipherBenmarker;

import java.util.*;

/**
 * Created by root on 12/5/14.
 */
public abstract class CipherBenchMark {
  final static int DEFAULT_BUFFERSIZE = 512 * 1024;

  protected String benchMarkName;
  protected CipherBenchmarkOption option;
  protected int bufferSize = DEFAULT_BUFFERSIZE;

  protected String getBenchMarkName() {
    return benchMarkName;
  }

  protected byte[] prepareData(int size) {
    byte[] data = new byte[size];
    Random r = new Random();
    r.nextBytes(data);
    return data;
  }

  protected byte[][] prepareTwoDimensionsData(int strideNum, int stride) {
    byte[] dataEntry = new byte[stride];
    byte[][] result = new byte[strideNum][stride];
    Random r = new Random();
    r.nextBytes(dataEntry);
    for (int i = 0; i < strideNum; i++) {
      result[i] = dataEntry.clone();
    }
    return result;
  }

  protected void printResult(String operation, double timeCost) {
    System.out.println(
      "result of " + getBenchMarkName() + " for the " + operation + " operation is " + timeCost +
        " M/s");
    System.out.println();
  }

  public abstract void getBenchMarkData();

  public abstract String getProvider();
}
