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
  FROM tf_a_trade_pos
  WHERE user_id = TO_NUMBER(:USER_ID)
       and partition_id >= :PARTITION_ID1 and partition_id <= :PARTITION_ID2 AND
       oper_date <= TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS') AND
       oper_date >=
       decode(:X_TAG,
              1,
              TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS'),
              0,
              add_months(TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS'),
                         :LIMIT_TIME))