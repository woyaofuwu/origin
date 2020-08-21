DELETE FROM TF_F_USER_MBMP_SUB a
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND (end_date>sysdate
   or exists (select 1 from  TF_B_TRADE_MBMP_SUB_BAK b where b.user_id=a.user_id and b.BIZ_TYPE_CODE=a.BIZ_TYPE_CODE and b.ORG_DOMAIN=a.ORG_DOMAIN and b.start_date=a.start_date and b.SP_ID=a.SP_ID and b.SP_SVC_ID=a.SP_SVC_ID and b.trade_id=TO_NUMBER(:TRADE_ID)))