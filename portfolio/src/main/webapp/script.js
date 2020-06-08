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

//Array of greetings in different languages.
var greetings = ['Welcome!', '¡Bienvenido!', 'Bienvenue!', 'Welkom!', 
'Velkommen!', 'Benvenuto!'];

/**
 * Gets a random greeting.
 */
function getRandomGreetingIndex() {
    return Math.floor(Math.random() * greetings.length);
}

/** Allows for multiple calls to the typeWriter function, writes text on the webpage. */
function writeText(inputText, container) {
    var i = 0;
    var txt = inputText;
    var speed = 90;

    /** Types greeting on page. */
    function typeWriter() {
        if (i < txt.length) {
            document.getElementById(container).innerHTML += txt.charAt(i);
            i++;
            setTimeout(typeWriter, speed);
        }
    }
    document.getElementById(container).innerHTML = "";
    typeWriter();
}

/** Function that calls writeText for the welcome messages. Ensures that no greeting is repeated twice in a row.*/
var oldIndex = 0;
function writeGreeting() {
    var greetingIndex = getRandomGreetingIndex();
    if (oldIndex != greetingIndex) {
        oldIndex = greetingIndex;
        writeText(greetings[greetingIndex], "greeting-container");
    } else {
        oldIndex = oldIndex + 1;
        if (oldIndex >= greetings.length) {
            oldIndex = 0;
        }
        writeText(greetings[oldIndex], "greeting-container");
    }
}

/** Function that fetches a hard-coded text message from the server and writes it in the hero header. */
function fetchAndWriteMsg() {
    fetch('/data').then(response => response.text()).then((data) => {
        writeText(data, "greeting-container") = data;
    });
}

/** Function that fetches a comment from the server and displays it in the comment section. 
    Automatically only displays 5 comments unless user picks another amount. 
*/
function fetchAndDisplayComments() {
    const dataListElement = document.getElementById('comments-container');
    fetch('/data?num-comments=5').then(response => response.json()).then((data) => {
        data.forEach((comment) => {
            dataListElement.appendChild(createCommentElement(comment));
        });
    });
}

/** Function that fetches a specified number of comments from the server and displays it in the comment section 
    (only called when a value is picked from the dropdown). 
*/
function fetchAndDisplayNumComments(num) {
    document.getElementById('comments-container').innerHTML = "";
    const dataListElement = document.getElementById('comments-container');
    fetch('/data?num-comments='+num).then(response => response.json()).then((data) => {
        data.forEach((comment) => {
            dataListElement.appendChild(createCommentElement(comment));
        });
    }); 
}

/** Creates a comment element by converting the object into Strings and concatenating them. */
function createCommentElement(comment) {
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const nameElement = document.createElement('span');
  nameElement.innerText = comment.name;

  const typeElement = document.createElement('span');
  typeElement.innerText = comment.type;

  const msgElement = document.createElement('li');
  msgElement.innerText = comment.msg;

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteComment(comment);

    // Remove the task from the DOM.
    commentElement.remove();
  });

  const brElement = document.createElement('br');

  commentElement.appendChild(nameElement);
  commentElement.appendChild(typeElement);
  commentElement.appendChild(msgElement);
  commentElement.appendChild(deleteButtonElement);
  commentElement.appendChild(brElement);
  return commentElement;
}

/** Tells the server to delete the comment. */
function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-comment', {method: 'POST', body: params});
}

/** Function that calls writeText for the name in the hero header. */
function writeName() {
    const name = 'Smruthi Balajee.';
    writeText(name, "hero-text");
}

/** Hides the navbar when scrolling down, nav bar appears when scrolling up. */
var prevScrollpos = window.pageYOffset;
window.onscroll = function() {
var currentScrollPos = window.pageYOffset;
    if (prevScrollpos > currentScrollPos) {
        document.getElementById("navbar").style.top = "0";
    } else {
        document.getElementById("navbar").style.top = "-50px";
    }
    prevScrollpos = currentScrollPos;
}



