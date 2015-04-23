#!/bin/bash

############
### INIT ###
############
# get python packages installation tool
wget https://raw.github.com/pypa/pip/master/contrib/get-pip.py
sudo python2.7 get-pip.py
sudo rm get-pip.py*
#######################################
### INSTALL SCRIPTS FOR THE PROJECT ###
#######################################
# scikit-learn package install
echo "#### Installing scikit-learn... ####"
sudo apt-get -y install build-essential python-dev python-setuptools \
                     python-numpy python-scipy \
                     libatlas-dev libatlas3gf-base
sudo update-alternatives --set libblas.so.3 \
    /usr/lib/atlas-base/atlas/libblas.so.3
sudo update-alternatives --set liblapack.so.3 \
    /usr/lib/atlas-base/atlas/liblapack.so.3
yes w | pip2.7 install --install-option="--prefix=" -U scikit-learn
# requests package install
echo "#### Installing requests... ####"
yes w | pip2.7 install --install-option="--prefix=" -U requests
# lxml package install
echo "#### Installing lxml... ####"
sudo apt-get -y install libxslt1-dev libxslt1.1 libxml2-dev libxml2 libssl-dev
yes w | pip2.7 install --install-option="--prefix=" -U lxml
# BeautifulSoup package install
echo "#### Installing BeautifulSoup... ####"
yes w | pip2.7 install --install-option="--prefix=" -U BeautifulSoup
# ZeroMQ package install
echo "#### Installing ZeroMQ... ####"
sudo apt-get -y install libzmq3-dev
yes w | pip2.7 install --install-option="--prefix=" -U pyzmq
# JZMQ install
echo "#### GETTING pkg-config... ####"
sudo apt-get -y install pkg-config
echo "####"
echo "#### GETTING libtool... ####"
sudo apt-get -y install libtool
echo "####"
echo "#### GETTING uuid-dev... ####"
sudo apt-get -y install uuid-dev
echo "####"
echo "#### GETTING autoconf... ####"
sudo apt-get -y install autoconf
echo "####"
echo "#### INSTALLING jzmq... ####"
git clone https://github.com/zeromq/jzmq.git && cd jzmq
./autogen.sh
./configure
make
sudo make install
echo "####"
echo "#### REMOVING installation files... ####"
cd ..
sudo rm -R jzmq && sudo rm -R pkg-config-0.28 && sudo rm pkgconfig.tgz
echo "####"
echo "#### RUNNING ldconfig... ####"
sudo ldconfig
echo "#### DONE. ####"
