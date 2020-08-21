SELECT  a.user_id
  FROM tf_a_payrelation a inner join tf_f_user u on a.user_id=u.user_id and u.remove_tag='0'
 WHERE a.acct_id = TO_NUMBER(:ACCT_ID)
   AND a.act_tag = :ACT_TAG
   AND TO_CHAR(SYSDATE, 'YYYYMMDD') between a.start_cycle_id and
       a.end_cycle_id
   AND a.default_tag='1'
   AND a.user_id<>:USER_ID