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

/**
 * Gets a random greeting.
 */
function getRandomGreeting() {
    const greetings =
        ['Welcome!', '¡Bienvenido!', '歡迎!', 'Welkom!'];

    // Pick a random greeting.
    const greeting = greetings[Math.floor(Math.random() * greetings.length)];
    return greeting;
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

/** Function that calls writeText for the welcome messages. */
function writeGreeting() {
    const greeting = getRandomGreeting();
    writeText(greeting, "greeting-container");
}

/** Function that fetches a hard-coded text message from the server and writes it in the hero header. */
function fetchAndWriteMsg() {
    fetch('/data').then(response => response.text()).then((data) => {
        writeText(data, "greeting-container") = data;
    });
}

/** Function that fetches a JSON message from the server and displays it in the hero header. */
function fetchAndDisplayJSON() {
    fetch('/data').then(response => response.json()).then((data) => {
        const dataListElement = document.getElementById('greeting-container');
        dataListElement.innerHTML = '';
        dataListElement.appendChild(createListElement(data.msg1));
        dataListElement.appendChild(createListElement(' ' + data.msg2));
        dataListElement.appendChild(createListElement(' ' + data.msg3));
    });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
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



