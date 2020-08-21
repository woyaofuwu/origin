SELECT COUNT(1)  recordcount
  FROM tf_f_cust_vipsimbak a,tf_f_cust_vip b
 WHERE a.vip_id=b.vip_id and b.user_id=TO_NUMBER(:USER_ID)
   AND (a.act_tag=:ACT_TAG OR :ACT_TAG IS NULL)
   AND (a.client_info1=:CLIENT_INFO1 OR :CLIENT_INFO1 IS NULL)