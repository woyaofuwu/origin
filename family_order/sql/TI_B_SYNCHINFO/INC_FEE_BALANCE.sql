INSERT INTO TF_A_FEE_BALANCE(sys_code,log_id,sub_log_id,partition_id,trade_eparchy_code,trade_city_code,trade_depart_id,oper_staff_id,pay_money_code,sale_type_code,purchase_attr,trade_type_code,pay_mode_code,net_type_code,profit_cen_id,trade_date,check_number,fee_type_code,fee_item_type_code,channel_id,fee,present_fee,form_fee,score,dc_tag,cancel_tag,res_type,nums,acc_date,acc_tag,acc_no,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3)
VALUES(:SYS_CODE,to_number(:LOG_ID),to_number(:SUB_LOG_ID),:PARTITION_ID,:TRADE_EPARCHY_CODE,:TRADE_CITY_CODE,:TRADE_DEPART_ID,:OPER_STAFF_ID,:PAY_MONEY_CODE,:SALE_TYPE_CODE,:PURCHASE_ATTR,:TRADE_TYPE_CODE,:PAY_MODE_CODE,:NET_TYPE_CODE,:PROFIT_CEN_ID,to_date(:TRADE_DATE,'yyyy-mm-dd hh24:mi:ss'),:CHECK_NUMBER,:FEE_TYPE_CODE,:FEE_ITEM_TYPE_CODE,:CHANNEL_ID,:FEE,:PRESENT_FEE,:FORM_FEE,:SCORE,:DC_TAG,:CANCEL_TAG,:RES_TYPE,:NUMS,to_date(:ACC_DATE,'yyyy-mm-dd hh24:mi:ss'),:ACC_TAG,:ACC_NO,:REMARK,:RSRV_TAG1,:RSRV_TAG2,:RSRV_TAG3,to_date(:RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss'),to_date(:RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss'),to_date(:RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss'),:RSRV_STR1,:RSRV_STR2,:RSRV_STR3,:RSRV_STR4,:RSRV_STR5,:RSRV_STR6,:RSRV_STR7,:RSRV_NUM1,:RSRV_NUM2,:RSRV_NUM3)