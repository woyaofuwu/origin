--IS_CACHE=N
SELECT DEPART_KIND_CODE 部门类型, ACCEPT_DATE 受理月份,TRADE_ID 台账标识
         FROM (SELECT A.DEPART_KIND_CODE,TO_CHAR(B.ACCEPT_DATE, 'YYYYMM') ACCEPT_DATE,B.TRADE_ID
                 FROM TD_M_DEPART A, ucr_crm1.TF_BH_TRADE B, TD_M_STAFF C
                WHERE B.USER_ID = TO_NUMBER(:USER_ID)
                  AND B.TRADE_TYPE_CODE IN ('10') --开户
                  AND B.CANCEL_TAG = '0' --末返销
                  AND B.TRADE_EPARCHY_CODE = '0898' -- 海南
                  AND B.TRADE_STAFF_ID = C.STAFF_ID
                  AND C.DEPART_ID = A.DEPART_ID
                  AND TRUNC(B.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE) --20101008 qiand 统一修改判断条件。
                  AND ROWNUM < 2
               UNION
               SELECT A.DEPART_KIND_CODE,TO_CHAR(B.OPEN_DATE, 'YYYYMM') ACCEPT_DATE, -1
                 FROM TD_M_DEPART A, TF_F_USER B
                WHERE B.USER_ID = TO_NUMBER(:USER_ID)
                  AND B.DEVELOP_DEPART_ID = A.DEPART_ID
                  AND B.ACCT_TAG = '0'
                  AND TRUNC(B.OPEN_DATE) BETWEEN TRUNC(SYSDATE - 15) AND TRUNC(SYSDATE)
                  AND NOT EXISTS (SELECT 1 FROM TF_F_USER_NP N WHERE N.USER_ID=TO_NUMBER(:USER_ID) AND N.NP_TAG = '1')
                  AND ROWNUM < 2)
        WHERE ROWNUM < 2