DECLARE
iv_user_id  NUMBER(16):=TO_NUMBER(:USER_ID);
iv_partition_id NUMBER(4):=MOD(iv_user_id,10000);
iv_curdate  DATE:=SYSDATE;
BEGIN
:CODE:=-1;
:INFO:='TRADE OK';
SELECT COUNT(1)
INTO :check_tag
FROM tf_f_user_ocs
WHERE user_id=iv_user_id AND partition_id=iv_partition_id
AND end_date>iv_curdate AND ROWNUM<2;
IF :check_tag=0 THEN
SELECT COUNT(1)
INTO :check_tag
FROM tf_f_user_ocs_inc
WHERE user_id=iv_user_id AND partition_id=iv_partition_id
AND end_date>iv_curdate AND ROWNUM<2;
END IF;
:CODE:=0;
END;