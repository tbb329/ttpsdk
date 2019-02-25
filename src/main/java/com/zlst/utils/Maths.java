/*
 * 创建日期 2004-7-15
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.zlst.utils;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;


/**
 * @author Administrator Maths
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * @version 1.0
 */
public class Maths {

    private static final Logger logger = LoggerFactory.getLogger(Maths.class);


    /******** add end **************/
    /**
     *
     */
    public Maths() {
        super();

    }

    /**
     * byte数据对比
     *
     * @param srcbyte
     * @param destbyte
     * @return
     */
    public static boolean byteequal(byte[] srcbyte, byte[] destbyte) {
        boolean ret = true;
        if (srcbyte == null || destbyte == null)
            return false;
        if (srcbyte.length != destbyte.length)
            return false;
        for (int i = 0; i < srcbyte.length; i++) {
            if (srcbyte[i] != destbyte[i])
                return false;
        }
        return ret;

    }

    /**
     * 把2个字节数据转换成Int形 注：高低换位
     *
     * @param data
     * @return
     */
    public static int twobytestoInt(byte[] data) {
        if (data == null || data.length != 2)
            return -1;
        String hexstr = byteToHex(data[1]) + byteToHex(data[0]);
        return Integer.parseInt(hexstr, 16);
    }

    /**
     * 把4个字节数据转换成Int形 注：高低换位
     *
     * @param data
     * @return
     */
    public static long fourbytestoInt(byte[] data) {
        if (data == null || data.length != 4)
            return -1;
        String hexstr = byteToHex(data[3]) + byteToHex(data[2])
                + byteToHex(data[1]) + byteToHex(data[0]);

        return Long.parseLong(hexstr, 16);
    }

    /**
     * 把int转换成2个byte 注：高低换位
     *
     * @param data
     * @return
     */
    public static byte[] inttotwobytes(int data) {
        return shortToBytes((short)data);
    }

    /**
     * 把int转换成2个byte 注：高低换位
     *
     * @param data
     * @return
     */
    public static byte[] inttofourbytes(long data) {

        byte[] temp = longToBytes(data);
        byte[] ret = new byte[4];
        ret[0] = temp[7];
        ret[1] = temp[6];
        ret[2] = temp[5];
        ret[3] = temp[4];
        return ret;
    }

    /******** add end **************/
    /**
     * 十六进制的值变成字节
     *
     * @param str
     * @param spacestr
     * @return
     */
    public static byte[] hexStringToBytes(String str, String spacestr) {
        str = str.replaceAll(spacestr, "");
        return hexStringToBytes(str);

    }

    /**
     * 十六进制的值变成字节
     *
     * @param str
     * @return
     */
    public static byte[] hexStringToBytes(String str) {
        int ilen = str.length() / 2;
        String tmp;
        byte[] b = new byte[ilen];
        for (int i = 0; i < ilen; i++) {
            tmp = str.substring(i * 2, i * 2 + 2);
            b[i] = (byte) Integer.parseInt(tmp, 16);
        }
        return b;
    }

    /**
     * 把获得字节的十六进制数据
     *
     * @param b
     * @return
     */
    public static String stringToHexString(String str) {
        StringBuilder ret = new StringBuilder();
        byte[] b = str.getBytes();
        try {
            for (int i = 0; i < b.length; i++) {
                ret.append(" " + byteToHex(b[i]));
            }
        } catch (Exception e) {
            logger.error("stringToHexString error", e);
        }
        return ret.toString().trim();
    }

    /**
     * 把获得字节的十六进制数据
     *
     * @param b
     * @return
     */
    public static String bytesToHexString(byte[] b) {
        StringBuilder ret = new StringBuilder();
        try {
            for (int i = 0; i < b.length; i++) {
                ret.append(" ").append(byteToHex(b[i]));
            }
        } catch (Exception e) {
            logger.error("bytesToHexString error", e);
        }
        return ret.toString().trim();
    }

    private static final String[] HEX = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    /**
     * 得到一个十六进制数据
     *
     * @param b
     * @return
     */
    public static String byteToHex(byte b) {
        String ret;
        int n1, n2;
        n1 = b >> 4 & 0x0f;
        n2 = b & 0x0f;
        ret = HEX[n1] + HEX[n2];
        return ret;
    }

    /**
     * 把一个Int数据转化成一个4个字节的字节数组
     *
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value) {
        byte[] ret = new byte[4];
        ret[3] = (byte)(value & 0xFF);
        ret[2] = (byte)((value >> 8) & 0xFF);
        ret[1] = (byte)((value >> 16) & 0xFF);
        ret[0] = (byte)((value >> 24) & 0xFF);
        return ret;

    }

    /**
     * 把一个short数据转化成一个2个字节的字节数组 注：高低换位
     */
    public static byte[] shortToBytes(short value) {
        byte[] tmp = intToBytes(value);
        byte[] ret = new byte[2];
        ret[0] = tmp[3];
        ret[1] = tmp[2];
        return ret;
    }

    /**
     * 把一个short数据转化成一个2个字节的字节数组 注：高低换位
     */
    public static byte[] shortToNotChangeBytes(short value) {
        byte[] tmp = intToBytes(value);
        byte[] ret = new byte[2];
        ret[0] = tmp[2];
        ret[1] = tmp[3];
        return ret;
    }

    /**
     * 把两个字节按高低换位  注：高低换位
     */
    public static byte[] twobytechange(byte a, byte b) {
        byte[] ret = new byte[2];
        ret[0] = b;
        ret[1] = a;
        return ret;
    }

    /**
     * 把一个long型数据转化成一个4个字节的字节数组
     *
     * @param value
     * @return
     */
    public static byte[] longToBytes(long value) {
        byte[] ret = new byte[8];
        byte tmp;
        tmp = (byte) value;
        ret[7] = tmp;
        value = value >> 8;
        tmp = (byte) value;
        ret[6] = tmp;
        value = value >> 8;
        tmp = (byte) value;
        ret[5] = tmp;
        value = value >> 8;
        tmp = (byte) value;
        ret[4] = tmp;
        value = value >> 8;
        tmp = (byte) value;
        ret[3] = tmp;
        value = value >> 8;
        tmp = (byte) value;
        ret[2] = tmp;
        value = value >> 8;
        tmp = (byte) value;
        ret[1] = tmp;
        value = value >> 8;
        tmp = (byte) value;
        ret[0] = tmp;
        return ret;

    }

    /**
     * bytesTOInt
     *
     * @param headbyte
     * @return
     */
    public static int bytesToInt(byte[] headbyte) {
        int i = 0;
        int tmp;
        tmp = headbyte[3];
        if (tmp < 0) {
            tmp = 256 + tmp;
        }
        i = i + tmp;
        tmp = headbyte[2];
        if (tmp < 0) {
            tmp = 256 + tmp;
        }
        i = i + tmp * 256;
        tmp = headbyte[1];
        if (tmp < 0) {
            tmp = 256 + tmp;
        }
        i = i + tmp * 256 * 256;
        tmp = headbyte[0];
        if (tmp < 0) {
            tmp = 256 + tmp;
        }
        i = i + tmp * 256 * 256 * 256;
        return i;
    }


    /**
     * float转byte[]
     */
    public static byte[] floatToByte(float v) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        byte[] ret = new byte[4];
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(v);
        bb.get(ret);
        return ret;
    }

    /**
     * byte[]转float
     */
    public static float byteToFloat(byte[] v) {
        return Float.intBitsToFloat(bytesToInt(v));
    }

    /**
     * byte[]转float
     */
    public static float fourbyteToFloat(byte[] v) {
        if (v == null || v.length != 4)
            return 0;
        if (v[0] == 0xFF && v[1] == 0xFF && v[2] == 0xFF && v[3] == 0xFF)
            return Float.NaN;
        float f = Float.intBitsToFloat(bytesToInt(new byte[]{v[3], v[2],
                v[1], v[0]}));
        if (f == Float.NaN) {
            f = 0;
        }
        return f;
    }

    public static byte[] doubleToBytes(double db) {
        long l = Double.doubleToRawLongBits(db);
        byte[] b = new byte[8];

        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (l & 0xff);
            l = l >>> 8;
        }
        return b;
    }

    public static double byteToDouble(byte[] b) {
        if (b == null || b.length < 8)
            throw new NumberFormatException();
        long lval = 0;
        for (int i = 0; i < 8; i++) {
            lval = lval << 8;
            lval += (b[(7 - i)]);
        }
        return Double.longBitsToDouble(lval);
    }

    public static int byteToInt(byte b) {
        return Integer.parseInt(byteToHex(b), 16);
    }

    public static byte bcdToUint8(byte bcd) {
        return (byte) ((bcd & 0x0F) + ((bcd >> 4 & 0x0F) * 10));
    }

    /**
     * 低位在前,1字节有符号数
     *
     * @param b
     * @return
     */
    public static int bytesToShortInt(byte[] b) {
        return (b[0]);
    }

    /**
     * 低位在前，2字节有符号数（低位在前）
     *
     * @param b
     * @return
     */
    public static int bytesToSmallInt(byte[] b) {
        return (b[1] << 8) + (b[0] & 0xFF);

    }

    /**
     * 高位在前2字节有符号数
     *
     * @param b
     * @return
     */
    public static int bytesToNormalSmallInt(byte[] b) {

        return (b[0] << 8) + (b[1] & 0xFF);

    }

    /**
     * 低位在前4字节有符号数
     *
     * @param b
     * @return
     */
    public static int bytesToLongInt(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 3; i >= 0; i--) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    /**
     * 高位在前4字节有符号数
     *
     * @param b
     * @return
     */
    public static int bytesToNormalLongInt(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < 4; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    /**
     * byte2bits
     *
     * @param b
     * @return
     */
    public static String byte2bits(byte b) {
        int z = b;
        z |= 256;
        String str = "00000000" + Integer.toBinaryString(z);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    /**
     * 获取第一个data 在 src中的位置,从idx位置开始搜索
     *
     * @param src
     * @param data
     * @param idx
     * @return data的位置信息, 不包含则返回-1.
     */
    public static int indexOf(byte[] src, byte data, int idx) {
        int pos = -1;
        if (src == null || src.length < 1 || idx < 0) {
            return pos;
        }
        for (int i = idx; i < src.length; i++) {
            if (src[i] == data) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    /**
     * 在src中从idx处反向搜索到第一个data的位置,
     *
     * @param src
     * @param data
     * @param idx
     * @return data的位置, 不包含在返回-1.
     */
    public static int reverseIndexOf(byte[] src, byte data, int idx) {
        int pos = -1;
        if (src == null || src.length < 1 || idx < 0 || idx > src.length - 1) {
            return pos;
        }
        for (int i = idx; i >= 0; i--) {
            if (src[i] == data) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public static String byteBufToStr(ByteBuf buf) {
        int i = buf.readableBytes();
        byte[] msg = new byte[i];
        buf.readBytes(msg);
        buf.resetReaderIndex();
        return bytesToHexString(msg);
    }

    public static void main(String[] args) {
        byte[] bytes = intToBytes(1232);
        System.out.println(Maths.bytesToHexString(bytes));
    }

}
