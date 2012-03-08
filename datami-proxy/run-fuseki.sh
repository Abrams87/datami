# example script to run Fuseki to be used alongside datami-proxy
# assumes fuseki has been downloaded and copied in Fuseki-0.2.0

cd Fuseki-0.2.0/
./fuseki-server --update --port=3031 --loc=../repository/ /wll
cd -