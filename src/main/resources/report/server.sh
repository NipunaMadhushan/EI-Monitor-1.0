#!/bin/sh
echo Reporter dashboard started on http://localhost:3030/report.html
exec python -m SimpleHTTPServer 3030
