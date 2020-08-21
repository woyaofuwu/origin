SELECT a.user_id user_id,
       '' ACCT_ID,
       '' FEE,
       0 ACYC_ID,
       '' REASON,
       c.discnt_name remark,
       c.fee1 RSRV_FEE1,
       TO_CHAR(a.start_date, 'YYYYMM') RSRV_DATE,
       '' RSRV_INFO,
       c.fee2 RSRV_INFO1,
       to_char(c.discnt_code) RSRV_INFO2
  FROM tf_f_user_discnt a, TD_B_DISCNT_JFPRESENT c
 WHERE a.user_id = :USER_ID
   AND TO_CHAR(a.start_date, 'YYYYMM') <= :BCYC_ID
   AND TO_CHAR(a.end_date, 'YYYYMM') >= :BCYC_ID
   AND (a.spec_tag = '0' OR a.spec_tag = '2')
   AND a.discnt_code = c.discnt_code