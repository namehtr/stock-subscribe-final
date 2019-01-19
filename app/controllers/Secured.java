package controllers;

import models.UserInfo;
import models.UserInfoDB;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;

public class Secured extends Security.Authenticator {


  @Override
  public String getUsername(Context ctx) {
    return ctx.session().get("email");
  }


  @Override
  public Result onUnauthorized(Context context) {
    return redirect(routes.Application.login()); 
  }
  

  public static String getUser(Context ctx) {
    return ctx.session().get("email");
  }
  

  public static boolean isLoggedIn(Context ctx) {
    return (getUser(ctx) != null);
  }
  

  public static UserInfo getUserInfo(Context ctx) {
    return (isLoggedIn(ctx) ? UserInfoDB.getUser(getUser(ctx)) : null);
  }
}