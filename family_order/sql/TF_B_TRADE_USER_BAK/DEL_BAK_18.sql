delete from TF_B_TRADE_GRP_PLATSVC_BAK
where trade_id=to_number(:TRADE_ID)
  and accept_month=to_number(substr(:TRADE_ID,5,2))