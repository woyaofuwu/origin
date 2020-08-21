SELECT to_char(acct_id) acct_id,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_acct_realpay
 WHERE acct_id = TO_NUMBER(:ACCT_ID)
   AND NVL(TO_DATE(:QUERY_DATE, 'YYYY-MM-DD HH24:MI:SS'),SYSDATE) BETWEEN start_date AND end_date