The DATAMI text processing component is the part of the [datami architecture](http://www.datami.co.uk/?p=12) in charge of running the texts collected using the [datami-proxy](http://www.datami.co.uk/?p=24) through an automatic text annotation service, here the [Apache Stanbol Enhancer](http://incubator.apache.org/stanbol/docs/trunk/enhancer.html).

## What it does ##

The basic tasks of this tool are to query the triple store containing the data collected by the proxy, extract the texts, call the annotation service, save the results through another triple store, and delete the texts from the original triple store.

It will realize this at regular intervals so that the triple store containing the annotation is constantly updated with information coming from the datami-proxy.

## Setting up and running it ##

The source of the DATAMI text processing component are available on the public SVN of the DATAMI Google code repository (see http://code.google.com/p/datami/source/browse/#svn%2Ftrunk%2Fdatami-process-text).

Simple [compilation](http://code.google.com/p/datami/source/browse/trunk/datami-process-text/compile.sh) and [run](http://code.google.com/p/datami/source/browse/trunk/datami-process-text/run-text-process.sh) scripts are provided.

Running this components requires access to two triple stores, one that contains information produced by the datami-proxy, and the other to host the information resulting from the automatic annotation. Once started, the text processing component will regularly query the first triple store, call the annotation service with found new text, and update both triple stores.

The [datami.properties](http://code.google.com/p/datami/source/browse/trunk/datami-process-text/datami.properties) file is used to configure the tool with the URLs of the triple stores' endpoints, of the annotation service and with other useful types of information.