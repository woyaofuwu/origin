SELECT COUNT(1) recordcount
  FROM dual
 WHERE (
         SELECT COUNT(1) FROM tf_a_payrelation
          WHERE acct_id= :ACCT_ID
            AND default_tag= :DEFAULT_TAG
            AND act_tag=:ACT_TAG
            AND (SELECT MIN(cycle_id) FROM TD_B_CYCLE WHERE use_tag=0) BETWEEN start_cycle_id  AND end_cycle_id
            and rownum < 2)
       > :NUM