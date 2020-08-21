DECLARE
    iv_trade_eparchy_code   CHAR(4):= :TRADE_EPARCHY_CODE;
    iv_trade_city_code      CHAR(4):= :TRADE_CITY_CODE;
    iv_serial_number        VARCHAR2(15):= NVL(:SERIAL_NUMBER,'-');
    iv_sim_card_no          VARCHAR2(20):= NVL(:SIM_CARD_NO,'-');
    iv_brand_code           VARCHAR2(4):= NVL(:BRAND_CODE,'-');
    iv_rec_mphonecode_idle  tf_r_mphonecode_idle%ROWTYPE;
    iv_rec_simcard_idle     tf_r_simcard_idle%ROWTYPE;
BEGIN
    :CODE:= -1;
    :INFO:= 'FALSE!';
    IF LENGTH(iv_serial_number)=11 THEN
        BEGIN
       	    SELECT * INTO iv_rec_mphonecode_idle
       	        FROM tf_r_mphonecode_idle
    	        WHERE serial_number=iv_serial_number;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                :CODE := -1;
                :INFO:='该号码不在空闲表中！';
  	        RETURN;
        END;
        
        IF iv_rec_mphonecode_idle.code_state_code != '1' THEN
            :CODE := -1;
            :INFO:='该号码状态不正确！';
            RETURN;
        END IF;
        
        IF iv_rec_mphonecode_idle.precode_tag != '1' THEN
            :CODE := -1;
            :INFO:='号码没有预配！';
            RETURN;
        END IF ;
        
        IF iv_rec_mphonecode_idle.city_code != iv_trade_city_code THEN
            :CODE := -1;
            :INFO:='号码不在本业务区！';
            RETURN;
        END IF;
        
        IF iv_rec_mphonecode_idle.pool_code != '0' THEN
            :CODE := -1;
            :INFO:='号码不在普通号码池！';
            RETURN;
        END IF;
        IF iv_rec_mphonecode_idle.STOCK_LEVEL != 'C' THEN
            :CODE := -1;
            :INFO:='该号码资源不在业务区！';
            RETURN;
        END IF;
    ELSIF LENGTH(iv_sim_card_no)=20 THEN
        BEGIN
       	    SELECT * INTO iv_rec_simcard_idle
       	        FROM tf_r_simcard_idle
    	        WHERE sim_card_no=iv_sim_card_no;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                :CODE := -1;
                :INFO:='该SIM卡不在空闲表中！';
  	        RETURN;
        END;
        
        IF iv_rec_simcard_idle.sim_state_code != '1' THEN
            :CODE := -1;
            :INFO:='该SIM卡状态不正确！';
            RETURN;
        END IF;
        
        IF iv_rec_simcard_idle.precode_tag != '1' THEN
            :CODE := -1;
            :INFO:='该SIM卡没有预配！';
            RETURN;
        END IF;
        
        IF iv_rec_simcard_idle.city_code != iv_trade_city_code THEN
            :CODE := -1;
            :INFO:='该SIM卡不在本业务区！';
            RETURN;
        END IF;
        IF iv_rec_simcard_idle.STOCK_LEVEL != 'C' THEN
            :CODE := -1;
            :INFO:='该SIM卡资源不在业务区！';
            RETURN;
        END IF;
        
    ELSE
        :CODE := -1;
        :INFO:='资料不完整！';
        RETURN;
    END IF;
    :CODE:= 0;
    :INFO:= 'Ok!';
END;