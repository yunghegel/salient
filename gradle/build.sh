#!/bin/bash
timestamp=$(date +%Y%m%d%H%M%S)
gradle run --profile
mv -p build/reports/profile/profile-*.html /path/to/history/profile-$timestamp.html