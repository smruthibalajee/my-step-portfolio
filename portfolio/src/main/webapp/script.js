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

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(init);
var colors = {'darkest-green' : '#006400','dark-green' : '#228b22','medium-green' : '#8fbc8f','light-green' : '#97bf80'};
//raw data
var languages = [['Language', 'Proficiency Level', { role: 'style' }, { role: 'annotation' } ],
                ['Java', 5, colors['darkest-green'], 'Advanced' ],
                ['Python', 5, colors['darkest-green'], 'Advanced' ],
                ['Javascript', 4, colors['dark-green'], 'Intermediate' ],
                ['HTML/CSS', 4, colors['dark-green'], 'Intermediate' ],
                ['Scheme', 3, colors['medium-green'], 'Intermediate' ],
                ['SQL', 3, colors['medium-green'], 'Intermediate' ],
                ['C', 2, colors['light-green'], 'Beginner' ]
];

var technologies = [['Tool/Technology', 'Proficiency Level', { role: 'style' }, { role: 'annotation' } ],
                ['Autodesk Inventor', 5, colors['darkest-green'], 'Advanced' ],
                ['JupyterLab', 5, colors['darkest-green'], 'Advanced' ],
                ['NumPy', 4, colors['dark-green'], 'Intermediate' ],
                ['Git', 4, colors['dark-green'], 'Intermediate' ],
                ['Agile Scrum', 3, colors['medium-green'], 'Intermediate' ],
                ['Adobe Suite', 2, colors['light-green'], 'Beginner' ]
];

function init() {
    // Create and populate the data tables.
    var data = [];
    data[0] = google.visualization.arrayToDataTable(languages);
    data[1] = google.visualization.arrayToDataTable(technologies);

    var options = {
        'title': 'Skills',
        'width':900,
        'height':450,
        'backgroundColor': {stroke:null, fill:null, strokeSize: 0},
        hAxis: {minValue:0, maxValue:5, format: '0'},
        animation:{
            duration: 1000,
            easing: 'out'
        },
        legend: { position: "none" },
        fontName: 'Montserrat',
        fontSize: 12
    };

    //variable to toggle between charts
    var current = 0;

    // Create and draw the visualization.
    var chart = new google.visualization.BarChart(document.getElementById('chart-container'));
    var button = document.getElementById('b1');

    //Function draws the correct chart based on button value.
    function drawChart() {
        // Disabling the button while the chart is drawing.
        button.disabled = true;
        google.visualization.events.addListener(chart, 'ready',
            function() {
                button.disabled = false;
                button.innerHTML = 'Switch to ' + (current ? 'Languages' : 'Technologies');
            });
        options['title'] = 'Proficiency in Various ' + (current ? 'Tools and Technologies' : 'Programming Languages');

        chart.draw(data[current], options);
    }

    //Draws language chart when loaded
    drawChart();

    //Changes button value when toggled
    button.onclick = function() {
    current = 1 - current;
    drawChart();
    }
}

// Initialize and add the map
function initMap() {
    // My location
    var dublin = {lat: 37.702152, lng: -121.935791};
    // The map, centered at Dublin
    var map = new google.maps.Map(document.getElementById('map'), {zoom: 6, center: dublin});
    // The marker, positioned at Dublin
    var marker = new google.maps.Marker({position: dublin, map: map});
    var contentString = 'Smruthi';
    var infowindow = new google.maps.InfoWindow({
    content: contentString
    });
    marker.addListener('click', function() {
    infowindow.open(map, marker);
    });
}

//Array of greetings in different languages.
var greetings = ['Welcome!', '¡Bienvenido!', 'Bienvenue!', 'Welkom!', 
'Velkommen!', 'Benvenuto!'];

/** Gets a random greeting. */
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

/** Function that fetches a specified number of comments from the server and displays it in the comment section. 
    Default number shown is 5 comments. */
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

    const locationElement = document.createElement('span');
    locationElement.innerText = comment.location;

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
    commentElement.appendChild(locationElement);
    commentElement.appendChild(msgElement);
    commentElement.appendChild(brElement);
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



