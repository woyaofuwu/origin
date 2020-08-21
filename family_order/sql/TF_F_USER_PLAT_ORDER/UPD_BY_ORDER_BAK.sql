UPDATE TF_F_USER_PLAT_ORDER a
   SET a.END_DATE = TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS') - 1 / 24 / 3600
WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND (a.sp_code ,a.biz_code) IN (
   SELECT b.sp_code,b.biz_code 
     FROM TF_B_TRADE_PLAT_ORDER b
     WHERE b.partition_id=MOD(TO_NUMBER(:TRADE_ID),10000)
       AND b.trade_id = :TRADE_ID   
   )
   AND EXISTS(SELECT 1 FROM td_s_commpara 
              WHERE param_attr = :PARAM_ATTR 
              AND param_code = to_char(a.BIZ_TYPE_CODE) 
              AND sysdate BETWEEN start_date AND end_date)