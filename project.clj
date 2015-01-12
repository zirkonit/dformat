(defproject dformat "0.1.0"
  :description "A library designed to simplify handling time and date format strings by using human-provided examples."
  :url "https://github.com/zirkonit/dformat"
  :scm {:name "git"
        :url  "https://github.com/zirkonit/dformat"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
  				 [clj-time "0.9.0"]]
  :signing {:gpg-key "zirkonit@gmail.com"}
  :deploy-repositories [["clojars" {:creds :gpg}]]
  )
