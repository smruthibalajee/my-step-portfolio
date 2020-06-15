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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.data.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Variable to hold an ArrayList of values to convert to JSON.
    List<Comment> commentsList = new ArrayList<>();

    // Get the input from the form for number of comments allowed on page.
    int numComments = getCommentNum(request, response);

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Entity> resultsList = results.asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE));

    for (int i = 0; i < Math.min(numComments, resultsList.size()); i++) {
      Entity entity = resultsList.get(i);
      String name = (String) entity.getProperty("name");
      String email = (String) entity.getProperty("email");
      String type = (String) entity.getProperty("type");
      String msg = (String) entity.getProperty("msg");
      String location = (String) entity.getProperty("location");
      long timestamp = (long) entity.getProperty("timestamp");
      long id = entity.getKey().getId();

      Comment c = new Comment(name, email, type, msg, timestamp, id, location);
      commentsList.add(c);
    }

    //Convert data (ArrayList) to json, now using Gson instead of manually converting with string concatenation.
    String json = new Gson().toJson(commentsList);

    //Send the json as a response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /** Returns the number entered by the user, or -1 if the choice was invalid. */
  private int getCommentNum(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String commentNumString = getParameter(request, "num-comments", "");

    // Convert the input to an int.
    int commentNum;
    try {
      commentNum = Integer.parseInt(commentNumString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + commentNumString);
      return -1;
    }
    return commentNum;
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Define a data store object.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    UserService userService = UserServiceFactory.getUserService();

    // Only logged-in users can post messages
    if (!userService.isUserLoggedIn()) {
      String urlToRedirectToAfterUserLogsIn = "/index.html#home";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      //redirects to the login page if they try to post a comment without logging in.
      response.sendRedirect(loginUrl);
      return;
    }
    
    // Get the input from the form, depending on the type of commentor.
    String name = getParameter(request, "name-input", "");
    String location = getParameter(request, "location-input", "");
    boolean student = Boolean.parseBoolean(getParameter(request, "student", "false"));
    boolean industry = Boolean.parseBoolean(getParameter(request, "industry-professional", "false"));
    boolean recruiter = Boolean.parseBoolean(getParameter(request, "recruiter", "false"));
    String msg = getParameter(request, "comment-input", "");
    long timestamp = System.currentTimeMillis();

    //Email is assigned to comment based on the current user signed in.
    String email = userService.getCurrentUser().getEmail();
    
    String type = "other";

    if (student) {
      type = "student";
    } else if (industry) {
      type = "industry";
    } else if (recruiter) {
      type = "recruiter";
    }

    populateDataStore(datastore, name, email, type, msg, timestamp, location);

    //redirect back to original page
    response.sendRedirect("/index.html#comment");
  }

  /** Helper function to add the name, category, and message to the datastore. */
  private void populateDataStore(DatastoreService d, String name, String email, String type, String msg, long timestamp, String location) {
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("type", " [" + type + "] ");
    commentEntity.setProperty("msg", msg);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("location", location);
    d.put(commentEntity);
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
