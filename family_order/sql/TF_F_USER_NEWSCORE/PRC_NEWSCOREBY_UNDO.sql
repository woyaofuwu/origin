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
       SET score=nvl(score,0)-iv_score,rsrv_num1=rsrv_num1-nvl(iv_rsrv_num1,0)
     WHERE user_id=iv_user_id
       AND partition_id=MOD(iv_user_id,10000)
       AND year_id=iv_year_id
       AND id_type='0'
       AND score_type_code IN ('02','03');
       
    UPDATE tf_ah_integralbill SET integral_fee=nvl(integral_fee,0)-iv_score
    WHERE user_id=iv_user_id AND integral_type_code='c' AND acyc_id=iv_acyc_id;       
     EXCEPTION
     WHEN OTHERS THEN
     NULL;
     :RESULTINFO:='修改年度积分出错、月积分历史表出错【返销】：'||substr(sqlerrm,1,150);
     END;
END;