SELECT COUNT(1) recordcount
  FROM dual
 WHERE (  SELECT COUNT(1) FROM tf_a_payrelation
          WHERE acct_id= :ACCT_ID
            AND default_tag= '1'
            AND act_tag='1'
            AND (SELECT MIN(acyc_id) FROM td_a_acycpara WHERE use_tag=0) BETWEEN start_acyc_id  AND end_acyc_id)> 1