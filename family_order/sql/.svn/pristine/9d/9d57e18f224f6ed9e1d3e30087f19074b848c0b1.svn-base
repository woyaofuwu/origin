--IS_CACHE=Y
SELECT bill_item_code,integrate_item_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,valid_tag,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,integrate_item 
  FROM td_sd_billitem_integ
 WHERE bill_item_code >=6001 AND bill_item_code <=6500
   AND valid_tag='1' order by integrate_item_code