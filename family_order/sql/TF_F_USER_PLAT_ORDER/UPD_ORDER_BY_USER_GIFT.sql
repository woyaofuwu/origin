UPDATE TF_F_USER_PLAT_ORDER a
   SET a.END_DATE = TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
WHERE GIFT_USER_ID= :USER_ID
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND a.BIZ_STATE_CODE <> :BIZ_STATE_CODE
   AND EXISTS(SELECT 1 FROM td_s_commpara 
              WHERE param_attr = :PARAM_ATTR 
              AND param_code = to_char(a.BIZ_TYPE_CODE) 
              AND sysdate BETWEEN start_date AND end_date)