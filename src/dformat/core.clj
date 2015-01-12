(ns dformat.core)

(require '[clj-time.format :as f])

;; ## Blah blah
;;
;; Blah blah blah?

(def timezone-regexp 
  (re-pattern 
    (str 
      "^(" 
      (clojure.string/join
        "|" 
        ["ACDT" "ACST"  "ACT"  "ADT"  "AEDT" "AEST"  "AFT"   "AKDT"
         "AKST" "AMST"  "AMT"  "ART"  "AST"  "AWDT"  "AWST"  "AZOST"
         "AZT"  "BDT"   "BIOT" "BIT"  "BOT"  "BRT"   "BST"   "BTTCAT"
         "CCT"  "CDT"   "CEDT" "CEST" "CET"  "CHADT" "CHAST" "CHOT"
         "ChST" "CHUT"  "CIST" "CIT"  "CKT"  "CLST"  "CLT"   "COST"
         "COT"  "CST"   "CT"   "CVT"  "CWST" "CXT"   "DAVT"  "DDUT"
         "DFT"  "EASST" "EAST" "EAT"  "ECT"  "EDT"   "EEDT"  "EEST"
         "EET"  "EGST"  "EGT"  "EIT"  "EST"  "FET"   "FJT"   "FKST"
         "FKT"  "FNT"   "GALT" "GAMT" "GET"  "GFT"   "GILT"  "GIT"
         "GMT"  "GST"   "GYT"  "HADT" "HAEC" "HAST"  "HKT"   "HMT"
         "HOVT" "HST"   "ICT"  "IDT"  "IOT"  "IRDT"  "IRKT"  "IRST"
         "IST"  "JST"   "KGT"  "KOST" "KRAT" "KST"   "LHST"  "LINT"
         "MAGT" "MART"  "MAWT" "MDT"  "MET"  "MEST"  "MHT"   "MIST"
         "MIT"  "MMT"   "MSK"  "MST"  "MUT"  "MVT"   "MYT"   "NCT"
         "NDT"  "NFT"   "NPT"  "NST"  "NT"   "NUT"   "NZDT"  "NZST"
         "OMST" "ORAT"  "PDT"  "PET"  "PETT" "PGT"   "PHOT"  "PHT"
         "PKT"  "PMDT"  "PMST" "PONT" "PST"  "RET"   "ROTT"  "SAKT"
         "SAMT" "SAST"  "SBT"  "SCT"  "SGT"  "SLT"   "SRT"   "SST"
         "SYOT" "TAHT"  "THA"  "TFT"  "TJT"  "TKT"   "TLT"   "TMT"
         "TOT"  "TVT"   "UCT"  "ULAT" "UTC"  "UYST"  "UYT"   "UZT"
         "VET"  "VLAT"  "VOLT" "VOST" "VUT"  "WAKT"  "WAST"  "WAT"
         "WEDT" "WEST"  "WET"  "WST"  "YAKT" "YEKT"]) 
      ")$"))

  "AZOST"
)

(def monthnames-regexp 
  (re-pattern 
    (str 
      "^(" 
      (clojure.string/join
        "|" 
        ["January" "February" "March"     "April"   "May"      "June" 
         "July"    "August"   "September" "October" "November" "December"]) 
      ")$")))

(def abbr-monthnames-regexp 
  #"^(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)$")

(def daynames-regexp
  #"^(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday)$")

(def abbr-daynames-regexp
  #"^(Sun|Mon|Tue|Wed|Thu|Fri|Sat)$")

(def one-digit-regexp #"^\d{1}$")

(def two-digit-regexp #"^\d{2}$")

(def four-digit-regexp #"^\d{4}$")

(def time-regexp #"(\d{1,2})(:)(\d{2})(\s*)(:)?(\d{2})?(\s*)?([ap]m)?")

(def meridian-lower-regexp #"^(a|p)m$")

(def meridian-upper-regexp #"^(A|P)M$")

(defn in-range? [lower upper number] (and (>= upper number) (<= lower number)))

(defn obvious-24-hour? [x] (in-range? 13 23 x))
(defn obvious-day? [x] (in-range? 13 31 x))
(defn obvious-year? [x] (in-range? 32 99 x))

(defn ambiguous? [x] (= (first x) :disambiguate))

(defn analyze-token
  "Arguably the heart of the library. We match off a token against every regex
   we have established as meaningful. If it's an obvious match, we classify it
   as such, if it can be a number of possible fields, we leave it for the
   disambiguation algorithm to decide." 
  [token]
  (cond
    (re-find monthnames-regexp token) [:month "MMMM"]
    (re-find abbr-monthnames-regexp token) [:month "MMM"]
    (re-find daynames-regexp token) [:dow "EEEE"]
    (re-find abbr-daynames-regexp token) [:dow "EEE"]
    (re-find timezone-regexp token) [:timezone "z"]
    (re-find four-digit-regexp token) [:year "YYYY"]
    (and 
      (re-find two-digit-regexp token) 
      (obvious-day? (read-string token))) [:day "dd"]
    (and 
      (re-find two-digit-regexp token) 
      (obvious-year? (read-string token))) [:year "yy"]
    (re-find two-digit-regexp token) 
      [:disambiguate [:month "MM"] [:day "dd"] [:year "yy"]]
    (re-find one-digit-regexp token) 
      [:disambiguate [:month "M"] [:day "d"]]
    :else token))

(defn tokens-to-string 
  "For meaningful tokens (which are two-item tokens), we take the Joda string
   pattern (provided by `analyze-token`). For literals, we just leave them 
   as-is.

   Joda date format strings gotta be quote-delimited for literals.
   Unfortunately, for non-letter literals (like punctuation) single quotes are 
   interpreted as literals, so we have to check the nature of the token before 
   quoting it."
  [tokens]
  (reduce 
    #(str 
      %1 
      (cond 
        (vector? %2) (last %2) 
        (re-find #"[A-z]" %2) (str "'" %2 "'")
        :else %2)) 
    tokens))

(defn disambiguate-token 
  "Internal function of disambiguate – picks the first possible meaning from 
   `token` which is not currently present in the `itemset`"
  [itemset token]
  (if (= (first token) :disambiguate)
    (let [disambiguated
          (first 
            (filter #(not (contains? itemset %)) (map first (rest token))))]
      (first (filter #(= (first %) disambiguated) (rest token))))
    token))

(defn disambiguate 
  "Takes an analyzed string and disambiguated all tokens that might have 
   different meanings depending on usage.

   It does so by comparing possible
   meanings of the token to already classified identifiers and taking the
   first (hopefully, only) meaning which is not present in the format string 
   so far. It uses `reduce` with carried state in order not to use the same 
   meaning in cases two different ambiguous tokens allow for the same meaning."
  [tokens]
  ((reduce
    (fn [state token]
      (let [itemset       (state :itemset)
            disambiguated (disambiguate-token itemset token)]
        {:itemset (conj itemset (first disambiguated))
         :tokens  (conj (state :tokens) disambiguated)}))
    {:itemset 
             (set 
                (filter 
                  #(not= % :disambiguate) 
                  (map first (filter vector? tokens))))
     :tokens []}
    tokens) 
  :tokens))

(defn dformat
  "Takes a clj-time date to format and a sample usage string to get formatting
   from. Returns `date` formatted with the format identical to one of the
   `sample`" 
  [date sample]
  (let [tokens (map analyze-token (clojure.string/split sample #"\b"))]
    (f/unparse 
      (f/formatter 
        (tokens-to-string 
          (if (some ambiguous? tokens) (disambiguate tokens) tokens))) 
      date)))