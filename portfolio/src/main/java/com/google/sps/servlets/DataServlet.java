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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  //Variable to hold an ArrayList of values to convert to JSON.
  private List<String> comments = new ArrayList<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    //Convert data (ArrayList) to json, now using Gson instead of manually converting with string concatenation.
    String json = new Gson().toJson(comments);

    //Send the json as a response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form, depending on the type of commentor.
    String name = getParameter(request, "name-input", "");
    boolean student = Boolean.parseBoolean(getParameter(request, "student", "false"));
    boolean industry = Boolean.parseBoolean(getParameter(request, "industry-professional", "false"));
    boolean recruiter = Boolean.parseBoolean(getParameter(request, "recruiter", "false"));
    boolean other = Boolean.parseBoolean(getParameter(request, "other", "false"));
    String msg = getParameter(request, "comment-input", "");

    if (student) {
      populateArrayList(comments, name, "student", msg);
    } else if (industry) {
      populateArrayList(comments, name, "industry professional", msg);
    } else if (recruiter) {
      populateArrayList(comments, name, "recruiter", msg);
    } else {
      populateArrayList(comments, name, "other", msg);
    }

    //redirect back to original page
    response.sendRedirect("/index.html#comment");
  }

  /** Helper function to add the name, category, and message to the right ArrayList. */
  private void populateArrayList(List c, String name, String type, String msg) {
    name += " [" + type + "]:";
    c.add(name);
    c.add(msg);
    c.add("_");
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
