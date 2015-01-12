# dformat

A Clojure library designed to simplify handling time and date format strings by using human-provided examples. Inspired by / shamelessly stolen from [jeremyw's stamp](https://github.com/jeremyw/stamp)

## Usage

Give `dformat` an example date string with whatever month, day, year, and weekday parts you'd like, and your date will be formatted accordingly:

```clojure
(def date (clj-time.core/date-time 2011 6 9)

(dformat date "March 1, 1999") 		 ;; "June 9, 2011"
(dformat date "Jan 1, 1999")   		 ;; "Jun 9, 2011"
(dformat date "Jan 01")        		 ;; "Jun 09"
(dformat date "Sunday, May 1, 2000") ;; "Thursday, June 9, 2011"
(dformat date "Sun Aug 5")           ;; "Thu Jun 9"
(dformat date "12/31/99")            ;; "06/09/11"
(dformat date "DOB: 12/31/2000")     ;; "DOB: 06/09/2011"
(dformat date "March 15, 1999")      ;; "June 09, 2011"

```

Also, there is `dformatter` method which returns `clj-time` date formatter.

```clojure
(require '[clj-time.format :as f])


(def custom-formatter (clj-time.format/formatter "yyyy-MM-dd"))

(clj-time.format/unparse 
	custom-formatter 
	(clj-time.core/date-time 2010 10 3))	;; "2010-10-03"
(clj-time.format/unparse 
	(dformatter "2012-11-21") 
	(clj-time.core/date-time 2010 10 3))	;; "2010-10-03"

```

## Features

* Abbreviated and full names of months and weekdays are recognized.
* Days with or without a leading zero work instinctively.
* Standard time zone abbreviations are recognized; e.g. "UTC", "PST", "EST".
* Include any extraneous text you'd like; e.g. "DOB:".

## Disambiguation by value

You can use any month, weekday, day, or year value that makes sense in your examples, and stamp can often infer your intent based on context, but there may be times that you need to use unambiguous values to make your intent more explicit.

For example, "01/09" could refer to January 9, September 1, or January 2009. More explicit examples include "12/31", "31/12", and "12/99".

Using unambiguous values will also help people who read the code in the future, including yourself, understand your intent.

## License

Copyright © 2015 zirkonit

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
