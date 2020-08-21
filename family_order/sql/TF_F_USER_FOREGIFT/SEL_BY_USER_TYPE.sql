SELECT to_char(user_id) user_id,foregift_code,to_char(money) money,cust_name,pspt_id,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM tf_f_user_foregift
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND foregift_code=:FOREGIFT_CODE