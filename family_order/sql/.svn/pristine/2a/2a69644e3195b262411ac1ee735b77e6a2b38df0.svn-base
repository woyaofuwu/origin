select SEQ,
       CUSTOMER_NAME,
      :ID_CARD_TYPE  ID_CARD_TYPE ,
       ID_CARD_NUM,
       CAMP_ON,
       P_SEQ,
       to_char(START_TIME,'yyyy-mm-dd hh24:mi:ss') START_TIME,
       to_char(END_TIME,'yyyy-mm-dd hh24:mi:ss') END_TIME,
       to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       REMARK,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_TAG1
  from TF_F_OPENLIMIT_CAMPON_IBOSS T
 where 1=1
   and T.ID_CARD_TYPE IN ( select a.para_code1 FROM TD_S_COMMPARA A  WHERE A.subsys_code='CSM'  AND A.param_attr='2553'  and a.param_code=:ID_CARD_TYPE  AND sysdate BETWEEN A.start_date AND A.end_date  )
   and T.ID_CARD_NUM = :ID_CARD_NUM
   and T.CAMP_ON = :CAMP_ON
   -- STATE = 1 预占成功 , =3 用户信息同步失败
   and (T.STATE=1 or T.STATE=3  ) 
   and sysdate between T.START_TIME and T.END_TIME
