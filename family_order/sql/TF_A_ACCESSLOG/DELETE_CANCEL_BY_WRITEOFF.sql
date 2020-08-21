delete from tf_a_accesslog where (operate_id,partition_id) in (select operate_id2,partition_id from tf_a_chargerelation
 where operate_id1 in (select writeoff_id from tf_a_writeofflog where charge_id=:CHARGE_ID 
 and partition_id >= :PARTITION_ID - 1 and PARTITION_ID <=:PARTITION_ID + 1 and rownum<=1) ) 
 and cancel_tag='2'