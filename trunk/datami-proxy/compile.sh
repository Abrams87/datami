#! /bin/bash
# super simple compilation script for datami proxy

cd src 

export CLASSPATH="./:../lib/commons-lang3-3.1.jar:../lib/jenaandhttpclient/arq-2.8.4.jar:../lib/jenaandhttpclient/commons-codec-1.3.jar:../lib/jenaandhttpclient/commons-httpclient-3.1.jar:../lib/jenaandhttpclient/commons-logging-1.0.4.jar:../lib/jenaandhttpclient/icu4j-3.4.4.jar:../lib/jenaandhttpclient/iri-0.8-sources.jar:../lib/jenaandhttpclient/iri-0.8.jar:../lib/jenaandhttpclient/jena-2.6.3-tests.jar:../lib/jenaandhttpclient/jena-2.6.3.jar:../lib/jenaandhttpclient/junit-4.5.jar:../lib/jenaandhttpclient/log4j-1.2.13.jar:../lib/jenaandhttpclient/lucene-core-2.3.1.jar:../lib/jenaandhttpclient/slf4j-api-1.5.8.jar:../lib/jenaandhttpclient/slf4j-log4j12-1.5.8.jar:../lib/jenaandhttpclient/stax-api-1.0.1.jar:../lib/jenaandhttpclient/wstx-asl-3.2.9.jar:../lib/jenaandhttpclient/xercesImpl-2.7.1.jar:../lib/jetty-ajp-7.4.2.v20110526.jar:../lib/jetty-annotations-7.4.2.v20110526.jar:../lib/jetty-client-7.4.2.v20110526.jar:../lib/jetty-continuation-7.4.2.v20110526.jar:../lib/jetty-deploy-7.4.2.v20110526.jar:../lib/jetty-http-7.4.2.v20110526.jar:../lib/jetty-io-7.4.2.v20110526.jar:../lib/jetty-jmx-7.4.2.v20110526.jar:../lib/jetty-jndi-7.4.2.v20110526.jar:../lib/jetty-overlay-deployer-7.4.2.v20110526.jar:../lib/jetty-plus-7.4.2.v20110526.jar:../lib/jetty-policy-7.4.2.v20110526.jar:../lib/jetty-rewrite-7.4.2.v20110526.jar:../lib/jetty-security-7.4.2.v20110526.jar:../lib/jetty-server-7.4.2.v20110526.jar:../lib/jetty-servlet-7.4.2.v20110526.jar:../lib/jetty-servlets-7.4.2.v20110526.jar:../lib/jetty-util-7.4.2.v20110526.jar:../lib/jetty-webapp-7.4.2.v20110526.jar:../lib/jetty-websocket-7.4.2.v20110526.jar:../lib/jetty-xml-7.4.2.v20110526.jar:../lib/servlet-api-2.5.jar"

javac com/weblifelog/proxy/*.java
javac com/weblifelog/process/*.java
cd -

