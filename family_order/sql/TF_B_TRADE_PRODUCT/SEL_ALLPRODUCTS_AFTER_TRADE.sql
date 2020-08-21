SELECT '0' TRADE_ID, 0 ACCEPT_MONTH,
       TO_CHAR(a.USER_ID) AS USER_ID, TO_CHAR(a.USER_ID_A) AS USER_ID_A, a.PRODUCT_ID AS PRODUCT_ID,
       a.PRODUCT_MODE AS PRODUCT_MODE, a.BRAND_CODE AS BRAND_CODE,
       0 OLD_PRODUCT_ID, '0000' OLD_BRAND_CODE,
       TO_CHAR(a.INST_ID) AS INST_ID, TO_CHAR(a.CAMPN_ID) AS CAMPN_ID,
       TO_CHAR(a.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       TO_CHAR(a.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE, 'A' MODIFY_TAG
FROM tf_f_user_product a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date>sysdate
   AND NOT EXISTS (SELECT /*+ leading(c) */1 FROM tf_b_trade_product b,tf_b_trade c
                    WHERE c.user_id = TO_NUMBER(:USER_ID) AND c.cancel_tag='0'
                      AND c.accept_month = TO_NUMBER(:ACCEPT_MONTH)
                      AND b.trade_id = c.trade_id
                      AND b.accept_month = c.accept_month AND b.modify_tag='1'
                      AND b.product_id=a.product_id AND b.start_date=a.start_date
                      AND b.end_date<=c.accept_date)
union all
SELECT /*+ leading(e) */TO_CHAR(d.TRADE_ID) AS TRADE_ID, d.ACCEPT_MONTH AS ACCEPT_MONTH,
       TO_CHAR(d.USER_ID) AS USER_ID, TO_CHAR(d.USER_ID_A) AS USER_ID_A, d.PRODUCT_ID AS PRODUCT_ID,
       d.PRODUCT_MODE AS PRODUCT_MODE, d.BRAND_CODE AS BRAND_CODE,
       d.OLD_PRODUCT_ID AS OLD_PRODUCT_ID, d.OLD_BRAND_CODE AS OLD_BRAND_CODE,
       TO_CHAR(d.INST_ID) AS INST_ID, TO_CHAR(d.CAMPN_ID) AS CAMPN_ID,
       TO_CHAR(d.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       TO_CHAR(d.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE, d.MODIFY_TAG AS MODIFY_TAG
FROM   tf_b_trade_product d, tf_b_trade e
WHERE e.user_id = TO_NUMBER(:USER_ID) AND e.cancel_tag='0'
   AND e.accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND d.trade_id = e.trade_id
   AND d.user_id = TO_NUMBER(:USER_ID)
   AND d.accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND d.modify_tag='0'
   AND NOT EXISTS( SELECT 1 FROM tf_b_trade_product p
                    WHERE p.trade_id = TO_NUMBER(:TRADE_ID)
                      AND p.accept_month = TO_NUMBER(:ACCEPT_MONTH) AND p.modify_tag='1'
                      AND p.product_id = d.product_id AND start_date = d.start_date)
union all
SELECT TO_CHAR(TRADE_ID) AS TRADE_ID, ACCEPT_MONTH AS ACCEPT_MONTH,
       TO_CHAR(USER_ID) AS USER_ID, TO_CHAR(USER_ID_A) AS USER_ID_A, PRODUCT_ID AS PRODUCT_ID,
       PRODUCT_MODE AS PRODUCT_MODE, BRAND_CODE AS BRAND_CODE,
       OLD_PRODUCT_ID AS OLD_PRODUCT_ID, OLD_BRAND_CODE AS OLD_BRAND_CODE,
       TO_CHAR(INST_ID) AS INST_ID, TO_CHAR(CAMPN_ID) AS CAMPN_ID,
       TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE, MODIFY_TAG AS MODIFY_TAG
FROM   tf_b_trade_product
WHERE  trade_id = TO_NUMBER(:TRADE_ID)
AND    accept_month = TO_NUMBER(:ACCEPT_MONTH)
AND    modify_tag = '1'