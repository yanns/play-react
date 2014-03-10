play-react
==========

Proof of concept of running react.js on the server side with play

The page under [http://localhost:9000/clientSide] suffers with a "Flash Of No-Data Content".
To display some data, the browser must:
1. load the page and the html
2. make an ajax request to the server
3. display the data

This sample application simulates a slow AJAX response to make this flash visible.

The page under [http://localhost:9000/serverSide] pre-renders the html on the server side
and displays the page already with data.

The changes on the client side are then applied dynamically with the normal client side JavaScript
ReactJS code.

The pre-render the ReactJS components on the server side, the following libraries are used:
- [http://localhost:9000/serverSide] uses [trireme](https://github.com/apigee/trireme) provides a Node API on the JVM with Rhino
- [http://localhost:9000/serverSide2] uses [js-engine](https://github.com/typesafehub/js-engine) that itself uses [trireme](https://github.com/apigee/trireme) behind [Akka](http://akka.io/) actors

TODOs:
- use nodejs styles modules for the client side libraries -> bind [browserify](http://browserify.org/) or similar tool
- the pre-rendered page waits for the data before sending any bytes to the browser. To optimize this, we could send the first bytes immediately, to let the browser load the CSS / JavaScript, and send the rest of the body when available.
For this, we can use the same implementation as the one presented there: http://de.slideshare.net/brikis98/composable-and-streamable-play-apps
