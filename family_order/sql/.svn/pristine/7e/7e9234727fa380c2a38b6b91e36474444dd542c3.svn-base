SELECT '0' AS TRADE_ID, 0 ACCEPT_MONTH,
        TO_CHAR(a.USER_ID) AS USER_ID, TO_CHAR(a.USER_ID_A) AS USER_ID_A, a.PRODUCT_ID AS PRODUCT_ID,
        a.PACKAGE_ID AS PACKAGE_ID, a.SERVICE_ID AS SERVICE_ID, a.MAIN_TAG AS MAIN_TAG,
        TO_CHAR(a.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE, to_char(a.inst_id) inst_id,
        TO_CHAR(a.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE, 'A' AS MODIFY_TAG
 FROM tf_f_user_svc a
  WHERE user_id = TO_NUMBER(:USER_ID)
    AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
    AND end_date>sysdate
    AND NOT EXISTS (SELECT  /*+ index(b PK_TF_B_TRADE_SVC) */1 FROM tf_b_trade_svc b,tf_b_trade c
                     WHERE c.user_id = TO_NUMBER(:USER_ID) AND c.cancel_tag='0'
                       AND c.accept_month = TO_NUMBER(:ACCEPT_MONTH)
                       AND b.trade_id = c.trade_id
                       AND b.accept_month = c.accept_month AND b.modify_tag in('1', '5')
                       AND b.service_id=a.service_id AND b.inst_id =a.inst_id
                       and b.product_id = a.product_id and b.package_id = a.package_id)
 union all
 SELECT  /*+ index(d PK_TF_B_TRADE_SVC) */
         TO_CHAR(d.TRADE_ID) AS TRADE_ID, d.ACCEPT_MONTH AS ACCEPT_MONTH,
        TO_CHAR(d.USER_ID) AS USER_ID, TO_CHAR(d.USER_ID_A) AS USER_ID_A, d.PRODUCT_ID AS PRODUCT_ID,
        d.PACKAGE_ID AS PACKAGE_ID, d.SERVICE_ID AS SERVICE_ID, d.MAIN_TAG AS MAIN_TAG,
        TO_CHAR(d.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE, to_char(d.inst_id) inst_id,
        TO_CHAR(d.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE, '0' AS MODIFY_TAG
 FROM tf_b_trade_svc d,tf_b_trade e
 WHERE e.user_id = TO_NUMBER(:USER_ID) AND e.cancel_tag='0'
    AND e.accept_month = TO_NUMBER(:ACCEPT_MONTH)
    AND d.trade_id = e.trade_id
    AND d.user_id = TO_NUMBER(:USER_ID)
    AND d.accept_month = TO_NUMBER(:ACCEPT_MONTH)
    AND d.modify_tag in('0','4')
    AND NOT EXISTS( SELECT /*+ index(s PK_TF_B_TRADE_SVC) */ 1 FROM tf_b_trade_svc s
                     WHERE s.trade_id = TO_NUMBER(:TRADE_ID)
                       AND s.accept_month = TO_NUMBER(:ACCEPT_MONTH) AND s.modify_tag in('1','5')
                       and s.product_id = d.product_id and s.package_id = d.package_id
                       AND s.service_id = d.service_id AND s.start_date = d.start_date)
 union all
 SELECT /*+ index(t PK_TF_B_TRADE_SVC) */
        TO_CHAR(t.TRADE_ID) AS TRADE_ID, t.ACCEPT_MONTH AS ACCEPT_MONTH,
        TO_CHAR(t.USER_ID) AS USER_ID, TO_CHAR(t.USER_ID_A) AS USER_ID_A, t.PRODUCT_ID AS PRODUCT_ID,
        t.PACKAGE_ID AS PACKAGE_ID, t.SERVICE_ID AS SERVICE_ID, t.MAIN_TAG AS MAIN_TAG,
        TO_CHAR(t.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE, to_char(t.inst_id) inst_id,
        TO_CHAR(t.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE, '1' AS MODIFY_TAG
 FROM   TF_B_TRADE_SVC t
 WHERE  modify_tag in('1', '5')
 and    trade_id = TO_NUMBER(:TRADE_ID)
 AND    accept_month = TO_NUMBER(:ACCEPT_MONTH)