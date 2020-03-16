FROM openjdk:8-slim

RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y procps

WORKDIR /opt

ARG SPARK_VERSION="spark-2.4.5"
ARG SCALA_VERSION="2.12"
ARG SPARK_BIN="${SPARK_VERSION}-bin-hadoop2.7"
ARG SPARK_DOWNLOAD="https://archive.apache.org/dist/spark/${SPARK_VERSION}/${SPARK_BIN}.tgz"

RUN wget ${SPARK_DOWNLOAD}
RUN tar -zxvf ${SPARK_BIN}.tgz

RUN ln -s ${SPARK_BIN} spark

ARG SPARK_HOME=/opt/spark
ENV SPARK_HOME=${SPARK_HOME}
ENV PATH=${PATH}:${SPARK_HOME}/bin:${SPARK_HOME}/sbin

RUN rm /opt/*tgz

COPY master.sh /opt
COPY worker.sh /opt

