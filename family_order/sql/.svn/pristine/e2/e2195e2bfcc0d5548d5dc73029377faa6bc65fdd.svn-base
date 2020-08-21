SELECT partition_id,to_char(user_id) user_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_str11,rsrv_str12,rsrv_str13,rsrv_str14,rsrv_str15,rsrv_str16,rsrv_str17,rsrv_str18,rsrv_str19,rsrv_str20,rsrv_str21,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark
  FROM tf_f_user_other
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND sysdate BETWEEN start_date+0 AND end_date+0