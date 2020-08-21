INSERT INTO tf_b_trade_mbmp_sub(trade_id,user_id,sp_id,biz_type_code,org_domain,opr_source,sp_svc_id,
start_date,end_date,biz_state_code,modify_tag,billflg,rsrv_str2)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(:USER_ID),:SP_ID,:BIZ_TYPE_CODE,:ORG_DOMAIN,:OPR_SOURCE,
 :SP_SVC_ID,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),
 :BIZ_STATE_CODE,:MODIFY_TAG,billing_type,:RSRV_STR2
  FROM td_m_spservice
 WHERE sp_id = :SP_ID
   AND sp_svc_id = :SP_SVC_ID