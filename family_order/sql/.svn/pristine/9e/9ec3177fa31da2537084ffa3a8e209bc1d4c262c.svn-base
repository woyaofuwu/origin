--IS_CACHE=Y
select A.RULE_ID,
       A.RULE_NAME,
       A.GIFT_TYPE_CODE,
       A.SCORE,
       A.SCORE_TYPE_CODE,
       A.EXCHANGE_TYPE_CODE,
       A.EXCHANGE_MODE,
       A.SCORE_NUM,
       A.UNIT,
       A.EXCHANGE_LIMIT,
       A.REWARD_LIMIT,
       A.MONEY_RATE,
       A.FMONTHS,
       A.DEPOSIT_CODE,
       A.AMONTHS,
       A.CLASS_LIMIT,
       A.EPARCHY_CODE,
       A.BRAND_CODE,
       A.RIGHT_CODE,
       A.START_DATE,
       A.END_DATE,
       A.STATUS,
       A.REMARK,
       A.UPDATE_TIME,
       A.UPDATE_DEPART_ID,
       A.UPDATE_STAFF_ID
  from td_b_exchange_rule a, td_b_score_exchange_type b
 where a.exchange_type_code = b.exchange_type_code
   and a.eparchy_code = b.eparchy_code
   and a.status = '0'
   and a.exchange_type_code = :EXCHANGE_TYPE_CODE  
   and (a.brand_code = :BRAND_CODE or :BRAND_CODE is null)
   and (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
   and a.end_date+0 >= sysdate