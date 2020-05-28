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

/** Allows for multiple calls to the typeWriter function. */
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

function writeGreeting() {
    const greeting = getRandomGreeting();
    writeText(greeting, "greeting-container");
}

function writeName() {
    const name = 'Smruthi Balajee.';
    writeText(name, "hero-text");
}



