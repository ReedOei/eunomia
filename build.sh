#!/usr/bin/env bash

mvn clean compile test spotbugs:check -DwithHistory org.pitest:pitest-maven:mutationCoverage

