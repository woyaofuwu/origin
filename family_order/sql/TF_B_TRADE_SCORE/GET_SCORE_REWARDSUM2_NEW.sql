          SELECT         
               A.RULE_ID,
               SUM(ACTION_COUNT) ACTION_COUNT,
               TO_CHAR(NVL(SUM(VALUE_CHANGED) / 100, 0)) VALUE_CHANGED        
          FROM TF_B_TRADE_SCORE   A,
               TF_BH_TRADE        B,
			   td_m_depart        d

          WHERE A.TRADE_ID = B.TRADE_ID
           AND A.ACCEPT_MONTH = B.ACCEPT_MONTH
           AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:START_DATE, 6, 2))
           AND B.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:START_DATE, 6, 2))
           AND B.ACCEPT_DATE >= TO_DATE(:START_DATE, 'yyyy-mm-dd')
           AND B.ACCEPT_DATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd') + 1
           AND A.RULE_ID = TRIM(:RULE_ID)
		   AND b.trade_depart_id = d.depart_id
           AND B.TRADE_CITY_CODE = TRIM(:TRADE_CITY_CODE)
		   AND d.depart_kind_code = TRIM(:DEPART_KIND_CODE)
           AND B.TRADE_TYPE_CODE = '330' 
          GROUP BY A.RULE_ID