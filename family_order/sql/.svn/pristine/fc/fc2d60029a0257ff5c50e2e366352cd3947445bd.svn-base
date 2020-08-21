SELECT to_char(user_id) user_id,
       foregift_code,
       to_char(money) money,
       cust_name,
       pspt_id,
       to_char(FOREGIFT_IN_DATE, 'yyyy-mm-dd hh24:mi:ss') FOREGIFT_IN_DATE,
       to_char(FOREGIFT_OUT_DATE, 'yyyy-mm-dd hh24:mi:ss') FOREGIFT_OUT_DATE,
       to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       remark,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5
  FROM tf_f_user_foregift
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)