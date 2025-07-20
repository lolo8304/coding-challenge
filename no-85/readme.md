# Coding challenge for Build your own time zone converter

I am implementing the foundation for time zone conversion.
based on https://codingchallenges.fyi/challenges/timezone-converter
by John Crickett

# Problem Statement
Write an API that converts a given source time zone into multiple target time zones. The API should accept a date and time in the source time zone and return the equivalent date and time in the target time zones.
The API should be able to handle daylight saving time changes and other time zone rules.

# Development

## Requirements

- java 21
- gradle 
- some 3rd party libraries
  - lombok for builder pattern
  - unit test for 
  - picocli for command line interface
  - spark for web server
  - gson for json parsing
- pure java classes 

## Build

```bash
gradle installDist
```



## Solution 

java based conversion with LocalDateTime and ZonedDateTime.
Use spark to expose the API as a web service

# Run

## Command line
```bash
./timezone.sh -h
Usage: timezone [-hvV] [-vv] [-s=<sourceTimezone>] [-c
                [=<targetTimezoneCities>...]] [--cc
                [=<targetTimezoneCountries>...]] [-t[=<targetTimezones>...]]
This challenge is to build your own timezone converter
  -c=[<targetTimezoneCities>...]
                         set target timezone abbreviations based on a city name
                           separated by ,
      --cc[=<targetTimezoneCountries>...]
                         set target timezone country based separated by ,
  -h, --help             Show this help message and exit.
  -s=<sourceTimezone>    set source timezone abbreviation. default is current
                           timezone is Europe/Zurich
  -t=[<targetTimezones>...]
                         set target timezone abbreviations separated by ,
  -v                     verbose model level 1
  -V, --version          Print version information and exit.
      -vv                verbose model level 2
```

## Example

### Command line using city names (timezone name)
```bash
./timezone.sh -s=America/New_York -t=Europe/Zurich
```

### Command line using country names (timezone name)
```bash
./timezone.sh -s=America/New_York --cc=DE
```

it will calculate the timezone based on the country code in ISO 3166-1 alpha-2 format.
It could get multiple timezones countries, separated by a comma.

### Command line using city names (timezone abbreviation)
```bash
./timezone.sh -s=America/New_York -c=Zurich
```

the citynames are seperated by a comma, case insensitive

## Web service
if no country,  city or timezone is specified, then  an timezone APi is exposed

POST /timezone-converter
```json
{
  "source": "Europe/Zurich",
  "countries": [
    "US"
  ]
}
```


### Order

the order of the command line parameters is important.
- search for cities first
- search for countries
- search for timezones

# TODO

- impl better return values