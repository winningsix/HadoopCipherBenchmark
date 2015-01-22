//package cipherBenmarker;
//
//import javax.crypto.*;
//import javax.crypto.spec.*;
//import java.security.*;
//import java.util.*;
//
///**
// * Created by root on 12/5/14.
// */
//public class JavaCipherBenchMark extends CipherBenchMark {
//  public JavaCipherBenchMark(int dataSize) {
//    this.stride = dataSize;
//    benchMarkName = "JavaCipherBenchMark";
//  }
//
//  @Override
//  public void getBenchMarkData(int times) {
//    byte[] data = prepareData(stride);
//    KeyGenerator kgen = null;
//    try {
//      kgen = KeyGenerator.getInstance("AES");
//    } catch (NoSuchAlgorithmException e) {
//      e.printStackTrace();
//    }
//    kgen.init(128);
//    SecretKey aesKey = kgen.generateKey();
//
//    Random r = new Random();
//    byte[] iv = new byte[16];
//    byte[] key = new byte[16];
//    r.nextBytes(iv);
//    r.nextBytes(key);
//
//    // get the jceks store
//    try {
//      String provider = getProvider();
//      if (provider == null || provider.isEmpty()) {
//        return;
//      }
//      Cipher c = Cipher.getInstance("AES/CTR/NoPadding", provider);
//      c.init(Cipher.ENCRYPT_MODE, aesKey);
//
//      long beginTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
//      for (int i = 0; i < times; i++) {
//        c.doFinal(data);
//      }
//      long endTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
//      printResult("ENCRYPT",
//        1000.0 * data.length * times / ((endTime - beginTime) * 1024.0 * 1024.0));
//
//
//      IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
//      c.init(Cipher.DECRYPT_MODE, aesKey, ivParameterSpec);
//
//
//      beginTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
//      for (int i = 0; i < times; i++) {
//        c.doFinal(data);
//      }
//      endTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
//      printResult("DECRYPT",
//        1000.0 * data.length * times / ((endTime - beginTime) * 1024.0 * 1024.0));
//    } catch (NoSuchAlgorithmException e) {
//      e.printStackTrace();
//    } catch (NoSuchPaddingException e) {
//      e.printStackTrace();
//    } catch (InvalidKeyException e) {
//      e.printStackTrace();
//    } catch (BadPaddingException e) {
//      e.printStackTrace();
//    } catch (IllegalBlockSizeException e) {
//      e.printStackTrace();
//    } catch (NoSuchProviderException e) {
//      e.printStackTrace();
//    } catch (InvalidAlgorithmParameterException e) {
//      e.printStackTrace();
//    }
//  }
//
//  @Override
//  public String getProvider() {
//    String schema = "jceks";
//    KeyStore store;
//    try {
//      store = KeyStore.getInstance(schema);
//      return store.getProvider().getName();
//    } catch (KeyStoreException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//}
