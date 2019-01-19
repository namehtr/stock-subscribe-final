package models;

import java.util.HashMap;
import java.util.Map;
import java.util.*;

public class UserInfoDB {
  
  private static Map<String, UserInfo> userinfos = new HashMap<String, UserInfo>();
  private static Map<String, ArrayList<String> > userstocks = new HashMap<String, ArrayList<String> >();

  /**
   * Adds the specified user to the UserInfoDB.
   * @param name Their name.
   * @param email Their email.
   * @param password Their password. 
   */
  public static void addUserInfo(String name, String email, String password) {
    userinfos.put(email, new UserInfo(name, email, password));
  }
  public static void addUserStock(String email, String symbol) {
    ArrayList<String> userStockArray = new ArrayList<String>();
    if(userstocks.containsKey(email)) {
      userStockArray = userstocks.get(email);
    }
    userStockArray.add(symbol);
    userstocks.put(email, userStockArray);
  }

  public static void removeUserStock(String email, String symbol) {
    if(userstocks.containsKey(email)) {
      ArrayList<String> userStockArray = userstocks.get(email);
      userStockArray.remove(symbol);
      userstocks.put(email,userStockArray);
    }
  }
  /**
   * Returns true if the email represents a known user.
   * @param email The email.
   * @return True if known user.
   */
  public static boolean isUser(String email) {
    return userinfos.containsKey(email);
  }

  /**
   * Returns the UserInfo associated with the email, or null if not found.
   * @param email The email.
   * @return The UserInfo.
   */
  public static UserInfo getUser(String email) {
    return userinfos.get((email == null) ? "" : email);
  }

  /**
   * Returns true if email and password are valid credentials.
   * @param email The email. 
   * @param password The password. 
   * @return True if email is a valid user email and password is valid for that email.
   */
  public static boolean isValid(String email, String password) {
    return ((email != null) 
            &&
            (password != null) 
            &&
            isUser(email) 
            &&
            getUser(email).getPassword().equals(password));
  }
  public static  ArrayList<String> getUserStockArray(String email){
    ArrayList<String> userStockArray = new ArrayList<String>();
    if(userstocks.containsKey(email)){
      userStockArray = userstocks.get(email);
    }
    return userStockArray;
  }
}
