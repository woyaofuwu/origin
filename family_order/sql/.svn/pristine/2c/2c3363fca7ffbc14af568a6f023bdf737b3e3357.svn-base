DECLARE
    iv_trade_id     NUMBER(16) := TO_NUMBER(:TRADE_ID);
    iv_user_id      NUMBER(16) := TO_NUMBER(:USER_ID);
    iv_brand_no    	CHAR(1):=:BRAND_NO;
    iv_vip_id    	CHAR(1):=:VIP_ID;
    iv_eparchy_code CHAR(4):=:EPARCHY_CODE;
    iv_brand_code   CHAR(4):=:BRAND_CODE;
    iv_trade_type_code       NUMBER(4):=:TRADE_TYPE_CODE;
    iv_update_staff_id  CHAR(8):=:UPDATE_STAFF_ID;
    iv_update_depart_id  CHAR(5):=:UPDATE_DEPART_ID;
    iv_date DATE := SYSDATE;
    iv_count NUMBER;
BEGIN
    IF iv_brand_no='2' THEN--获取用户最后生效的神州行优惠   
        BEGIN         
        SELECT MAX(start_date) INTO iv_date
          FROM tf_f_user_discnt
         WHERE partition_id = MOD(iv_user_id,10000)
            AND user_id=iv_user_id
            AND end_date>SYSDATE;    
        EXCEPTION
        WHEN OTHERS THEN
            :RESULTINFO:= '用户没有生效的优惠!';
        END;
    ELSIF iv_brand_no='1' AND iv_vip_id='1' THEN--如果是大客户
        iv_date:=SYSDATE;
    ELSE        
        BEGIN    
        --获取影响品牌的优惠的最小开始时间
        SELECT MIN(a.start_date) INTO iv_date
          FROM tf_f_user_discnt a,td_s_commpara b
         WHERE a.partition_id = MOD(iv_user_id,10000)
            AND a.user_id=iv_user_id
            AND a.end_date>SYSDATE
            AND b.subsys_code='CSM'
            AND b.param_attr=2001
            AND b.param_code=decode(iv_brand_no,'1','GTONDSCN','3','MZonDSCN','4','GSDSCN')
            AND b.para_code1=to_char(a.discnt_code)
            AND (b.eparchy_code = iv_eparchy_code OR b.eparchy_code='ZZZZ')
            AND b.end_date>SYSDATE;
        EXCEPTION
            WHEN OTHERS THEN
            iv_date:=SYSDATE;
        END;
    END IF;   
    IF iv_trade_type_code IN (5001,5002) THEN--大客户修改和大客户注销都取系统时间
        iv_date:=SYSDATE;
    END IF; 
    --看用户生效的品牌中是否有和新品牌不一致的结果，且时间大于导致品牌变化的优惠的最早开始时间
    SELECT COUNT(*) INTO iv_count FROM tf_f_user_brandchange
     WHERE user_id=iv_user_id
       AND partition_id=MOD(iv_user_id,10000)
       AND brand_no!=iv_brand_no
       AND end_date>=iv_date;    
    IF iv_count > 0 OR iv_trade_type_code IN (10,500) THEN
    BEGIN
    --变更生效的品牌记录
        UPDATE tf_f_user_brandchange SET end_date=iv_date-0.00001,relation_trade_id=to_char(iv_trade_id),
        trade_type_code=iv_trade_type_code,update_staff_id=iv_update_staff_id,update_depart_id=iv_update_depart_id
        WHERE user_id=iv_user_id
        AND partition_id=MOD(iv_user_id,10000)
        AND end_date>=iv_date;
    --删除错误记录
        DELETE FROM tf_f_user_brandchange WHERE user_id=iv_user_id
        AND partition_id=MOD(iv_user_id,10000)
        AND start_date>end_date;  
    --防止主键冲突
        DELETE FROM tf_f_user_brandchange WHERE user_id=iv_user_id            
        AND partition_id=MOD(iv_user_id,10000)
        AND start_date=iv_date;
    --插入新的品牌记录
        INSERT INTO tf_f_user_brandchange(PARTITION_ID,USER_ID,TRADE_TYPE_CODE,RELATION_TRADE_ID,BRAND_CODE,BRAND_NO,VIP_ID,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,RSRV_STR3,RSRV_STR4)
        VALUES (MOD(iv_user_id,10000),iv_user_id,iv_trade_type_code,iv_trade_id,iv_brand_code,iv_brand_no,iv_vip_id,nvl(iv_date,SYSDATE),to_date('20501231','yyyymmdd'),nvl(iv_date,SYSDATE),iv_update_staff_id,iv_update_depart_id,'0','0');
    EXCEPTION
    WHEN OTHERS THEN
        :RESULTINFO:= '更新用户品牌记录异常:'||substr(sqlerrm,1,170);
    END;
    END IF;       
END;