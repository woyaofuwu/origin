INSERT INTO tf_a_subadjustblog(adjust_id,detail_item_code,adjust_per,adjust_fee,adjust_time,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10)
 VALUES(TO_NUMBER(:ADJUST_ID),:DETAIL_ITEM_CODE,TO_NUMBER(:ADJUST_PER),TO_NUMBER(:ADJUST_FEE),sysdate,:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,:RSRV_STR4,:RSRV_STR5,:RSRV_STR6,:RSRV_STR7,:RSRV_STR8,:RSRV_STR9,:RSRV_STR10)