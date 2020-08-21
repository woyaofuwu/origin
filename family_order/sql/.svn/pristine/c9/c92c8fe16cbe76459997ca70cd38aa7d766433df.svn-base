INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTRB(:TRADE_ID,5,2)),TO_NUMBER(:USER_ID),'1',discnt_code,'0',TRUNC(TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')),TO_DATE('20501231','yyyymmdd')
  FROM td_b_product_discnt
 WHERE product_id = :PRODUCT_ID
   AND discnt_code = :DISCNT_CODE
   AND SYSDATE BETWEEN start_date AND end_date
   AND NOT EXISTS(SELECT 1 FROM tf_f_user_discnt
                   WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                     AND user_id = TO_NUMBER(:USER_ID)
                     AND discnt_code+0 = :DISCNT_CODE
                     AND SYSDATE < end_date
                     AND end_date > ADD_MONTHS(TRUNC(SYSDATE,'MM'),1)) --去除到月底结束优惠的可能
   AND NOT EXISTS(SELECT 1 FROM tf_f_user_discnt a,td_b_discnt b
                   WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                     AND user_id = TO_NUMBER(:USER_ID)
                     AND a.discnt_code = b.discnt_code
                     AND SYSDATE < a.end_date
                     AND a.end_date > ADD_MONTHS(TRUNC(SYSDATE,'MM'),1) --去除到月底结束优惠的可能
                     AND SYSDATE BETWEEN b.start_date AND b.end_date
                     AND b.discnt_type_code = :DISCNT_TYPE_CODE)
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_discnt
                   WHERE trade_id = TO_NUMBER(:TRADE_ID)
                     AND accept_month = TO_NUMBER(SUBSTRB(:TRADE_ID,5,2))
                     AND id = TO_NUMBER(:USER_ID)
                     AND id_type = '1'
                     AND discnt_code = :DISCNT_CODE
                     AND modify_tag = '0'
                     AND SYSDATE < end_date)