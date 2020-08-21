INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),TO_NUMBER(:USER_ID),'1',TO_NUMBER(para_code11),'0',
       TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),
       ADD_MONTHS(TRUNC(TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),'mm'),NVL(TO_NUMBER(para_code12),200))-1/24/3600
  FROM td_s_commpara
 WHERE subsys_code = 'BRN' AND param_attr = 272
   AND (eparchy_code = 'ZZZZ' OR eparchy_code = :EPARCHY_CODE)
   AND param_code = :PARAM_CODE
   AND para_code11 IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM tf_b_trade_discnt
                    WHERE trade_id = TO_NUMBER(:TRADE_ID)
                      AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                      AND discnt_code = TO_NUMBER(para_code11)
                      AND modify_tag = '0')