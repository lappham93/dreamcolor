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

APP_ARGS=""

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
# jvm arguments: empty means disable or not-available

JVM_XMX=128M
JVM_XMS=
JVM_JMX_HOST=
JVM_JMX_PORT=
JVM_JDWP_PORT=65008

#jvm extra options
JVM_EXTRA_ARGS=""

