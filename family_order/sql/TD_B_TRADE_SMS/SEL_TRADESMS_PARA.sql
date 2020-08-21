--IS_CACHE=Y
SELECT a.trade_type_code,a.brand_code,a.product_id,a.cancel_tag,a.item_index,
  a.order_no,a.obj_type_code,a.obj_code,a.modify_tag,
  b.template_content1||b.template_content2||b.template_content3||b.template_content4||b.template_content5
   notice_content,
  a.send_delay,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
  a.eparchy_code,a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_b_trade_sms a,td_b_template b
 WHERE a.trade_type_code = :TRADE_TYPE_CODE
   AND (a.brand_code = :BRAND_CODE OR a.brand_code = 'ZZZZ')
   AND (a.product_id = :PRODUCT_ID OR a.product_id = -1)
   AND a.cancel_tag = :CANCEL_TAG
   AND (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
   and a.template_id=b.template_id
   AND SYSDATE BETWEEN a.start_date AND a.end_date
 ORDER BY a.order_no,a.item_index