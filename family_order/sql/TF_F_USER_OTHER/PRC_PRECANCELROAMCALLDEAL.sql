DECLARE
    iv_user_id     NUMBER(16):= TO_NUMBER(:USER_ID);
    iv_product_id  NUMBER(8);
    iv_count NUMBER;
    iv_brand_code   CHAR(4);
    iv_serial_number    VARCHAR2(20);
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    SELECT product_id,brand_code,serial_number
    INTO iv_product_id,iv_brand_code,iv_serial_number
    FROM tf_f_user_infochange
    WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
    AND SYSDATE BETWEEN start_date AND end_date+0;
    SELECT COUNT(1)
    INTO iv_count
    FROM  tf_f_user_svc a
    WHERE SYSDATE BETWEEN a.start_date AND a.end_date+0 AND a.service_id+0 BETWEEN 13 AND 19
    AND user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
    AND NOT EXISTS (SELECT 1 FROM td_b_product_svc b
    WHERE SYSDATE BETWEEN b.start_date AND b.end_date+0 AND product_id=iv_product_id
    AND a.service_id=b.service_id);
    IF iv_count>0 THEN
    :INFO:='业务校验,恢复用户的老的产品不存在现在已经生效的通话或漫游级别';
    RETURN;
    END IF;
    DELETE FROM tf_f_user_brandchange WHERE user_id=iv_user_id AND start_date>SYSDATE;
    UPDATE tf_f_user_brandchange SET end_date=to_date('20501231 23:59:59','yyyymmdd hh24:mi:ss') 
    WHERE user_id=iv_user_id AND SYSDATE BETWEEN start_date AND end_date;
    UPDATE tf_r_mphonecode_use
       SET product_id=iv_product_id
     WHERE serial_number=iv_serial_number;    
    :CODE           := 0;
END;