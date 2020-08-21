SELECT a.user_id user_id,
       '' ACCT_ID,
       '' FEE,
       0 ACYC_ID,
       '' REASON,
       c.discnt_name remark,
       c.fee1 RSRV_FEE1,
       '' RSRV_DATE,
       '' RSRV_INFO,
       c.fee2 RSRV_INFO1,
       to_char(c.discnt_code) RSRV_INFO2
  FROM mv_tf_f_user_discnt a, TD_B_DISCNT_JFPRESENT c
 WHERE a.user_id = :USER_ID
   AND a.start_date <
       (to_date(:END_TIME, 'YYYY-MM-DD HH24:MI:SS') - 1/24/3600)
   AND a.end_date >=
       (to_date(:END_TIME, 'YYYY-MM-DD HH24:MI:SS') - 1/24/3600)
   AND (a.spec_tag = '0' OR a.spec_tag = '2')
   AND a.discnt_code = c.discnt_code
UNION ALL
SELECT b.user_id user_id,
       '' ACCT_ID,
       '' FEE,
       0 ACYC_ID,
       '' REASON,
       c.discnt_name remark,
       c.fee1 RSRV_FEE1,
       '' RSRV_DATE,
       '' RSRV_INFO,
       c.fee2 RSRV_INFO1,
       to_char(c.discnt_code) RSRV_INFO2
  FROM mv_tf_f_user_infochange b, TD_B_DISCNT_JFPRESENT c
 WHERE b.user_id = :USER_ID
   AND b.start_date <
       (to_date(:END_TIME, 'YYYY-MM-DD HH24:MI:SS') -1/24/3600)
   AND b.end_date >=
       (to_date(:END_TIME, 'YYYY-MM-DD HH24:MI:SS') -1/24/3600)
   AND b.product_id = c.discnt_code