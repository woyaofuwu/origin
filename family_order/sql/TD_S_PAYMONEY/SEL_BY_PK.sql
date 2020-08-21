--IS_CACHE=Y
SELECT pay_money_code,pay_money,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_s_paymoney
 WHERE pay_money_code=:PAY_MONEY_CODE