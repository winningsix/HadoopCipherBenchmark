package cipherBenmarker;

/**
 * Created by root on 1/22/15.
 */
public class CipherBenchmarkOption {
  public int dataSize;
  public int operationSize;
  public int warmupIterations;
  public int iterations;

  public CipherBenchmarkOption(int dataSize, int operationSize,
                               int warmupIterations, int iterations) {
    this.dataSize = dataSize;
    this.operationSize = operationSize;
    this.warmupIterations = warmupIterations;
    this.iterations = iterations;
  }

  public static CipherBenchmarkOptionBuilder newBuilder(){
    return new CipherBenchmarkOptionBuilder();
  }

  public static class CipherBenchmarkOptionBuilder {
    private int dataSize;
    private int operationSize;
    private int warmupIterations;
    private int iterations;

    public CipherBenchmarkOptionBuilder buildDataSize(int dataSize) {
      this.dataSize = dataSize;
      return this;
    }

    public CipherBenchmarkOptionBuilder buildOperationSize(int operationSize) {
      this.operationSize = operationSize;
      return this;
    }

    public CipherBenchmarkOptionBuilder buildWarmupIterations(int warmupIterations) {
      this.warmupIterations = warmupIterations;
      return this;
    }

    public CipherBenchmarkOptionBuilder buildIterations(int iterations) {
      this.iterations = iterations;
      return this;
    }

    public CipherBenchmarkOption create() {
      return new CipherBenchmarkOption(dataSize, operationSize, warmupIterations, iterations);
    }
  }
}
