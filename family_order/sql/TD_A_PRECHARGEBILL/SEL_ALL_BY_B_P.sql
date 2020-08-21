--IS_CACHE=Y
SELECT precharge_bill_code,brand_code,product_id,to_char(premoney) premoney,to_char(backmoney) backmoney,backtimes,to_char(presentmoney) presentmoney,to_char(dcreasescores) dcreasescores,prechargeinfo 
  FROM td_a_prechargebill
 WHERE brand_code=:BRAND_CODE
   AND product_id=:PRODUCT_ID