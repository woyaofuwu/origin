INSERT INTO TF_F_USER_VALIDCHANGE(partition_id,user_id,start_date,end_date,update_time,update_staff_id,update_depart_id,
     remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,
     rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3)
SELECT MOD(user_id,10000),user_id,start_date,end_date,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,
    rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,
    rsrv_tag2,rsrv_tag3
FROM tf_b_trade_validchange_bak
WHERE trade_id = to_number(:TRADE_ID)
  AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))