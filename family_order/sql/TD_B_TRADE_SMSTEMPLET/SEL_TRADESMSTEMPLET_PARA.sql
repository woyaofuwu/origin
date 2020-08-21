--IS_CACHE=Y
SELECT trade_type_code,brand_code,product_id,cancel_tag,item_index,order_no,obj_type_code,obj_code,sms_type,notice_content,send_delay,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM TD_B_TRADE_SMSTEMPLET
 WHERE trade_type_code = :TRADE_TYPE_CODE
   AND (brand_code = :BRAND_CODE OR brand_code = 'ZZZZ')
   AND (product_id = :PRODUCT_ID OR product_id = -1)
   AND cancel_tag = :CANCEL_TAG
   AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
   AND SYSDATE BETWEEN start_date AND end_date
 ORDER BY order_no,item_index