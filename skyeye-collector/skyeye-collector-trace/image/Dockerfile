# build the base image: jdk
# this is the docker file, use the jdk 8u144-ubuntu16.04
# VERSION 1
# Author: leviqian

# the basic image
FROM 192.168.88.73:8888/common/jdk:8u144-ubuntu16.04

# maintainer
MAINTAINER leviqian leviqian@sina.com

# get the args
ARG version
ARG branch

# set env
ENV VERSION $version
ENV BRANCH $branch

# copy the file
RUN mkdir -p /home/deploy
ADD skyeye-collector-trace-$version.tar /home/deploy
COPY run.sh /run.sh
RUN chmod +x /run.sh

# CMD to start
CMD ["/run.sh"]
