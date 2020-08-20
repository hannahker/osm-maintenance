# osm-maintenance
This repository contains code that can be used to A) Collect historical data using the [OSHDB API](https://github.com/GIScience/oshdb), and B) Clean, filter, analyze, and visualize this data to understand characteristics of data production and maintenance for a given area. 

### Necessary Inputs

This code requires an OSHDB extract for a given area of interest. Extracts can be downloaded directly from HeiGIt's [download server](http://downloads.ohsome.org/v0.5/), or generated using the ETL process described [here](https://github.com/GIScience/oshdb/tree/master/oshdb-tool/etl). 

This code also requires a simple CSV file with spatial and temporal parameters that describe the extent of your area of interest. The format of this CSV file should follow the example provided here, _case_studies.csv_. As is included in this file, you should specify a brief string identifier for your case study or area of interest (_case-study-name_). 

### Procedure 

1. As is described in [this](https://github.com/GIScience/oshdb/tree/master/documentation/first-steps) _first steps_ tutorial, you should begin by creating a new Java maven project and add OSHDB as a dependency. 
2. Using the OSHDB API, the _StreamOSHDB.java_ file will generate a CSV file that contains all NODES or WAYS that were produced in OSM within the spatial and temporal extents defined in your _case_studies.csv_ file. This file requires the _bounding box_, _start date_, _end date_, and _output file path_ variables to be defined. This file should be run multiple times if multiple case studies or areas are being investigated. As example output file can be found within the Data folder. Note that in this file, all OSM user IDs have been replaced with the number, 1. 
3. Note that, to be compatible with the subsequent data analysis script, this output CSV file should follow the _case-study-name_.csv naming convention. 
3. This output CSV file can then be processed using the _Data_Processing_ notebook. This notebook contains a series of functions for loading and cleaning data, calculating summary statistics, parsing feature tags, creating shapefiles, and calculating maintenance. This notebook also contains code for creating numerous data visualizations to communicate key results. This _Data_Processing_ file requires you to specify your <case-study-name> as an input parameter to read in the appropriate data.  

