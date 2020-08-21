insert into TF_SM_CAMPAIGN_ASKED_RESULT
  (SERV_ID,
   MSISDN,
   EPARCHY_CODE,
   DEPART_ID,
   STAFF_ID,
   MARKET_ID,
   MARKET_NAME,
   OPER_TYPE,
   OPER_CODE,
   ACCEPT_TIME,
   ACCEPT_FLAG,
   EFFECT_CODE,
   REMARK,
   CHANNLE_TYPE,
   REFUSE_CODE,
   REFUSE_REASON_DESC,
   TRADE_ID,
   CAMPN_ID) values
  (TO_NUMBER(:SERV_ID), :MSISDN, :EPARCHY_CODE,:DEPART_ID, :STAFF_ID, TO_NUMBER(:MARKET_ID), :MARKET_NAME, 
  :OPER_TYPE, :OPER_CODE,TO_DATE(:ACCEPT_TIME,'YYYY-MM-DDHH24:MI:SS'), :ACCEPT_FLAG,:EFFECT_CODE, :REMARK,
  :CHANNLE_TYPE,:REFUSE_CODE,:REFUSE_REASON_DESC,TO_NUMBER(:TRADE_ID),TO_NUMBER(:CAMPN_ID))