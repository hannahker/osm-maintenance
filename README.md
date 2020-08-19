# osm-maintenance
This repository contains code that can be used to A) Collect historical data using the [OSHDB API](https://github.com/GIScience/oshdb), and B) Clean, filter, analyze, and visualize this data to understand characteristics of data production and maintenance for a given area. 

### Necessary Inputs

This code requires an OSHDB extract for a given area of interest. Extracts can be downloaded directly from HeiGIt's [download server](http://downloads.ohsome.org/v0.5/), or generated using the ETL process described [here](https://github.com/GIScience/oshdb/tree/master/oshdb-tool/etl). 

This code also requires a simple CSV file with spatial and temporal parameters that describe the extent of your area of interest. The format of this CSV file should follow the example provided here, _case_studies.csv_. 

### Procedure 

1. As is described in [this](https://github.com/GIScience/oshdb/tree/master/documentation/first-steps) _first steps_ tutorial, you should begin by creating a new Java maven project and add OSHDB as a dependency. 
2. Using the OSHDB API, the _data_stream.java_ file will generate a CSV file that contains all NODES or WAYS that were produced in OSM within the spatial and temporal extents defined in your _case_studies.csv_ file. This file requires the _bounding box_, _start date_, _end date_, and _output file path_ variables to be defined. This file should be run multiple times if multiple case studies or areas are being investigated. 

