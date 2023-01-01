package frog;

import frog.frog_Properties;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

public class frog_Properties {
  static final boolean GLOBAL_DEBUG = true;
  
  static final String ALGORITHM = "frog";
  
  static final double VERSION = 0.1D;
  
  static final String FULL_NAME = "Custom FROG algorithm";
  
  static final String NAME = "frog_Properties";
  
  static final Properties properties = new Properties();
  
  private static final String[][] DEFAULT_PROPERTIES = new String[][] { { "Trace.frog_Algorithm", "true" }, { "Debug.Level.*", "1" }, { "Debug.Level.frog_Algorithm", "9" } };
  
  public static String getProperty(String paramString) {
    return properties.getProperty(paramString);
  }
  
  public static String getProperty(String paramString1, String paramString2) {
    return properties.getProperty(paramString1, paramString2);
  }
  
  public static void list(PrintStream paramPrintStream) {
    list(new PrintWriter(paramPrintStream, true));
  }
  
  public static void list(PrintWriter paramPrintWriter) {
    paramPrintWriter.println("#");
    paramPrintWriter.println("# ----- Begin frog properties -----");
    paramPrintWriter.println("#");
    Enumeration enumeration = properties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = properties.getProperty(str1);
      paramPrintWriter.println(String.valueOf(str1) + " = " + str2);
    } 
    paramPrintWriter.println("#");
    paramPrintWriter.println("# ----- End frog properties -----");
  }
  
  public static Enumeration propertyNames() {
    return properties.propertyNames();
  }
  
  static boolean isTraceable(String paramString) {
    String str = "Trace." + paramString;
    str = properties.getProperty(str);
    return (str == null) ? false : (new Boolean(str)).booleanValue();
  }
  
  static int getLevel(String paramString) {
    String str = "Debug.Level." + paramString;
    str = properties.getProperty(str);
    if (str == null) {
      str = properties.getProperty("Debug.Level.*");
      if (str == null)
    	  System.out.println("1");
        return 0; 
    } 
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException numberFormatException) {
      return 0;
    } 
  }
  
  static PrintWriter getOutput() {
    PrintWriter printWriter;
    String str = properties.getProperty("Output");
    if (str != null && str.equals("out")) {
      printWriter = new PrintWriter(System.out, true);
    } else {
      printWriter = new PrintWriter(System.err, true);
    } 
    return printWriter;
  }
  
  static {
    InputStream inputStream = frog_Properties.class.getResourceAsStream("frog.properties");
    boolean bool = (inputStream == null) ? false : true;
    if (bool)
      try {
        properties.load(inputStream);
        inputStream.close();
      } catch (Exception exception) {
        bool = false;
      }  
    if (!bool) {
      int i = DEFAULT_PROPERTIES.length;
      for (byte b = 0; b < i; b++)
        properties.put(DEFAULT_PROPERTIES[b][0], DEFAULT_PROPERTIES[b][1]); 
    } 
  }
}
