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
import com.google.gson.Gson;
import com.google.sps.data.Comment;
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
      String type = (String) entity.getProperty("type");
      String msg = (String) entity.getProperty("msg");
      long timestamp = (long) entity.getProperty("timestamp");
      long id = entity.getKey().getId();

      Comment c = new Comment(name, type, msg, timestamp, id);
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
    
    // Get the input from the form, depending on the type of commentor.
    String name = getParameter(request, "name-input", "");
    boolean student = Boolean.parseBoolean(getParameter(request, "student", "false"));
    boolean industry = Boolean.parseBoolean(getParameter(request, "industry-professional", "false"));
    boolean recruiter = Boolean.parseBoolean(getParameter(request, "recruiter", "false"));
    boolean other = Boolean.parseBoolean(getParameter(request, "other", "false"));
    String msg = getParameter(request, "comment-input", "");
    long timestamp = System.currentTimeMillis();

    if (student) {
      populateDataStore(datastore, name, "student", msg, timestamp);
    } else if (industry) {
      populateDataStore(datastore, name, "industry professional", msg, timestamp);
    } else if (recruiter) {
      populateDataStore(datastore, name, "recruiter", msg, timestamp);
    } else {
      populateDataStore(datastore, name, "other", msg, timestamp);
    }

    //redirect back to original page
    response.sendRedirect("/index.html#comment");
  }

  /** Helper function to add the name, category, and message to the datastore. */
  private void populateDataStore(DatastoreService d, String name, String type, String msg, long timestamp) {
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("type", " [" + type + "]:");
    commentEntity.setProperty("msg", msg);
    commentEntity.setProperty("timestamp", timestamp);
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
