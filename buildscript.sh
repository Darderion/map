#!/bin/bash

rm -r -f build &&
bash scripts/build_server.sh &&
bash scripts/build_webapp.sh
