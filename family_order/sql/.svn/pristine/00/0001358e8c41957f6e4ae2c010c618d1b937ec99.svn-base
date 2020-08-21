--IS_CACHE=N
INSERT INTO CHNL_CU_SMSREC_PARALLEL
  (SERIAL_NBR,
   CUMU_ID,
   OLD_SCORE,
   CHANGE_SCORE,
   SCORE,
   DEAL_TYPE,
   AWORD_TYPE,
   STAFF_ID,
   DEPART_ID,
   DEAL_TIME,
   PRINT_FLAG,
   REMARK,
   BACK_SERIAL_NBR,
   PRE_CHAR1,
   PRE_CHAR2,
   PRE_NUM1,
   PRE_NUM2)
  SELECT SEQ_CHNL_EMPINTEGRAL_SERIALNBR.NEXTVAL,
         :CUMU_ID,
         :OLD_SCORE,
         :TRADE_SCORE,
         :LAST_SCORE,---LAST_SCORE=OLD_SCORE+TRADE_SCORE
         :TRADE_TYPE,-------3内部店员任务分值扣减,6店员积分兑奖（短信兑奖）,1积分登记时实时计算的积分,(11,12,22,32为月末结算)
         '',
         :TRADE_STAFF_ID,----'SUPERUSR',
         :TRADE_DEPART_ID,---'00000',
         SYSDATE,
         DECODE(:TRADE_TYPE,'3','9','6','7','1','7','11','11','22','22','32','32','1'),---3,9;6,7;1,7
         DECODE(:TRADE_TYPE,'3','内部店员任务分值扣减','6','店员积分短信兑奖','1','积分登记时实时计算的积分','11','积分计算:用户满足使用条件插入!','12','积分计算:登记的第四个月发放积分插入!','22','积分计算:登记的第五个月发放积分插入!','32','积分计算:登记的第六个月发放积分插入!','未知交易类型'),--REMARK
         :REGI_NUM,---V_BACK_ID---REGI_NUM
         :OPER_CODE,
         :TRADE_ID,
         :VERSION_ID,
         ''
        FROM DUAL