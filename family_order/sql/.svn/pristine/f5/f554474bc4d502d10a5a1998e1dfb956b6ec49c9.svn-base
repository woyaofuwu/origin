INSERT INTO tf_f_user_plat_order(partition_id,user_id,serial_number,biz_code,sp_code,product_no,biz_type_code,
            org_domain,opr_source,biz_state_code,start_date,end_date,first_date,first_date_mon,gift_serial_number,
            gift_user_id,bill_type,price,subscribe_id,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,
            rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_date1,
            rsrv_date2,rsrv_date3,rsrv_date4,rsrv_date5,remark,update_staff_id,update_depart_id,update_time)
SELECT PARTITION_ID,USER_ID,SERIAL_NUMBER,BIZ_CODE,SP_CODE,PRODUCT_NO,BIZ_TYPE_CODE,ORG_DOMAIN,OPR_SOURCE,
       :BIZ_STATE_CODE,to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),FIRST_DATE,FIRST_DATE_MON,GIFT_SERIAL_NUMBER,GIFT_USER_ID,
       BILL_TYPE,PRICE,:TRADE_ID,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,
       RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_DATE1,RSRV_DATE2,
       RSRV_DATE3,RSRV_DATE4,RSRV_DATE5,REMARK,UPDATE_STAFF_ID,UPDATE_DEPART_ID,UPDATE_TIME
  FROM tf_f_user_plat_order
 WHERE partition_id = MOD(to_number(:USER_ID), 10000)
   AND user_id = to_number(:USER_ID)
   AND biz_state_code = :OLD_STATE_CODE
   AND start_date >= to_date(:OLD_START_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND end_date > SYSDATE