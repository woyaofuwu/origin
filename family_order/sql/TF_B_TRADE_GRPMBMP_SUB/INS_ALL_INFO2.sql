INSERT INTO tf_b_trade_grpmbmp_sub(trade_id,modify_tag,partition_id,user_id,serial_number,biz_code,biz_name,start_date,end_date,ec_user_id,ec_serial_number,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,update_depart_id,update_time)
SELECT TO_CHAR(:TRADE_ID),:MODIFY_TAG,MOD(TO_NUMBER(:TRADE_ID),10000),TO_NUMBER(:USER_ID),
 TO_NUMBER(:SERIAL_NUMBER),:BIZ_CODE,:BIZ_NAME,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),
 TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_NUMBER(:EC_USER_ID),TO_NUMBER(:EC_SERIAL_NUMBER),
 :RSRV_NUM1,:RSRV_NUM2,:RSRV_NUM3,TO_NUMBER(:RSRV_NUM4),TO_NUMBER(:RSRV_NUM5), :RSRV_STR1,:RSRV_STR2,
 :RSRV_STR3,:RSRV_STR4,:RSRV_STR5,decode(:MODIFY_TAG,'0',TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),
 TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS')), TO_DATE(:RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS'),
 TO_DATE(:RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS'),:REMARK,
 :UPDATE_STAFF_ID,:UPDATE_DEPART_ID,SYSDATE 
  FROM dual