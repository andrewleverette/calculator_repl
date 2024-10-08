(ns calculator-repl.core
  (:gen-class)
  (:require
   [calculator-repl.printer :as printer]
   [calculator-repl.evaluator :refer [evaluate]]
   [calculator-repl.reader.core :refer [read-expression]]
   [calculator-repl.history :refer [update-history clear-history]]))

(defn display-welcome-message
  []
  (printer/display-line "==================================================")
  (printer/display-line "Welcome to Calculator REPL")
  (printer/display-line "Enter an arithmetic expression to evaluate")
  (printer/display-line "Currently supported operators: + - * / ^ %")
  (printer/display-line "History trakcing is available via 'p1', 'p2', 'p3'")
  (printer/display-line "Type 'quit' or 'exit'  to stop the REPL")
  (printer/display-line "=================================================="))

(defn should-quit?
  [s]
  (#{"quit" "exit"} s))

(defn calculate
  [input history]
  (try
    (let [result (-> input read-expression (evaluate history))]
      (update-history history result)
      {:ok result})
    (catch Exception e
      {:error (.getMessage e)})))

(defn -main
  []
  (display-welcome-message)
  (let [history (atom [])]
    (loop []
      (printer/display "(in)=> " :with-flush? true)
      (let [input (read-line)]
        (if (should-quit? input)
          (do
            (clear-history history)
            (printer/display-line "Bye!"))
          (let [{:keys [ok error]} (calculate input history)]
            (if ok
              (printer/display-line (str "(out)=> " ok))
              (printer/display-line (str "(err)=> " error)))
            (recur)))))))
