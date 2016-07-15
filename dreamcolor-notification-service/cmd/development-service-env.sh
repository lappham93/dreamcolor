#!/bin/sh

#
# Common options need to change: JVM_XMX, JVM_JMX_HOST, JVM_JMX_PORT
#
#
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
# common attributes
#CONF_FILES=config.ini

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
# app arguments: empty means disable or not-available

APP_ARGS=" -Dlogback.configurationFile=conf/logback-development.xml "

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
# jvm arguments: empty means disable or not-available

JVM_XMX=256M
JVM_XMS=
JVM_JMX_HOST=
JVM_JMX_PORT=
JVM_JDWP_PORT=

#jvm extra options
JVM_EXTRA_ARGS=""

