--IS_CACHE=Y
SELECT feeitem_code,feeitem_name,pre_tag,to_char(premoney) premoney,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_feeitem
 WHERE feeitem_code=:FEEITEM_CODE
   AND sysdate BETWEEN start_date AND end_date
   AND eparchy_code=:EPARCHY_CODE