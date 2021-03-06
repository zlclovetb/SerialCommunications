package com.example.serialcommunications.tools;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class ToolUtility {

  /**
   * isEmpty
   * 
   * Test to see whether input string is empty.
   * 
   * @param str
   * @return True if it is empty; false if it is not.
   */
  public static boolean isEmpty(String str) {
    return (str == null || str.length() == 0 || str.trim().equals(""));
  }

  /**
   * isEmpty
   * 
   * Test to see whether input string buffer is empty.
   * 
   * @param stringBuffer
   * @return True if it is empty; false if it is not.
   */
  public static boolean isEmpty(StringBuffer stringBuffer) {
    return (stringBuffer == null || stringBuffer.length() == 0 || stringBuffer.toString().trim().equals(""));
  }

  public static boolean isEmpty(Object[] array) {
    return (array == null || array.length == 0);
  }

  public static boolean isEmpty(Object val) {
    return (val == null);
  }

  public static boolean isEmpty(java.util.List<? extends Object> list) {
    return (list == null || list.size() == 0);
  }

  public static Date getCurrentDate() {
    Calendar tmp = Calendar.getInstance();
    return (new Date(tmp.getTime().getTime()));
  }

  public static Date getStartTime(Date timestamp) {
    if (isEmpty(timestamp)) {
      timestamp = getCurrentDate();
    }
    Calendar todayStart = Calendar.getInstance();
    todayStart.setTime(timestamp);
    todayStart.set(Calendar.HOUR_OF_DAY, 0);
    todayStart.set(Calendar.MINUTE, 0);
    todayStart.set(Calendar.SECOND, 0);
    todayStart.set(Calendar.MILLISECOND, 0);
    return new Timestamp(todayStart.getTime().getTime());
  }

  public static Date getEndTime(Date timestamp) {
    if (isEmpty(timestamp)) {
      timestamp = getCurrentDate();
    }
    Calendar todayEnd = Calendar.getInstance();
    todayEnd.setTime(timestamp);
    todayEnd.set(Calendar.HOUR_OF_DAY, 23);
    todayEnd.set(Calendar.MINUTE, 59);
    todayEnd.set(Calendar.SECOND, 59);
    todayEnd.set(Calendar.MILLISECOND, 999);
    return new Date(todayEnd.getTime().getTime());
  }

  public static int calculate(String sigore) {
    int l = sigore.charAt(4);
    int h = sigore.charAt(7);
    return (h - l + 1) * 20;
  }

  /**
   * ???????????????????????????????????????
   * 
   * @param String str ????????????ASCII?????????
   * @return String ??????Byte????????????????????????: [61 6C 6B]
   */
  public static String str2HexStr(String str) {

    char[] chars = "0123456789ABCDEF".toCharArray();
    StringBuilder sb = new StringBuilder("");
    byte[] bs = str.getBytes();
    int bit;

    for (int i = 0; i < bs.length; i++) {
      bit = (bs[i] & 0x0f0) >> 4;
      sb.append(chars[bit]);
      bit = bs[i] & 0x0f;
      sb.append(chars[bit]);
      sb.append(' ');
    }
    return sb.toString().trim();
  }

  /**
   * ???????????????????????????
   * 
   * @param String str Byte?????????(Byte?????????????????? ???:[616C6B])
   * @return String ??????????????????
   */
  public static String hexStr2Str(String hexStr) {
    String str = "0123456789ABCDEF";
    char[] hexs = hexStr.toCharArray();
    byte[] bytes = new byte[hexStr.length() / 2];
    int n;

    for (int i = 0; i < bytes.length; i++) {
      n = str.indexOf(hexs[2 * i]) * 16;
      n += str.indexOf(hexs[2 * i + 1]);
      bytes[i] = (byte) (n & 0xff);
    }
    return new String(bytes);
  }

  /**
   * bytes??????????????????????????????
   * 
   * @param byte[] b byte??????
   * @return String ??????Byte?????????????????????
   */
  public static String byte2HexStr(byte[] b) {
    String stmp = "";
    StringBuilder sb = new StringBuilder("");
    for (int n = 0; n < b.length; n++) {
      stmp = Integer.toHexString(b[n] & 0xFF);
      sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
      sb.append(" ");
    }
    return sb.toString().toUpperCase().trim();
  }

  /**
   * bytes??????????????????Byte???
   * 
   * @param String src Byte??????????????????Byte?????????????????????
   * @return byte[]
   */
  public static byte[] hexStr2Bytes(String src) {
    int m = 0, n = 0;
    int l = src.length() / 2;
    System.out.println(l);
    byte[] ret = new byte[l];
    for (int i = 0; i < l; i++) {
      m = i * 2 + 1;
      n = m + 1;
      ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
    }
    return ret;
  }

  /**
   * String?????????????????????unicode???String
   * 
   * @param String strText ???????????????
   * @return String ??????unicode??????????????????
   * @throws Exception
   */
  public static String strToUnicode(String strText) throws Exception {
    char c;
    StringBuilder str = new StringBuilder();
    int intAsc;
    String strHex;
    for (int i = 0; i < strText.length(); i++) {
      c = strText.charAt(i);
      intAsc = (int) c;
      strHex = Integer.toHexString(intAsc);
      if (intAsc > 128)
        str.append("\\u" + strHex);
      else // ??????????????????00
        str.append("\\u00" + strHex);
    }
    return str.toString();
  }

  /**
   * unicode???String?????????String????????????
   * 
   * @param String hex 16?????????????????? ?????????unicode???2byte???
   * @return String ???????????????
   */
  public static String unicodeToString(String hex) {
    int t = hex.length() / 6;
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < t; i++) {
      String s = hex.substring(i * 6, (i + 1) * 6);
      // ??????????????????00??????
      String s1 = s.substring(2, 4) + "00";
      // ???????????????
      String s2 = s.substring(4);
      // ???16?????????string??????int
      int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
      // ???int???????????????
      char[] chars = Character.toChars(n);
      str.append(new String(chars));
    }
    return str.toString();
  }
}
