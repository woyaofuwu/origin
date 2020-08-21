--IS_CACHE=Y
SELECT table_name 
  FROM td_cdr_route
 WHERE cdr_type=:CDR_TYPE
   AND cdr_month=:CDR_MONTH