# Elevation-finder
This program finds the minimum and maximum elevation within an area given a coordinate to search.
Different area sizes can optionally be specified by advanced users only in terms of decimal degrees via command line arguments, argument 1 is the latitude search size, and argument 2 is the longitude search size.
The central coordinate is acquired through user input, default search size is 0.01 arcminutes (The size of an average town)
Resolution is always 512 points no matter the search size, as we are limited by funding to pay for Google cloud API requests.
