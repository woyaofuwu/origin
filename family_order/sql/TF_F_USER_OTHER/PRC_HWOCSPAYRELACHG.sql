Declare
    iv_process_tag_set     CHAR(20):=:PROCESS_TAG_SET;
    iv_rsrv_str8           VARCHAR2(500) := :RSRV_STR8;
    iv_trade_id            NUMBER(16):=TO_NUMBER(:TRADE_ID);
    iv_trade_type_code     NUMBER(4);
    iv_serial_number       VARCHAR2(15);
BEGIN
    :CODE:= -1;
    :INFO:= 'TRADE OK!';
    IF TRIM(iv_trade_id) IS NOT NULL AND TRIM(iv_process_tag_set) IS NOT NULL THEN
       BEGIN
        SELECT trade_type_code,serial_number INTO iv_trade_type_code,iv_serial_number from tf_b_trade WHERE trade_id=iv_trade_id;
          EXCEPTION
           WHEN OTHERS THEN
         :INFO:='获取台账工单业务类型出错'||substr(sqlerrm,1,150);
          RETURN;
       END;
    END IF;
    IF iv_trade_type_code=160 THEN  --普通付费关系变更
       if (substr(iv_process_tag_set,6,1) = '1') then --业务用户为OCS用户，需要下个月切回到BOSS系统
             Begin
               UPDATE TF_F_USER_HWOCS SET end_date=trunc(add_months(SYSDATE,1),'MM')-1/24/3600,update_time=sysdate
                  WHERE serial_number=iv_serial_number and end_date>sysdate;
             Exception
               WHEN OTHERS THEN
              :CODE := -1;
              :INFO:='更新结束时间出错'||substr(sqlerrm,1,150);
              RETURN;
              END;
        elsif (substr(iv_process_tag_set,6,1) = '2') then--目标用户为OCS用户，需要下个月切回到BOSS系统
              Begin
               UPDATE TF_F_USER_HWOCS SET end_date=trunc(add_months(SYSDATE,1),'MM')-1/24/3600,update_time=sysdate
                  WHERE serial_number = iv_rsrv_str8 and end_date>sysdate;
             Exception
               WHEN OTHERS THEN
              :CODE := -1;
              :INFO:='更新结束时间出错'||substr(sqlerrm,1,150);
              RETURN;
              END;
        elsif (substr(iv_process_tag_set,6,1) = '3') then--两者都为OCS用户，需要下个月切回到BOSS系统
              Begin
               UPDATE TF_F_USER_HWOCS SET end_date=trunc(add_months(SYSDATE,1),'MM')-1/24/3600,update_time=sysdate
                  WHERE (serial_number=iv_serial_number or serial_number = iv_rsrv_str8) and end_date>sysdate;
             Exception
               WHEN OTHERS THEN
              :CODE := -1;
              :INFO:='更新结束时间出错'||substr(sqlerrm,1,150);
              RETURN;
              END;
         END if;
   ELSIF iv_trade_type_code=170 THEN  
          if (substr(iv_process_tag_set,1,1) = '1') then --业务用户为OCS用户
               Begin
                 UPDATE TF_F_USER_HWOCS SET end_date=trunc(add_months(SYSDATE,1),'MM')-1/24/3600,update_time=sysdate
                    WHERE serial_number=iv_serial_number and end_date>sysdate;
               Exception
                 WHEN OTHERS THEN
                :CODE := -1;
                :INFO:='更新结束时间出错'||substr(sqlerrm,1,150);
                RETURN;
                END;
          elsif (substr(iv_process_tag_set,1,1) = '2') then--目标用户为OCS用户
                Begin
                 UPDATE TF_F_USER_HWOCS SET end_date=trunc(add_months(SYSDATE,1),'MM')-1/24/3600,update_time=sysdate
                    WHERE serial_number = iv_rsrv_str8 and end_date>sysdate;
               Exception
                 WHEN OTHERS THEN
                :CODE := -1;
                :INFO:='更新结束时间出错'||substr(sqlerrm,1,150);
                RETURN;
                END;
          elsif (substr(iv_process_tag_set,1,1) = '3') then--两者都为OCS用户
                Begin
                 UPDATE TF_F_USER_HWOCS SET end_date=trunc(add_months(SYSDATE,1),'MM')-1/24/3600,update_time=sysdate
                    WHERE (serial_number=iv_serial_number or serial_number = iv_rsrv_str8) and end_date>sysdate;
               Exception
                 WHEN OTHERS THEN
                :CODE := -1;
                :INFO:='更新结束时间出错'||substr(sqlerrm,1,150);
                RETURN;
                END;
           END if;
   END IF;
    :CODE:= 0;
END;