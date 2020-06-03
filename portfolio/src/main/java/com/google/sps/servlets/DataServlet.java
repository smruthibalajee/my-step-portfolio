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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  //Variable to hold an ArrayList of values to convert to JSON.
  private List<String> msg;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Initializes the ArrayList.
    msg = new ArrayList();
    msg.add("Hello there!");
    msg.add("Welcome to my portfolio!");
    msg.add("Scroll for more info.");

    //Convert data (ArrayList) to json.
    String json = convertToJson(msg);

    //Send the json as a response.
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /** Converts a DataServlet instance into a JSON string using manual String concatentation. */   
  private String convertToJson(ArrayList msg) {
    String json = "{";
    json += "\"msg1\": ";
    json += "\"" + msg.get(0) + "\"";
    json += ", ";
    json += "\"msg2\": ";
    json += "\"" + msg.get(1) + "\"";
    json += ", ";
    json += "\"msg3\": ";
    json += "\"" + msg.get(2) + "\"";
    json += "}";
    return json;
  }
}
