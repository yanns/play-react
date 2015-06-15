play-react
==========

Proof of concept of running react.js on the server side with play

[Details](http://yanns.github.io/blog/2014/03/15/server-side-rendering-for-javascript-reactjs-framework/)

Rendering on the Client Side
----------------------------

The page under [/clientSide](http://play-react.herokuapp.com/clientSide) suffers from a "Flash Of No-Data Content".
To display some data, the browser must:
 1. load the page and the html, displaying html without data
 2. make an ajax request to the server
 3. wait for an anwser
 4. display the data

This sample application simulates a slow AJAX response to make this flash visible.

Pre rendering on the Server Side 
--------------------------------

The page under [/serverSide](http://play-react.herokuapp.com/serverSide) pre-renders the html on the server side
and displays the page already with data.

The changes on the client side are then applied dynamically with the normal client side JavaScript
ReactJS code.

The pre-render the ReactJS components on the server side, the following libraries are used:
- [/serverSide](http://play-react.herokuapp.com/serverSide) uses [trireme](https://github.com/apigee/trireme) provides a Node API on the JVM with Rhino
- [/serverSide2](http://play-react.herokuapp.com/serverSide2) uses [js-engine](https://github.com/typesafehub/js-engine) that itself uses [trireme](https://github.com/apigee/trireme) behind [Akka](http://akka.io/) actors
- /serverSideJavax uses [js-engine](https://github.com/typesafehub/js-engine) that itself uses the Javax engine
- /serverSideNode uses [js-engine](https://github.com/typesafehub/js-engine) that itself uses NodeJS

Pre rendering on the Server Side and streaming the page
-------------------------------------------------------

The pre-rendered page waits for the data before sending any bytes to the browser.
To optimize this, another version [/serverSideStream](http://play-react.herokuapp.com/serverSideStream) sends the first bytes immediately, to let the browser load the CSS / JavaScript, and then sends the rest of the body when available.
For this, I used the Facebook’s BigPipe concept as presented in the [talk “Building composable, streaming, testable Play apps” from Yevgeniy Brikman](http://de.slideshare.net/brikis98/composable-and-streamable-play-apps)

TODOs:
------

- use nodejs styles modules for the client side libraries -> bind [browserify](http://browserify.org/) or similar tool

