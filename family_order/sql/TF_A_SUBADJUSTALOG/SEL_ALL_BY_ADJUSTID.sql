SELECT to_char(adjust_id) adjust_id,detail_item_code,to_char(adjust_per) adjust_per,to_char(adjust_fee) adjust_fee,to_char(adjust_time,'yyyy-mm-dd hh24:mi:ss') adjust_time,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10 
  FROM tf_a_subadjustalog
 WHERE adjust_id=TO_NUMBER(:ADJUST_ID)