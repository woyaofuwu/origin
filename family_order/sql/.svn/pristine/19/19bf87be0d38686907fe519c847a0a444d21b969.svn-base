delete from tf_a_accesslog where (operate_id,partition_id) in (select operate_id2,partition_id from tf_a_chargerelation
 where operate_id1=:OPERATE_ID1) and cancel_tag='2'