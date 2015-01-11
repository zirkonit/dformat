(ns dformat.core-test
  (:require [clojure.test :refer :all]
            [dformat.core :refer :all]
            [clj-time.core :as t]))

(deftest stamp-test
	(let [date (t/date-time 2011 6 9)] 
		  (testing "Stamp code examples"
			(is (= (dformat date "March 1, 1999") "June 9, 2011"))
			(is (= (dformat date "Jan 1, 1999") "Jun 9, 2011"))
			(is (= (dformat date "Jan 01") "Jun 09"))
			(is (= (dformat date "Sunday, May 1, 2000") "Thursday, June 9, 2011"))
			(is (= (dformat date "Sun Aug 5") "Thu Jun 9"))
			(is (= (dformat date "12/31/99") "06/09/11"))
			(is (= (dformat date "DOB: 12/31/2000") "DOB: 06/09/2011"))
			(is (= (dformat date "March 15, 1999") "June 09, 2011"))))
	)