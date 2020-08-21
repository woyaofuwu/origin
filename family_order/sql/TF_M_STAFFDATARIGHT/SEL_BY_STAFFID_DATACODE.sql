--IS_CACHE=Y
SELECT staff_id,data_code,data_type,right_attr,right_class,oper_special,right_tag,rsvalue1,rsvalue2,remark,to_char(accredit_time,'yyyy-mm-dd hh24:mi:ss') accredit_time,accredit_staff_id 
  FROM tf_m_staffdataright
 WHERE staff_id=:STAFF_ID
   AND data_code=:DATA_CODE
   AND data_type=:DATA_TYPE
   AND right_tag='1'