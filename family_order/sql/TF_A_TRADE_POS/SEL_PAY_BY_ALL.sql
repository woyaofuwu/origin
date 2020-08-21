SELECT to_char(trade_id) trade_id,
       to_char(user_id) user_id,
       partition_id,
       to_char(cust_id) cust_id,
       to_char(acct_id) acct_id,
       trade_type_code,
       serial_number,
       usr_name,
       pay_name,
       brand_code,
       trade_attr,
       to_char(oper_date, 'yyyy-mm-dd hh24:mi:ss') oper_date,
       deal_tag,
       trade_staff_id,
       trade_depart_id,
       trade_eparchy_code, 
       trade_city_code,
       eparchy_code,
       to_char(oper_fee) oper_fee,
       city_code,
       rsrv_tag1,
       rsrv_info1,
       rsrv_info2,
       remark,
       to_char(rsrv_date1, 'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(rsrv_date2, 'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(rsrv_fee1) rsrv_fee1,
       to_char(rsrv_fee2) rsrv_fee2
  FROM tf_a_trade_pos a
  WHERE (:SERIAL_NUMBER is null or serial_number = :SERIAL_NUMBER) 
       and (:TRADE_STAFF_ID is null or trade_staff_id = :TRADE_STAFF_ID)
       and oper_date <= TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS') 
       and oper_date >= TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS')
       and not exists (select 1 from tf_a_paylog b where a.trade_id=b.charge_id 
       and b.cancel_tag='0')
       and eparchy_code=:TRADE_EPARCHY_CODE
       and trade_type_code='3'
       and (:TRADE_DEPART_ID is null or trade_depart_id=:TRADE_DEPART_ID)
       and (:TRADE_CITY_CODE is null or city_code=:TRADE_CITY_CODE)