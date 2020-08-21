DECLARE
    iv_user_id         NUMBER(16)   := TO_NUMBER(:USER_ID);
    iv_remark          VARCHAR2(100):= :REMARK;
    iv_trade_depart_id CHAR(5)      := :TRADE_DEPART_ID;
    iv_start_acyc_id   NUMBER(6)    := :START_ACYC_ID;
    iv_disn_type_code  NUMBER(8)    := :DISN_TYPE_CODE;
    iv_trade_staff_id  CHAR(8)      := :TRADE_STAFF_ID;
    iv_trade_eparchy_code CHAR(4)   := :TRADE_EPARCHY_CODE;
    iv_trade_type_code NUMBER(4)    := :TRADE_TYPE_CODE;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    :DEPOSIT_CODE   := -1;
    BEGIN
    UPDATE tf_f_specific_discnt
    SET end_acyc_id=(SELECT acyc_id-1 FROM td_a_acycpara WHERE SYSDATE BETWEEN acyc_start_time AND acyc_end_time-1/24/3600),
    remark=iv_remark,
    update_time=SYSDATE,
    update_depart_id=iv_trade_depart_id,
    update_staff_id=iv_trade_staff_id
    WHERE id=iv_user_id
    AND id_type_code='0'
    AND disn_type_code=iv_disn_type_code
    AND start_acyc_id=iv_start_acyc_id
    AND start_acyc_id<=end_acyc_id;
    IF SQL%ROWCOUNT=0 THEN
        :INFO:='没有可取消的特殊优惠';
        RETURN;
    END IF;
    DELETE FROM tf_f_specific_discnt
    WHERE id=iv_user_id
    AND id_type_code='0'
    AND disn_type_code=iv_disn_type_code
    AND start_acyc_id=iv_start_acyc_id
    AND start_acyc_id>end_acyc_id;
    UPDATE tf_f_user_purchase a
    SET process_tag='2',end_date=SYSDATE,
    rsrv_str9=TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'),
    finish_date=SYSDATE,remark=iv_remark
    WHERE EXISTS (SELECT 1 FROM td_s_commpara
    WHERE param_attr=78  AND (eparchy_code=iv_trade_eparchy_code OR eparchy_code='ZZZZ')
    AND para_code2=a.purchase_mode AND para_code1=a.purchase_attr
    AND SYSDATE BETWEEN start_date AND end_date)
    AND user_id=iv_user_id
    AND SYSDATE<end_date
    AND process_tag='0' AND iv_trade_type_code=434 RETURNING RPAY_DEPOSIT_CODE INTO :DEPOSIT_CODE;
    UPDATE tf_f_user_purchase a
    SET process_tag='2',end_date=SYSDATE,
    rsrv_str9=TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'),
    finish_date=SYSDATE,remark=iv_remark
    WHERE EXISTS (SELECT 1 FROM td_s_commpara
    WHERE param_attr=91  AND (eparchy_code=iv_trade_eparchy_code OR eparchy_code='ZZZZ')
    AND para_code2=a.purchase_mode AND para_code1=a.purchase_attr
    AND SYSDATE BETWEEN start_date AND end_date)
    AND user_id=iv_user_id
    AND SYSDATE<end_date
    AND process_tag='0' AND iv_trade_type_code=436 RETURNING RPAY_DEPOSIT_CODE INTO :DEPOSIT_CODE;
    EXCEPTION
        WHEN OTHERS THEN
            :INFO:='取消用户特殊优惠异常:'||SQLERRM;
            RETURN;
    END;
    :CODE           := 0;
END;