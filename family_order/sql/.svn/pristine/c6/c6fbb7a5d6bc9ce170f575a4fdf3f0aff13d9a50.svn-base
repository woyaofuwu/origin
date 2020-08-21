DECLARE
    iv_trade_eparchy_code  CHAR(4):= :TRADE_EPARCHY_CODE;
    iv_trade_city_code CHAR(4):=:TRADE_CITY_CODE;
    iv_curdate    DATE:=SYSDATE;
    iv_province   CHAR(4);
    iv_orderno    CHAR(2);
    iv_audit_batch_no VARCHAR2(16);
    iv_audit_batch VARCHAR2(16);
    iv_batno1     NUMBER;
    iv_batno2     NUMBER;
    iv_batno3     NUMBER;
    iv_trade_staff_id  CHAR(8):= :TRADE_STAFF_ID;
    iv_tag        NUMBER:= :X_GETMODE;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
    :AUDIT_BATCH_NO :=' ';
    
    IF iv_tag IN (0,1) THEN
        SELECT tag_info
        INTO iv_province
          FROM td_s_tag
         WHERE eparchy_code=iv_trade_eparchy_code
           AND tag_code='PUB_CUR_PROVINCE'
           AND subsys_code='PUB'
           AND use_tag='0'
           AND start_date+0<iv_curdate
           AND end_date+0>=iv_curdate;
        IF iv_province='HNAN' AND iv_trade_eparchy_code='0731' THEN
            SELECT DECODE(iv_trade_city_code,'HNAN','ZX','A311','CS','A312','XS','A313','NX','A314','LY','A315','WC','QT')
            INTO iv_orderno FROM dual;
        ELSE
            SELECT LPAD(TO_CHAR(order_no),2,'0')
            INTO iv_orderno
            FROM td_m_area
            WHERE area_level=20
            AND area_code=iv_trade_eparchy_code;
        END IF;
        SELECT MAX(audit_batch_no)
        INTO iv_audit_batch
        FROM tf_b_trade_audit
        WHERE audit_batch_no LIKE TO_CHAR(iv_curdate,'YYYYMM')||iv_orderno||'%';
        
        if iv_audit_batch is null then
           iv_batno3:=1;
        else
           iv_batno3:=TO_NUMBER(SUBSTRB(iv_audit_batch,9,5));        
        end if;
        
    END IF;
    
    SELECT MAX(audit_batch_no)
    INTO iv_audit_batch_no
    FROM tf_b_trade_audit
    WHERE audit_staff_id=iv_trade_staff_id
    AND audit_batch_no LIKE TO_CHAR(iv_curdate,'YYYYMM')||iv_orderno||'%';
    IF iv_tag=3 THEN
        IF iv_audit_batch_no IS NULL THEN
            :info:='您还没有稽核过任何一笔工单！';
            RETURN;
        END IF;
    END IF;
    IF iv_audit_batch_no IS NULL THEN
        iv_batno1:=1;
    ELSE
        iv_batno1:=TO_NUMBER(SUBSTRB(iv_audit_batch_no,9,5));
    END IF;    
            
    IF iv_tag IN (0,1) THEN
    
        if (iv_audit_batch_no IS NULL and iv_batno1<>iv_batno3) or (iv_audit_batch_no IS NULL and iv_batno3=1 and iv_audit_batch is not null) then
        iv_batno1 :=iv_batno3+1;
        end if;
        
        IF iv_audit_batch_no IS NULL THEN
            iv_audit_batch_no:=TO_CHAR(iv_curdate,'YYYYMM')||iv_orderno||LPAD(TO_CHAR(iv_batno1),5,'0')||'00'||iv_tag;
        ELSIF SUBSTRB(iv_audit_batch_no,1,8)!=TO_CHAR(iv_curdate,'YYYYMM')||iv_orderno THEN
            iv_audit_batch_no:=TO_CHAR(iv_curdate,'YYYYMM')||iv_orderno||'0000100'||iv_tag;
        ELSE
        begin
            iv_batno2:=TO_NUMBER(SUBSTRB(iv_audit_batch_no,14,3))+iv_tag;
            IF iv_batno2-iv_tag>=100 THEN
                iv_batno1:=iv_batno3+1;
                iv_batno2:=iv_tag;
                iv_audit_batch_no:=TO_CHAR(iv_curdate,'YYYYMM')||iv_orderno||LPAD(TO_CHAR(iv_batno1),5,'0')||LPAD(TO_CHAR(iv_batno2),3,'0');
            ELSE
                iv_audit_batch_no:=SUBSTR(iv_audit_batch_no,1,13)||LPAD(TO_CHAR(iv_batno2),3,'0');
            END IF;
        end;
        END IF;
    END IF;
    :AUDIT_BATCH_NO := iv_audit_batch_no;
    :CODE           := 0;
END;