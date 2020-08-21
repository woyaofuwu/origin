SELECT COUNT(1) recordcount
  FROM dual
 WHERE (SELECT COUNT(1) 
          FROM tf_a_payrelation
         WHERE acct_id = :ACCT_ID
           AND default_tag = :DEFAULT_TAG
           AND act_tag = :ACT_TAG
           AND TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN start_cycle_id AND
               end_cycle_id) > :NUM