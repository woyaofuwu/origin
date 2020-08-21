select t.orderid,
       t.kdzh,
       t.fee,
       t.jf_time,
       t.state,
       t.remark,
       t.update_time,
       t.deal_state,
       t.deal_time
  from TI_B_KDJF_RECON_RESULT t
 where to_char(to_date(t.jf_time, 'yyyy-mm-dd'), 'yyyy-mm-dd') =:SETTLEDATE