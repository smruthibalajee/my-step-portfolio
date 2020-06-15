// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    //Creates new userService object to check if user is logged in.
    UserService userService = UserServiceFactory.getUserService();

    //Default settings for user object
    String userEmail = "";
    String logoutUrl = "";
    String loginUrl = "";

    if (userService.isUserLoggedIn()) {
        userEmail = userService.getCurrentUser().getEmail();
        String urlToRedirectToAfterUserLogsOut = "/";
        logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
    } else {
        String urlToRedirectToAfterUserLogsIn = "/index.html#home";
        loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
    }

    User user = new User(userService.isUserLoggedIn(), userEmail, logoutUrl, loginUrl);

    //Convert user login data to json.
    String json = new Gson().toJson(user);

    //Send the json as a response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
