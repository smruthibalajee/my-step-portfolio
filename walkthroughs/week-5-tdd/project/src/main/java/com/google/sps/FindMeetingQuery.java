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

package com.google.sps;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/** Class that implements functions to return all the available timeslots for a meeting between given attendees. **/
public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Check edge case where request is longer than a day
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    // Check edge case where no events have been passed in
    if (events == null) {
      return Arrays.asList();
    }
    
    Collection<String> attendees = request.getAttendees();
    // Check edge case where no attendees have been requested.
    if (attendees.size() == 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    ArrayList<TimeRange> sortedUnavailableTimes = getSortedUnavailableTimes(attendees, events);
    // Check edge case where all the events provided don't have one of the given attendees
    if (sortedUnavailableTimes == null) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    ArrayList<TimeRange> distinctUnavailableTimes = getDistinctUnavailableTimes(sortedUnavailableTimes);
    ArrayList<TimeRange> availableTimes = getFilteredAvailableTimes(distinctUnavailableTimes, request.getDuration());
    return availableTimes;
  }
  
  /** Helper function that returns a ArrayList of all the time ranges that the group of attendees is unavailable for, sorted by start time. **/
  private ArrayList<TimeRange> getSortedUnavailableTimes(Collection<String> attendees, Collection<Event> events) {
    ArrayList<TimeRange> unavailableTimes = new ArrayList<>();
    for (Event e : events) {
      for (String a : e.getAttendees()) {
        // Check if any of the requested attendees are also attending the event.
        if (attendees.contains(a)) {
          // If one attendee is attending the event, add the time range as unavailable and end the loop.
          unavailableTimes.add(e.getWhen());
          break;
        }
      }
    }

    // Check edge case where all the events provided don't have one of the given attendees
    if (unavailableTimes.size() == 0) {
      return null;
    }
    // Sort all of the unavailable time slots by start time.
    Collections.sort(unavailableTimes, TimeRange.ORDER_BY_START);
    return unavailableTimes;
  }

  /** Helper function that returns all the distinct unavailable times by merging times that are equal or overlapping. **/
  private ArrayList<TimeRange> getDistinctUnavailableTimes(ArrayList<TimeRange> sortedUnavailableTimes) {
    ArrayList<TimeRange> distinctUnavailableTimes = new ArrayList<>();
    TimeRange mergedTime = sortedUnavailableTimes.get(0);
    for (int i = 1; i < sortedUnavailableTimes.size(); i++) {
      TimeRange time = sortedUnavailableTimes.get(i);
      // If the current time overlaps with the previous time, merges them.
      if (mergedTime.overlaps(time)) {
        mergedTime = mergeEventTimes(mergedTime, time);
      } else {
        // If current time doesn't overlap, adds the previous time to the ArrayList. Then, sets up the current time to check merge.
        distinctUnavailableTimes.add(mergedTime);
        mergedTime = time;
      }
    }

    // Add the last element, if it was merged.
    distinctUnavailableTimes.add(mergedTime);
    return distinctUnavailableTimes;
  }
  
  /** Helper function that merges two time ranges and returns a new, longer (or equal) time range. **/
  private TimeRange mergeEventTimes(TimeRange time1, TimeRange time2) {
    int newStart = Math.min(time1.start(), time2.start());
    int newEnd = Math.max(time1.end(), time2.end());
    TimeRange newTimeRange = TimeRange.fromStartEnd(newStart, newEnd, false);
    return newTimeRange;
  }

  /** Helper function that returns the available times for all attendees that are at least as long as the duration of the meeting. **/
  private ArrayList<TimeRange> getFilteredAvailableTimes(ArrayList<TimeRange> distinctUnavailableTimes, long duration) {
    ArrayList<TimeRange> availableTimes = new ArrayList<>();
    // Check the time range from the start of the day to the start of first meeting.
    int availableStart = TimeRange.START_OF_DAY;
    int availableEnd = 0;
    for (int i = 0; i < distinctUnavailableTimes.size(); i++) {
      TimeRange currentUnavailable = distinctUnavailableTimes.get(i);
      availableEnd = currentUnavailable.start();
      // If the time range is greater than the duration of the meeting, add it to list of available times.
      if (availableEnd - availableStart >= duration) {
        availableTimes.add(TimeRange.fromStartEnd(availableStart, availableEnd, false));
      }
      availableStart = currentUnavailable.end();
    }
    // Check the time range from the end of last meeting to the end of the day.
    availableEnd = TimeRange.END_OF_DAY;
    if (availableEnd - availableStart >= duration) {
      availableTimes.add(TimeRange.fromStartEnd(availableStart, availableEnd, true));
    }
    return availableTimes;
  }
}
