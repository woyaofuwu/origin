UPDATE td_m_res_commpara
 SET   para_value1=decode(:CHECKTAG, 0,to_char(to_number(para_value1)+:PARA_VALUE9),1, to_char(to_number(para_value1)-:PARA_VALUE9)),
       update_time=SYSDATE,
       update_staff_id=:TRADE_STAFF_ID,
       update_depart_id=:TRADE_DEPART_ID           
 WHERE para_attr=:PARA_ATTR
  AND  eparchy_code=:EPARCHY_CODE
  AND  para_code1=:STORE_ID  --库存位置
  AND para_code2=:RES_KIND  --资源子种类