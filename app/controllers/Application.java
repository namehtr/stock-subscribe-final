package controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.*;
import play.data.*;
import play.*;

import views.html.*;
import views.formdata.LoginFormData;
import play.mvc.Security;
import models.UserInfoDB;
import java.util.*;
import play.data.validation.Constraints.*;
import alphavantage.*;
import org.json.simple.parser.*;
import com.fasterxml.jackson.databind.*;



public class Application extends Controller {


  public static class Subscribe {
    @Required public String symbol;
    @Required @Min(1) @Max(2) public Integer action;
  }

  public static Result index() {
    return ok(Index.render("Home", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx())));
  }

  public static Result login() {
    Form<LoginFormData> formData = Form.form(LoginFormData.class);
    return ok(Login.render("Login", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), formData));
  }

  public static Result postLogin() {

    // Get the submitted form data from the request object, and run validation.
    Form<LoginFormData> formData = Form.form(LoginFormData.class).bindFromRequest();

    if (formData.hasErrors()) {

      flash("error", "Login credentials not valid.");
      return badRequest(Login.render("Login", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()), formData));
    }
    else {
      // email/password OK, so now we set the session variable and only go to authenticated pages.
      session().clear();
      session("email", formData.get().email);
      return redirect(routes.Application.profile());
    }
  }


  @Security.Authenticated(Secured.class)
  public static Result logout() {
    session().clear();
    return redirect(routes.Application.index());
  }

  @Security.Authenticated(Secured.class)
  public static Result profile() {
    ArrayList<String> userStockArray = new ArrayList<String>(UserInfoDB.getUserStockArray(ctx().session().get("email")));
    for(int i=0;i<userStockArray.size();i++){
      try  {
        String symbol = userStockArray.get(i);
        String price = Request.getHTML("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+symbol+"&interval=5min&apikey=B35PIEJLFU7T66ZA");
        JSONParser jsonParser = new JSONParser();

        Object obj = jsonParser.parse(price);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        JsonNode node = mapper.convertValue(obj, JsonNode.class).path("Time Series (5min)");
        Iterator<JsonNode> iterator = node.elements();
        String lastPrice = "NA";
        while(iterator.hasNext()){
          JsonNode finalNode = iterator.next();
          lastPrice = finalNode.path("4. close").textValue();
        }
        userStockArray.set(i,symbol+" Last Price :"+lastPrice);
      }catch(Exception e){

      }
    }
    return ok(Profile.render("Profile", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()),userStockArray));
  }

  @Security.Authenticated(Secured.class)
  public static Result subscribe() {
    return ok(SubscribePage.render("Subscribe", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()),Form.form(Subscribe.class)));
  }

  public static Result subscribeAction() {
    Form<Subscribe> form = Form.form(Subscribe.class).bindFromRequest();
    if(form.hasErrors()) {
      return badRequest(SubscribePage.render("Subscribe", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()),form));
    } else {
      Subscribe data = form.get();
      if(data.action == 1){
        UserInfoDB.addUserStock(ctx().session().get("email"),data.symbol);
      }
      else{
        UserInfoDB.removeUserStock(ctx().session().get("email"),data.symbol);
      }
      return profile();
    }
  }
  @Security.Authenticated(Secured.class)
  public static Result price() {
    ArrayList<String> userStockArray = new ArrayList<String>();
    return ok(Profile.render("Profile", Secured.isLoggedIn(ctx()), Secured.getUserInfo(ctx()),userStockArray));

  }
}
