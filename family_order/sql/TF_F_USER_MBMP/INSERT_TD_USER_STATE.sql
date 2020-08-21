INSERT /*+append*/ INTO tf_f_user_mbmp(partition_id,user_id,serial_number,biz_type_code,org_domain,opr_source,passwd,biz_state_code,start_date,end_date,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,update_depart_id,update_time)
SELECT b.partition_id,b.user_id,b.serial_number,b.biz_type_code,b.org_domain,b.opr_source,b.passwd,:BIZ_STATE_CODE,sysdate,TO_DATE('2050-12-31 23:59:59','YYYY-MM-DD HH24:MI:SS'),b.rsrv_num1,b.rsrv_num2,b.rsrv_num3,b.rsrv_num4,b.rsrv_num5,b.rsrv_str1,b.rsrv_str2,b.rsrv_str3,b.rsrv_str4,b.rsrv_str5,b.rsrv_date1,b.rsrv_date2,b.rsrv_date3,b.remark,b.update_staff_id,b.update_depart_id,b.update_time
 from tf_f_user_mbmp b
WHERE b.biz_state_code=:BIZ_STATE_CODE1
  and b.biz_type_code=:BIZ_TYPE_CODE
  AND sysdate between b.start_date and b.end_date+0
  AND b.user_id=:USER_ID 
  AND b.PARTITION_ID=MOD(:USER_ID,10000)