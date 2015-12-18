The datami-interface is a Web interface that shows the content of the knowledge base/triple store produced through applying the [datami-proxy](http://www.datami.co.uk/?p=24) and the [datami text processing component](http://www.datami.co.uk/?p=59). It shows popular entities and websites in tag clouds, make it possible to obtain more info for each and allow the user to define filters based on selected entities, websites and dates.

It is developed using HTML and javascript, relying on the sparql.js library for querying the triple store, and on other (slightly adapted) interface components on top of the jQuery framework, namely the jquery tooltip plugin and the jquery calendar picker plugin.

To run it, it only requires to [get the code](http://code.google.com/p/datami/source/browse/#svn%2Ftrunk%2Fdatami-interface) and point a javascript compliant web browser to it (it is most likely not to work with versions of Internet Explorer below 8). The [datami.query.js](http://code.google.com/p/datami/source/browse/trunk/datami-interface/js/datami.query.js) however needs to be changed so that the mentions to "http://your.server.com:3030/datami/query" are replaced by the URL of your own SPARQL endpoint.