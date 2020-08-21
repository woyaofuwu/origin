--IS_CACHE=Y
SELECT COUNT(1) recordcount
  FROM td_a_acycpara
 WHERE acyc_id = (SELECT acyc_id-1
                    FROM td_a_acycpara
                   WHERE acyc_start_time <= sysdate
                     AND SYSDATE < acyc_end_time)
   AND use_tag = '0'
   AND TO_CHAR(SYSDATE,'dd') = '01'