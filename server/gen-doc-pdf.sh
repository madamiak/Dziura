#!/bin/bash

# Skrypt generujący dokumentację w PDF
# Potrzebne jest narzędzie: http://code.google.com/p/wkhtmltopdf/ (wersja 0.9.9)
# Plus odkomentować gem 'rails-erb' w Gemfile

PDF_CONV=wkhtmltopdf
OUTPUT=doc/doc_pdf.pdf

rm -rf doc/app doc/erd.png
rake doc:app
rake erd filetype=png title='Diagram ERD bazy danych' filename='doc/erd' orientation=vertical
cp -f doc/pdf_layout.css doc/app/rdoc.css

$PDF_CONV -s A4 \
 doc/app/index.html \
 doc/app/doc/interface_txt.html \
 doc/app/doc/config_txt.html \
 doc/app/doc/models_txt.html \
 doc/erd.html \
 doc/app/Address.html \
 doc/app/Category.html \
 doc/app/Issue.html \
 doc/app/IssueInstance.html \
 doc/app/Log.html \
 doc/app/Marker.html \
 doc/app/Photo.html \
 doc/app/Point.html \
 doc/app/Polygon.html \
 doc/app/Status.html \
 doc/app/Unit.html \
 doc/app/User.html \
 doc/app/doc/controllers_txt.html \
 doc/app/ServicesController.html \
 doc/app/IssuesController.html \
 $OUTPUT
