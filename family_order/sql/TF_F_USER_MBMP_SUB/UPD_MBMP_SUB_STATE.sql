UPDATE /*+ index(a PK_TF_F_USER_MBMP_SUB)*/ tf_f_user_mbmp_sub a

   SET end_date=sysdate,update_time=sysdate 

where a.user_id=(SELECT distinct user_id from tf_b_trade_mbmp_sub c where c.trade_id=to_number(:TRADE_ID))

  AND exists(select 1 from tf_b_trade_mbmp_sub c where c.trade_id=to_number(:TRADE_ID) and a.biz_type_code=c.biz_type_code and a.sp_id=c.sp_id and a.sp_svc_id=c.sp_svc_id)

  AND end_date>sysdate

  and start_date<(select min(end_date) from tf_b_trade_mbmp_sub d where d.trade_id=to_number(:TRADE_ID) and a.sp_id=d.sp_id and a.sp_svc_id=d.sp_svc_id)