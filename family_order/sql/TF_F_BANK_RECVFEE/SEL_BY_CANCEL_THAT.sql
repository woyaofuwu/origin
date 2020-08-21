SELECT id,agentcode,bank_card_no,cardname,acntbalance,aimp_fee,bank_tag,boss_tag,cycle_id,staff_id,trade_id,develop_city_code,trans_code,bank_trade_id,trade_time,bank_return_code,bank_return_info,cancel_tag,cancel_bank_trade_id,cancel_trade_time,cancel_bank_return_code,cancel_bank_return_info,remark,prevalue1,prevalue2,prevalue3,prevalue4,prevalue5,serial_number,charge_id 
  FROM tf_f_bank_recvfee
 WHERE prevalue5 between :STARTDATE and :ENDDATE
and substr(trade_time,1,8)!=substr(prevalue5,1,8)
union 
SELECT id,agentcode,bank_card_no,cardname,acntbalance,aimp_fee,bank_tag,boss_tag,cycle_id,staff_id,trade_id,develop_city_code,trans_code,bank_trade_id,trade_time,bank_return_code,bank_return_info,cancel_tag,cancel_bank_trade_id,cancel_trade_time,cancel_bank_return_code,cancel_bank_return_info,remark,prevalue1,prevalue2,prevalue3,prevalue4,prevalue5,serial_number,charge_id 
  FROM tf_f_bank_recvfee
 WHERE prevalue5 between :STARTDATE and :ENDDATE
and substr(trade_time,1,8)=substr(prevalue5,1,8) and cancel_tag <> 2