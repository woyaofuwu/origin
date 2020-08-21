SELECT partition_id,to_char(user_id) user_id,serial_number,biz_type_code,org_domain,opr_source,passwd,decode(a.biz_state_code,'A','正常使用','N','暂停使用','S','内部测试','T','测试待审','R','试商用','E','终止') biz_state_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(rsrv_num1) rsrv_num1,to_char(rsrv_num2) rsrv_num2,to_char(rsrv_num3) rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
FROM tf_f_user_mbmp a
WHERE user_id=TO_NUMBER(:USER_ID)
AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND a.biz_state_code='A'
AND SYSDATE BETWEEN a.start_date AND a.end_date