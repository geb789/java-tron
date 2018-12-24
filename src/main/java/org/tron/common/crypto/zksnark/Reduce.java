package org.tron.common.crypto.zksnark;


import java.math.BigInteger;
import org.tron.common.utils.ByteArray;

public class Reduce {

  private static final BigInteger INV = new BigInteger("-8659850874718887031");
  private static final BigInteger MODULUS = new BigInteger(
      "21888242871839275222246405745257275088696311157297823662689037894645226208583");
  //private static BigInteger ONE = new BigInteger("1", 10);


  private static final BigInteger current = new BigInteger(
      "10855362814118872746921150895317950015833966554628950873068002980441989956485");

  public static void mul_reduce(final BigInteger other) {
    System.out.println(current.toString());

    long inv = INV.longValue();
    //System.out.println("INV:"+INV.toString() + " inv:"+inv);

    BigInteger res = current.multiply(other);
    for (int i = 0; i < 4; i++) {
      long retI = bigIntegerToLong(res, i);
      long k = inv * retI;
      //System.out.println(k);

      {
        System.out.println("5k:" + k + "  res[i]:" + retI);
        System.out.println("6modulus:" + MODULUS.toString());
        System.out.println("7res+i:" + res.shiftRight(i * 64).toString());

//                String str="";
//                for(int j=0; j<256; j++) {
//                    str +="1";
//                }
//                BigInteger module256 = new BigInteger(str, 2);
        BigInteger module256 = BigInteger.ONE.shiftLeft(64 * 4);
        System.out.println(module256.toString());

        BigInteger temp = MODULUS.multiply(BigInteger.valueOf(k));

//                {
//                    BigInteger target = new BigInteger(
//                            "34737141404309989863563016744615464066455189490142675830579912161400335252603");
//                    BigInteger shouldBe = temp.subtract(target);
//
//                    System.out.println(target.bitLength());
//                }

        //BigInteger re = temp.mod(module256);
//                BigInteger re = temp.and(module256);
//                System.out.println(re.bitLength());
//                System.out.println(re.toString());

        BigInteger addMul = res.add(temp);

        // long carryout = bigIntegerToLong(addMul, 4);
        //BigInteger carryout = addMul.mod(module256);

        System.out.println(addMul.shiftRight(256).toString(2));

        System.out.println("8carryout:" + addMul.shiftRight(256).longValue());
        //System.out.println("9res+i:" + addMul.shiftRight( i*64 ).toString());
        //System.out.println("10res+n+i:" + res.shiftRight( i*64 ).toString());

      }
      break;

    }
  }

  public static long bigIntegerToLong(final BigInteger bigInteger, final int index) {
    return bigInteger.shiftRight(index * 64).longValue();
  }

  public static void showDifferent() {
    System.out.println(current.toString());
    System.out.println("java:");
    for (int i = 0; i < 4; i++) {
      long retI = bigIntegerToLong(current, i);
      System.out.println(retI);
      //System.out.println( BigInteger.valueOf(retI).toString(16));
    }

//        System.out.println("c++:");
//        BigInteger x1 = new BigInteger("-2958857645311278203");
//        BigInteger x2 = new BigInteger("-2003202992176724455");
//        BigInteger x3 = new BigInteger("-5131397046797937398");
//        BigInteger x4 = new BigInteger("-4198678090564094208");
//        System.out.println(x1.toString(16));
//        System.out.println(x2.toString(16));
//        System.out.println(x3.toString(16));
//        System.out.println(x4.toString(16));

  }

  public static BigInteger[] one2two(BigInteger b) {
    BigInteger[] result = new BigInteger[2];
    result[0] = b.shiftRight(64);
    result[1] = b.subtract(result[0].shiftLeft(64));
    return result;
  }

  public static BigInteger[] npm_mul(BigInteger[] a, BigInteger b) throws Exception {
    if (a.length != 4) {
      throw new Exception("length is error!");
    }

    BigInteger[] result = new BigInteger[5];
    BigInteger carry = BigInteger.ZERO;
    for (int i = a.length - 1; i >= 0; i--) {
      BigInteger temp = a[i].multiply(b).add(carry);

      BigInteger[] temp1 = one2two(temp);
      carry = temp1[0];
      result[i + 1] = temp1[1];
    }
    result[0] = carry;
    return result;
  }

  public static BigInteger[] npm_add(BigInteger[] a, BigInteger[] b) {
    BigInteger[] result = new BigInteger[5];
    BigInteger carry = BigInteger.ZERO;
    for (int i = a.length - 1; i > 0; i--) {
      BigInteger temp = a[i].add(b[i-1]).add(carry);
      BigInteger[] temp1 = one2two(temp);
      carry = temp1[0];
      result[i] = temp1[1];
    }
    result[0] = a[0].add(carry);
    return result;
  }

  public static BigInteger[] mpn_addmul_1(BigInteger[] res, BigInteger[] data, BigInteger k)
      throws Exception {
    BigInteger[] mul = npm_mul(data, k);
    BigInteger[] result = npm_add(mul, res);
    return result;
  }

  public static void main(String[] args) throws Exception {
//        BigInteger y = new BigInteger("-1");
//        int len = y.bitLength();
//        int a = y.abs().bitLength();
//        int x = INV.bitLength();
//        int z = INV.abs().bitLength();
    BigInteger[] data = new BigInteger[4];
    data[3] = new BigInteger(ByteArray.fromHexString("3C208C16D87CFD47"));
    data[2] = new BigInteger(ByteArray.fromHexString("97816A916871CA8D"));
    data[1] = new BigInteger(ByteArray.fromHexString("00B85045B68181585D"));
    data[0] = new BigInteger(ByteArray.fromHexString("30644E72E131A029"));
    BigInteger k = new BigInteger(ByteArray.fromHexString("00A11AB43E9190112D"));
    BigInteger[] res = new BigInteger[4];
    res[0] = new BigInteger(ByteArray.fromHexString("17FFEAE92E447A34"));
    res[1] = new BigInteger(ByteArray.fromHexString("00D38926FFB33D9DEF"));
    res[2] = new BigInteger(ByteArray.fromHexString("00A949AA5F127FB2E3"));
    res[3] = new BigInteger(ByteArray.fromHexString("00D6F006A5F645C385"));

    mpn_addmul_1(res, data, k);

    //    mul_reduce(BigInteger.ONE);

    //showDifferent();

//        BigInteger x = new BigInteger("45592504218428862610484167639933414082289156044771626703647915141842325209088");
//        BigInteger y = new BigInteger("10855362814118872746921150895317950015833966554628950873068002980441989956485");
//        BigInteger resutl = x.subtract(y);
//        System.out.println(resutl.toString());
//        System.out.println(resutl.bitLength());

//        BigInteger tar = new BigInteger("-5131397046797937398");
//        System.out.println(tar.toString(8));
//        for (int i=0; i<254; i++) {
//            long var = current.shiftRight(i).longValue();
//            if (var == tar.longValue()) {
//                System.out.print("===");
//            }
//            System.out.println(var);
//        }

//        System.out.println(current.toString(2));
//        System.out.println(current.shiftRight(1*64).toString(2));
//        System.out.println(current.shiftRight(2*64).toString(2));
//        System.out.println(current.shiftRight(3*64).toString(2));

//        BigInteger x1 = new BigInteger(
//                "1101011011110000000001101010010111110110010001011100001110000101", 2);
//        BigInteger x2 = new BigInteger(
//                "1010100101001001101010100101111100010010011111111011001011100011", 2);
//        BigInteger x3 = new BigInteger(
//                "1101001110001001001001101111111110110011001111011001110111101111", 2);
//        BigInteger x4 = new BigInteger(
//                "1011111111111111010101110100100101110010001000111101000110100", 2);
//
//        System.out.println(x1.longValue());
//        System.out.println(x2.longValue());
//        System.out.println(x3.longValue());
//        System.out.println(x4.longValue());

  }
}