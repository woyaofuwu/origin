SELECT '' TRADE_ID,'' OPER_DATE_STR,NULL OPER_TIME,'' RES_TYPE_CODE,'' OPER_FLAG,'' STAT_TYPE,'' OPER_STAFF_ID,'' CARD_TYPE_CODE,'' EPARCHY_CODE,CITY_CODE,'' DEPART_ID,
'' STAFF_ID,'' RSRV_TAG1,'' RSRV_TAG2,'' RSRV_TAG3,'' PARA_VALUE1,'' PARA_VALUE2,'' PARA_VALUE3, STAFF_ID PARA_VALUE4,PARA_VALUE5,PARA_VALUE6,PARA_VALUE7,'' PARA_VALUE8,
       TO_CHAR(PARA_VALUE9) PARA_VALUE9,'' PARA_VALUE10,'' PARA_VALUE11,'' PARA_VALUE12,'' PARA_VALUE13,'' PARA_VALUE14,'' PARA_VALUE15,'' PARA_VALUE16,'' PARA_VALUE17,
       '' PARA_VALUE18, NULL RDVALUE1, NULL RDVALUE2,'' REMARK2
  FROM (SELECT A.PARA_VALUE6 PARA_VALUE6,
               B.KIND_NAME PARA_VALUE5,
               A.CITY_CODE CITY_CODE,
               DECODE(A.OPER_FLAG,
                      'S',
                      F_RES_GETCODENAME('STAFF_ID', A.STAFF_ID, '', '') || '[' ||
                      A.STAFF_ID || ']',
                      '0',
                      F_RES_GETCODENAME('DEPART_NAME_CODE',
                                        A.DEPART_ID,
                                        '',
                                        '')) STAFF_ID,
               A.PARA_VALUE7 PARA_VALUE7,
               SUM(A.PARA_VALUE9) PARA_VALUE9
          FROM TF_B_RESDAYSTAT_LOG A, TD_S_RESKIND B
         WHERE A.OPER_TIME >=
               TO_DATE((TO_CHAR(TO_DATE(:OPER_TIME, 'yyyy-mm-dd'), 'yyyy-mm') ||
                       '-01'),
                       'yyyy-mm-dd')
           AND A.OPER_TIME <= LAST_DAY(TO_DATE(:OPER_TIME, 'yyyy-mm-dd')) + 1
           AND A.RES_TYPE_CODE = :RES_TYPE_CODE
           AND A.STAT_TYPE = '0'
           AND A.EPARCHY_CODE = :EPARCHY_CODE
           AND A.EPARCHY_CODE = B.EPARCHY_CODE
           AND B.RES_TYPE_CODE =
               DECODE(:RES_TYPE_CODE, 'U', '1', 'V', '3', 'W', '5', '')
           AND A.CARD_TYPE_CODE = B.RES_KIND_CODE
           AND A.CITY_CODE LIKE
               DECODE(:TRADE_CITY_CODE,
                      'HNSJ',
                      DECODE(:CITY_CODE, NULL, '%', :CITY_CODE),
                      :TRADE_CITY_CODE)
           AND ((:STOCK_LEVEL = '0' AND
               A.STAFF_ID NOT IN ('SUPERUSR', 'HNSJ0041'))
               OR
               (:STOCK_LEVEL = '2' AND A.OPER_FLAG = '0' AND EXISTS
                (SELECT 1
                    FROM TD_S_ASSIGNRULE C
                   WHERE C.EPARCHY_CODE = A.EPARCHY_CODE
                     AND C.RES_TYPE_CODE = DECODE(:RES_TYPE_CODE,'U','1','V','3','W','5','')
                     AND C.DEPART_CODE >= :STAFF_ID_S
                     AND C.DEPART_CODE <= :STAFF_ID_E
                     AND C.VALID_FLAG = '0'
                     AND C.AREA_CODE = A.CITY_CODE
                     AND C.START_DATE <= SYSDATE
                     AND (C.END_DATE >= SYSDATE OR C.END_DATE IS NULL)
                     AND C.DEPART_ID = A.DEPART_ID
                     AND EXISTS
                   (SELECT 1
                            FROM TD_M_DEPARTKIND D
                           WHERE D.CODE_TYPE_CODE = '0'
                             AND D.EPARCHY_CODE = C.EPARCHY_CODE
                             AND D.DEPART_KIND_CODE = C.DEPART_KIND_CODE)))
               OR
               (:STOCK_LEVEL = '1' AND A.OPER_FLAG = 'S' AND
               A.STAFF_ID >= :STAFF_ID_S AND A.STAFF_ID <= :STAFF_ID_E))
         GROUP BY A.PARA_VALUE6,B.KIND_NAME,A.CITY_CODE,
                  DECODE(A.OPER_FLAG,
                         'S',
                         F_RES_GETCODENAME('STAFF_ID', A.STAFF_ID, '', '') || '[' ||
                         A.STAFF_ID || ']',
                         '0',
                         F_RES_GETCODENAME('DEPART_NAME_CODE',
                                           A.DEPART_ID,
                                           '',
                                           '')), A.PARA_VALUE7)