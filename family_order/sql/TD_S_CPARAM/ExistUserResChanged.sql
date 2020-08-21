SELECT count(1) recordcount
  FROM tf_b_trade_res a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   And res_type_code= :RES_TYPE_CODE
   AND modify_tag = :MODIFY_TAG
   AND Exists(Select 1 From tf_f_user_res b
              Where b.user_id= :USER_ID
              And b.res_type_code= :RES_TYPE_CODE
              And b.end_date>Sysdate
              And b.res_code=a.res_code)