FROM ubuntu:latest

RUN apt-get -qq update
RUN apt-get -qq install -y build-essential cmake dos2unix

COPY docker-entrypoint.sh /usr/local/bin
RUN dos2unix /usr/local/bin/docker-entrypoint.sh
RUN ln -s usr/local/bin/docker-entrypoint.sh /

WORKDIR /project

ENTRYPOINT [ "docker-entrypoint.sh" ]
