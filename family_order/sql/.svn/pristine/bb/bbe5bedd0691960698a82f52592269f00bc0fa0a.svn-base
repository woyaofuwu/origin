SELECT NVL(SUM(CHANGE_SCORE),0) CHANGE_SCORE
  FROM CHNL_CU_SMSREC A
 WHERE A.CUMU_ID = :CUMU_ID
   AND A.DEAL_TYPE = '6'
   AND TO_CHAR(A.DEAL_TIME, 'yyyy-mm') >= TO_CHAR(add_months(SYSDATE,'-3'), 'yyyy-mm')
   AND TO_CHAR(A.DEAL_TIME, 'yyyy-mm') <= TO_CHAR(add_months(SYSDATE,'-1'), 'yyyy-mm')