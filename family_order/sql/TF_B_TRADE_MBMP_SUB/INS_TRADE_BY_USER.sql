INSERT INTO tf_b_trade_mbmp_sub(trade_id,user_id,sp_id,biz_type_code,org_domain,opr_source,sp_svc_id,start_date,end_date,biz_state_code,
billflg,modify_tag)
SELECT :TRADE_ID,user_id,sp_id,biz_type_code,org_domain,opr_source,sp_svc_id,sysdate,to_date('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),'E',
       billflg,'E'
  FROM tf_f_user_mbmp_sub
 WHERE partition_id = MOD(:USER_ID,10000)
   AND user_id = :USER_ID
   AND (biz_type_code = :BIZ_TYPE_CODE OR :BIZ_TYPE_CODE = '*')
   AND (sp_svc_id = :SP_SVC_ID OR :SP_SVC_ID = '*')
   AND SYSDATE BETWEEN start_date AND end_date
   AND biz_state_code in ('A','N')