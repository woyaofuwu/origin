select decode(sign(a.cou-b.cou),-1,1,0) recordcount from
(
select nvl(b.rsrv_str2,0) cou  From tf_b_trade_svc a,td_b_serv_itemb b
  where trade_id= TO_NUMBER(:TRADE_ID)
    and  accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
    and b.service_id=82 and b.item_index='1' and a.modify_tag='2'
      and a.serv_para1=b.item_field_code
)a,
(
select nvl(b.rsrv_str2,0) cou from tf_f_user_svc a,td_b_serv_itemb b
  where user_id=to_number(:USER_ID)
    and partition_id=MOD(to_number(:USER_ID),10000)
     and a.service_id=82
       and a.end_Date>sysdate and b.service_id=82 and b.item_index='1'
         and a.serv_para1=b.item_field_code
)b