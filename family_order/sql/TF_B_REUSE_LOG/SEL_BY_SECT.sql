select rsrv_str1, to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,count(serial_number) serial_number  
from tf_b_reuse_log a
 WHERE serial_number>=:RES_NO_S
   and serial_number<=:RES_NO_E
   and (:OPER_TAG is NULL or rsrv_tag1=:OPER_TAG)
   AND (:STAFF_ID is null or rsrv_str1=:STAFF_ID)
   AND (:PARA_VALUE4 IS NULL OR TO_DATE(:PARA_VALUE4,'yyyy-mm-dd')+1>=rsrv_date1)
   AND (:PARA_VALUE3 IS NULL OR TO_DATE(:PARA_VALUE3,'yyyy-mm-dd')<=rsrv_date1)
   AND NOT EXISTS (SELECT 1 FROM tf_r_mphonecode_idle b WHERE b.serial_number=a.serial_number and b.precode_tag='1') 
group by rsrv_str1,rsrv_date1