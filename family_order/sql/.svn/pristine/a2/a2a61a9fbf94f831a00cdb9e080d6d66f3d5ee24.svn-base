DECLARE
    iv_tax_no  VARCHAR2(20) := :RSRV_STR9;
    iv_ticket_id  VARCHAR2(30) := :RSRV_STR10;
BEGIN
    :CODE:= -1;
    :INFO:= 'TRADE OK!';
    Begin
    UPDATE tf_r_ticket SET tax_no=iv_tax_no,ticket_state_code='2'
    WHERE ticket_id = iv_ticket_id;
    Exception
        WHEN OTHERS THEN
        :CODE := -1;
        :INFO:='更新用户对应凭证号信息出错！TICKET_ID:'+TO_CHAR(iv_ticket_id);
        RETURN;
    END;
    :CODE:= 0;
END;