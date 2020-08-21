update tf_b_reuse_log  a  
set rsrv_tag1='1',
    rsrv_date1=sysdate,
    rsrv_str1=:STAFF_ID
where serial_number>=:RES_NO_S
  and serial_number<=:RES_NO_E
  AND NOT EXISTS (SELECT 1 FROM tf_r_mphonecode_idle b WHERE b.serial_number=a.serial_number and b.precode_tag='1')