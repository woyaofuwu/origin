SELECT ACCEPT_DATE 受理月份,SHARE_ID 台账标识,DEPART_KIND_CODE 部门类型
 FROM (SELECT D.DEPART_KIND_CODE,
                        TO_CHAR(B.START_DATE, 'YYYYMM') ACCEPT_DATE,
                        B.UPDATE_DEPART_ID,
                        B.SHARE_ID
                   FROM TF_F_USER_SHARE      A,
                        TF_F_USER_SHARE_RELA B,
                        TF_F_USER_PRODUCT    C,
                        TD_M_DEPART          D
                  WHERE A.SHARE_ID = B.SHARE_ID
                    AND A.USER_ID = C.USER_ID
                    AND B.UPDATE_DEPART_ID = D.DEPART_ID
                    AND B.ROLE_CODE = '02'
                    AND B.USER_ID_B = TO_NUMBER(:USER_ID) --副卡用户user_id
                    AND A.DISCNT_CODE IN (80003008,80003011,80010466,80010468)
                    AND C.PRODUCT_ID IN (80003010,80003014,80010472,80010473)
                    AND A.END_DATE > SYSDATE
                    AND B.END_DATE > SYSDATE
                    AND C.END_DATE > SYSDATE
                    AND (TO_CHAR(B.START_DATE, 'yyyymm') = TO_CHAR(SYSDATE, 'yyyymm')
                        --补登记：上线时间+10天
                        OR (TO_DATE('2018-07-01', 'yyyy-mm-dd') > SYSDATE AND B.START_DATE > TO_DATE('2017-08-01', 'yyyy-mm-dd'))
                        )
                    AND SYSDATE BETWEEN TO_DATE('2017-08-01', 'yyyy-mm-dd') AND TO_DATE('2018-06-30 23:59:59', 'yyyy-mm-dd hh24:mi:ss')) TRADE
          WHERE ROWNUM < 2