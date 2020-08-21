--IS_CACHE=Y
SELECT package_id,discnt_code,package_name,DECODE(package_type,'01','选号开户优惠包','') package_type,discnt_name,eparchy_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,DECODE(limit_type,'0','可以选择的优惠列表','1','必须附加绑定的优惠','') limit_type,months,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,rsrv_num1/100 rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_b_discnt_package
 WHERE (:PACKAGE_ID IS NULL OR package_id=:PACKAGE_ID)
   AND (:DISCNT_CODE=-1 OR discnt_code=:DISCNT_CODE)
   AND (:PACKAGE_TYPE IS NULL OR package_type=:PACKAGE_TYPE)
   AND eparchy_code=:EPARCHY_CODE
   AND start_date<=SYSDATE
   AND end_date>=SYSDATE
 ORDER BY package_id,discnt_code