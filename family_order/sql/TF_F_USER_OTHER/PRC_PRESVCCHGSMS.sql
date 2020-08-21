DECLARE
    iv_trade_id     NUMBER(16) := TO_NUMBER(:TRADE_ID);
    iv_user_id      NUMBER(16) := TO_NUMBER(:USER_ID);
    iv_service_id   NUMBER(8);
    iv_modify_tag   CHAR(1);
    iv_modify       VARCHAR2(20);
    iv_brand        CHAR(10);
    iv_service_name VARCHAR2(20);
    iv_notice_content VARCHAR2(500);
    iv_notice_content1 VARCHAR2(500);
    iv_exec_time    DATE;
    iv_start_date   DATE;
    iv_end_date     DATE;
    iv_count        NUMBER;
    TYPE t_cursor IS ref CURSOR;
    iv_cursor                           t_cursor;
BEGIN
    :CODE           := -1;
    :INFO           := 'TRADE OK!';
     iv_notice_content :='';
     iv_notice_content1 :='';
        select decode(rsrv_str1,'1','全球通','3','动感地带','神州行') into iv_brand from tf_f_user_brandchange 
             where partition_id=mod(iv_user_id,10000) and user_id=iv_user_id and sysdate between start_date and end_date;
      OPEN iv_cursor FOR 
        select a.service_id,a.modify_tag,a.start_date,a.end_date,b.exec_time  from tf_b_trade_svc a,tf_b_trade b where a.trade_id=iv_trade_id and b.trade_id=iv_trade_id ;
      LOOP
                FETCH iv_cursor INTO iv_service_id,iv_modify_tag,iv_start_date,iv_end_date,iv_exec_time;
                EXIT WHEN iv_cursor%NOTFOUND;     
      iv_count:=0;
    	 SELECT count(*) INTO iv_count
    	   FROM tf_b_trade_svc
    	  WHERE trade_id=iv_trade_id;
    
      IF iv_count=1 THEN
        IF iv_modify_tag=0  THEN
          SELECT decode(iv_modify_tag,'0','开通','关闭') INTO iv_modify FROM dual;
          SELECT service_name INTO iv_service_name from td_b_service WHERE service_id = iv_service_id; 
          :NOTICE_CONTENT :='尊敬的'||Trim(iv_brand)||'品牌客户，您预约办理的服务变更业务已受理成功，'||iv_service_name||'服务将在'||to_char(iv_exec_time,'yyyy-mm-dd hh24:mi:ss')||iv_modify||',谢谢使用。';
      ELSIF iv_modify_tag=1 THEN
         SELECT decode(iv_modify_tag,'0','开通','关闭') INTO iv_modify FROM dual;
         SELECT service_name INTO iv_service_name from td_b_service WHERE service_id =iv_service_id ; 
          :NOTICE_CONTENT :='尊敬的'||Trim(iv_brand)||'品牌客户，您预约办理的服务变更业务已受理成功，'||iv_service_name||'服务将在'||to_char(iv_exec_time,'yyyy-mm-dd hh24:mi:ss')||iv_modify||',谢谢使用。';
      END IF;
     END IF; 
     IF iv_count>1 THEN
      IF iv_modify_tag=0 THEN
          SELECT decode(iv_modify_tag,'0','开通','关闭') INTO iv_modify FROM dual;
          SELECT service_name INTO iv_service_name from td_b_service WHERE service_id =iv_service_id ;  
          iv_notice_content :=iv_service_name||'服务将在'||to_char(iv_exec_time,'yyyy-mm-dd hh24:mi:ss')||iv_modify||',';
        END IF;  
      IF iv_modify_tag=1 THEN
         SELECT decode(iv_modify_tag,'0','开通','关闭') INTO iv_modify FROM dual;
         SELECT service_name INTO iv_service_name from td_b_service WHERE service_id =iv_service_id ; 
       -- :NOTICE_CONTENT :='尊敬的'||Trim(iv_brand)||'品牌客户，您预约办理的服务变更业务已受理成功，'||iv_notice_content||iv_service_name||'服务将在'||to_char(iv_exec_time,'yyyy-mm-dd hh24:mi:ss')||iv_modify||',谢谢使用。';
         iv_notice_content1 :=iv_service_name||'服务将在'||to_char(iv_exec_time,'yyyy-mm-dd hh24:mi:ss')||iv_modify||',';
      END IF;
      :NOTICE_CONTENT :='尊敬的'||Trim(iv_brand)||'品牌客户，您预约办理的服务变更业务已受理成功，'||iv_notice_content||iv_notice_content1||'谢谢使用。';
    END IF;
    IF iv_count <1 THEN 
     :NOTICE_CONTENT := 'X';
    END IF;
      END LOOP;
    Close iv_cursor;
    :CODE           := 0;
END;