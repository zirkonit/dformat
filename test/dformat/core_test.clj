(ns dformat.core-test
  (:require [clojure.test :refer :all]
            [dformat.core :refer :all]
            [clj-time.core :as t]))

(deftest stamp-test
	(let [date (t/local-date 2011 6 9)] 
		  (testing "FIXME, I fail."
			(is (= (dformat date "March 1, 1999") "June 9, 2011"))))
	)