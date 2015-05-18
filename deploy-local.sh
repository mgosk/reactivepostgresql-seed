#!/usr/bin/env bash
sbt debian:packageBin
sbt flywayMigrate
aptitude purge reactivepostgresql-seed -y
dpkg -i target/reactivepostgresql-seed*.deb
