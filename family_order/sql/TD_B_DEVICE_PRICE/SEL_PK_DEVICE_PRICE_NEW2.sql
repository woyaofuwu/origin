SELECT res_type_code,res_kind_code,card_kind_code,capacity_type_code,trade_type_code,product_id,class_id,feeitem_code,to_char(device_price) device_price,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_b_device_price
 WHERE res_type_code = :RES_TYPE_CODE
   AND (res_kind_code = :RES_KIND_CODE OR res_kind_code = '%')
   AND (capacity_type_code = :CAPACITY_TYPE_CODE OR capacity_type_code = '%')
   AND (trade_type_code = NVL(:TRADE_TYPE_CODE,-1) OR trade_type_code = -1)
   AND (product_id = NVL(:PRODUCT_ID,-1) OR product_id = -1)
   AND SYSDATE BETWEEN start_date AND end_date
   AND eparchy_code = :EPARCHY_CODE
order by trade_type_code desc,product_id desc
