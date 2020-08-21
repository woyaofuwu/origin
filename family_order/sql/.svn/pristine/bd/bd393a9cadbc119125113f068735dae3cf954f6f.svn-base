DECLARE
iv_trade_id NUMBER(16):=TO_NUMBER(:trade_id);
iv_user_id  NUMBER(16):=TO_NUMBER(:user_id);
iv_partition_id NUMBER(4):=MOD(iv_user_id,10000);
BEGIN
:CODE:=-1;
:INFO:='TRADE OK';
BEGIN
INSERT INTO tf_f_user_ocs SELECT * FROM tf_f_user_ocs_inc WHERE user_id =iv_user_id AND partition_id=iv_partition_id;
EXCEPTION
WHEN OTHERS THEN
IF SQLCODE=-1 THEN
FOR i IN (SELECT * FROM tf_f_user_ocs_inc WHERE user_id =iv_user_id AND partition_id=iv_partition_id) LOOP
DELETE FROM tf_f_user_ocs WHERE user_id =i.user_id AND partition_id=i.partition_id AND start_date=i.start_date;
END LOOP;
INSERT INTO tf_f_user_ocs SELECT * FROM tf_f_user_ocs_inc WHERE user_id =iv_user_id AND partition_id=iv_partition_id;
ELSE
:info:='数据增量挪入全量出错：'||SUBSTRB(SQLERRM,12);
RETURN;
END IF;
END;
DELETE FROM  tf_f_user_ocs_inc WHERE user_id =iv_user_id AND partition_id=iv_partition_id;
:CODE:=0;
END;