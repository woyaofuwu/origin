SELECT partition_id,
       to_char(user_id) user_id,
       to_char(month_fee) month_fee,
       to_char(month_gprs) month_gprs,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       rsrv_str6,
       to_char(in_time, 'yyyy-mm-dd hh24:mi:ss') in_time,
       remark
  FROM TF_F_SALEACTIVE_SBXS
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)