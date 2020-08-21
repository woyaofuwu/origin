--IS_CACHE=Y
SELECT T.RULE_ID     RULE_ID, 
         T.SEC_CONFIRM SEC_CONFIRM ,
         T.SP_CONFIRM SP_CONFIRM
          FROM TD_B_PLATSVC_OPERRULE T 
         WHERE (T.ORG_DOMAIN = :ORG_DOMAIN OR T.ORG_DOMAIN = '-1')
           AND (T.SERVICE_ID = :SERVICE_ID OR SERVICE_ID = '-1')
           AND (T.OPER_CODE = :OPER_CODE OR T.OPER_CODE = '-1')
           AND (T.BIZ_TYPE_CODE = :BIZ_TYPE_CODE OR T.BIZ_TYPE_CODE = '-1')
           AND (T.IN_MODE_CODE = :IN_MODE_CODE OR T.IN_MODE_CODE = 'Q')
           AND (T.RSRV_TAG1 = :RSRV_TAG1 OR T.RSRV_TAG1 IS NULL)
           AND (T.RSRV_STR1 = :RSRV_STR1 OR T.RSRV_STR1 IS NULL)
           AND (T.RSRV_STR2 = :RSRV_STR2 OR T.RSRV_STR2 IS NULL)  -- 操作来源
           AND (T.RSRV_STR3 = :RSRV_STR3 OR T.RSRV_STR3 IS NULL)   --1 按条计费  2 包月计费
           AND (T.RSRV_STR4 = :RSRV_STR4 OR T.RSRV_STR4 IS NULL)   --0 梦网 1 自有
           AND (T.RSRV_STR5 = :RSRV_STR5 OR T.RSRV_STR5 IS NULL)   --0 立即 1 72小时免费
            AND (T.RSRV_STR6 = :RSRV_STR6 OR T.RSRV_STR6 IS NULL)
           AND ((T.ISFIRST_DATE = '1' AND T.ISFIRST_DATE = :ISFIRST_DATE) OR
               T.ISFIRST_DATE = '0')
           AND T.BIZ_PRI <> -1
           AND (T.ISFIRST_DATE_MON = :ISFIRST_DATE_MON) 
           AND T.BIZ_PRI =
               (SELECT MAX(BIZ_PRI)
                  FROM TD_B_PLATSVC_OPERRULE B
                 WHERE (B.SERVICE_ID = :SERVICE_ID OR B.SERVICE_ID = '-1')
                   AND (B.ORG_DOMAIN = :ORG_DOMAIN OR B.ORG_DOMAIN = '-1')
                   AND (B.OPER_CODE = :OPER_CODE OR B.OPER_CODE = '-1')
                   AND (B.BIZ_TYPE_CODE = :BIZ_TYPE_CODE OR B.BIZ_TYPE_CODE = '-1')
                   AND (B.IN_MODE_CODE = :IN_MODE_CODE OR B.IN_MODE_CODE = 'Q')
                   AND (B.RSRV_TAG1 = :RSRV_TAG1 OR B.RSRV_TAG1 IS NULL)
                   AND (B.RSRV_STR1 = :RSRV_STR1 OR B.RSRV_STR1 IS NULL)
                   AND (B.RSRV_STR2 = :RSRV_STR2 OR B.RSRV_STR2 IS NULL)  -- 操作来源
                   AND (B.RSRV_STR3 = :RSRV_STR3 OR B.RSRV_STR3 IS NULL)   --1 按条计费  2 包月计费
                   AND (B.RSRV_STR4 = :RSRV_STR4 OR B.RSRV_STR4 IS NULL)   --0 梦网 1 自有
                   AND (B.RSRV_STR5 = :RSRV_STR5 OR B.RSRV_STR5 IS NULL)   --0 立即 1 72小时免费
                   AND (B.RSRV_STR6 = :RSRV_STR6 OR B.RSRV_STR6 IS NULL) 
                   AND ((B.ISFIRST_DATE = '1' AND B.ISFIRST_DATE = :ISFIRST_DATE) OR
                       B.ISFIRST_DATE = '0')
                   AND B.BIZ_PRI <> -1
                   AND ( B.ISFIRST_DATE_MON = :ISFIRST_DATE_MON))