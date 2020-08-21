SELECT * FROM (
SELECT a.trade_id deal_id,
                '0' deal_tag,
                '1' deal_state,
                a.trade_eparchy_code deal_eparchy_code
           FROM ti_b_ec a
          WHERE a.modify_tag ='0'
          AND busi_sign NOT IN ('BBOSS_BOSS_ECInfo_1_0','MAS','OLDADC')
          AND status='9' AND (A.TRADE_ID, A.RSRV_STR1) IN
(SELECT MIN(B.TRADE_ID), B.RSRV_STR1
FROM TI_B_EC B
WHERE B.MODIFY_TAG = '0'
AND B.BUSI_SIGN NOT IN
('BBOSS_BOSS_ECInfo_1_0', 'MAS', 'OLDADC')
AND B.STATUS = '9'
GROUP BY B.RSRV_STR1) $CONDITIONSQL)
WHERE ROWNUM <= :ROW_COUNT