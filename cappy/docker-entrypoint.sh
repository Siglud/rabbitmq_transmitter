#!/bin/bash
set -e

# opencc
export LD_LIBRARY_PATH=/usr/lib:LD_LIBRARY_PATH

exec $@