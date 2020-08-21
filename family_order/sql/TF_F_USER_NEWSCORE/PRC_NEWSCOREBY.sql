DECLARE
    iv_score  NUMBER := to_number(:SCORE);
    iv_year_id CHAR(4) := to_number(:YEAR_ID);
    iv_rsrv_num1 number := :RSRVNUM1;
    iv_user_id NUMBER(16) := to_number(:USER_ID);
    iv_acyc_id NUMBER;    
BEGIN    
    SELECT acyc_id INTO iv_acyc_id
    From td_a_acycpara WHERE SYSDATE BETWEEN acyc_start_time AND acyc_end_time;
    BEGIN
    UPDATE tf_f_user_newscore
       SET score=nvl(score,0)+iv_score,rsrv_num1=nvl(iv_rsrv_num1,0)
     WHERE user_id=iv_user_id
       AND partition_id=MOD(iv_user_id,10000)
       AND year_id=iv_year_id
       AND id_type='0'
       AND score_type_code IN ('02','03');
     IF SQL%ROWCOUNT=0 THEN
     INSERT INTO tf_f_user_newscore(user_id,partition_id,year_id,acyc_id,id_type,score_type_code,score,rsrv_num1)
     VALUES(iv_user_id,MOD(iv_user_id,10000),iv_year_id,iv_acyc_id,'0','02',iv_score,iv_rsrv_num1);
     END IF;
     EXCEPTION
     WHEN OTHERS THEN
     NULL;
     :RESULTINFO:='修改年度积分出错：'||substr(sqlerrm,1,150);
     END;
     
    BEGIN
        UPDATE tf_ah_integralbill SET integral_fee=iv_score
        WHERE user_id=iv_user_id AND integral_type_code='c' AND acyc_id=iv_acyc_id;
        IF SQL%ROWCOUNT=0 THEN
        INSERT INTO tf_ah_integralbill(partition_id,user_id,integral_type_code,integral_fee,acyc_id)
        VALUES(MOD(iv_user_id,10000),iv_user_id,'c',iv_score,iv_acyc_id);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
        NULL;
        :RESULTINFO:='修改用户月积分历史表出错：'||substr(sqlerrm,1,150);
    END;     
END;