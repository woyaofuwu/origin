--IS_CACHE=Y
SELECT trade_type_code,product_id,class_id,feeitem_code,to_char(item_fee) item_fee,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_operfee
 WHERE trade_type_code=:TRADE_TYPE_CODE
   AND (product_id=:PRODUCT_ID OR product_id=-1)
   AND (class_id=:CLASS_ID OR class_id='Z')
   AND sysdate BETWEEN start_date AND end_date
   AND eparchy_code=:EPARCHY_CODE