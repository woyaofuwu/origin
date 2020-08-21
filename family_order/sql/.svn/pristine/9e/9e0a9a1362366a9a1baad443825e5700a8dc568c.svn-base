UPDATE tf_f_user_mbmp a
   SET (a.biz_state_code,a.start_date,a.end_date,a.update_staff_id,a.update_depart_id,a.
update_time) = (select b.biz_state_code,b.start_date,b.end_date,b.trade_staff_id,b.trade_depart_id,b.
trade_time from tf_b_trade_mbmp b where b.trade_id = :TRADE_ID) 
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.biz_type_code=:BIZ_TYPE_CODE
   AND a.org_domain=:ORG_DOMAIN
   AND a.biz_state_code='A'
   AND a.end_date>sysdate