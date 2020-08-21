SELECT t.id_card_num ,t.total ,
      :ID_CARD_TYPE   ID_CARD_TYPE 
  FROM TF_F_OPENLIMIT_DAILYCONTRACT T
 WHERE T.ID_CARD_TYPE IN ( select a.para_code1 FROM TD_S_COMMPARA A  WHERE A.subsys_code='CSM'  AND A.param_attr='2553'  and a.param_code=:ID_CARD_TYPE  AND sysdate BETWEEN A.start_date AND A.end_date  )
   AND T.ID_CARD_NUM = :ID_CARD_NUM
