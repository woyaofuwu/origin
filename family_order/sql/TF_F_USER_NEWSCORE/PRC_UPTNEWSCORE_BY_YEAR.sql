DECLARE
    iv_user_id          NUMBER(16) := to_number(:USER_ID);
    iv_id_type          CHAR(1)    := :ID_TYPE;
    iv_score_type_code  CHAR(2)    := :SCORE_TYPE_CODE;
    iv_score            NUMBER     := to_number(:SCORE);
    iv_year_id          CHAR(4);
    iv_score_plus       NUMBER     := to_number(:SCORE);
    iv_acyc_id          NUMBER;
BEGIN     
    BEGIN
        WHILE iv_score < 0 LOOP
            BEGIN
                SELECT score+iv_score,year_id INTO iv_score,iv_year_id
                  FROM tf_f_user_newscore
                 WHERE user_id=iv_user_id
                   AND partition_id=MOD(iv_user_id,10000)
                   AND score_type_code=iv_score_type_code
                   AND id_type = iv_id_type
                   AND score > 0 AND ROWNUM=1 ORDER BY year_id DESC;  
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                RETURN;
                :RESULTINFO:='用户扣减积分大于可扣减积分！';                 
            END;
            IF iv_score>=0 THEN  
                UPDATE tf_f_user_newscore SET score=iv_score,rsrv_num2=score 
                WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
                AND year_id = iv_year_id AND score > 0 AND id_type = iv_id_type
                AND score_type_code = iv_score_type_code;
            ELSIF iv_score < 0 THEN
                UPDATE tf_f_user_newscore SET score=0,rsrv_num2=score 
                WHERE user_id=iv_user_id AND partition_id=MOD(iv_user_id,10000)
                AND year_id = iv_year_id AND score > 0 AND id_type = iv_id_type
                AND score_type_code = iv_score_type_code;    
            END IF;
        END LOOP;
        SELECT acyc_id INTO iv_acyc_id
            From td_a_acycpara WHERE SYSDATE BETWEEN acyc_start_time AND acyc_end_time;
        IF iv_score_plus >=0 THEN
                BEGIN
                UPDATE tf_f_user_newscore
                  SET score=nvl(score,0)+iv_score_plus
                WHERE user_id=iv_user_id
                  AND partition_id=MOD(iv_user_id,10000)
                  AND year_id=to_char(sysdate, 'YYYY')
                  AND id_type='0'
                  AND score_type_code IN ('02','03');
                IF SQL%ROWCOUNT=0 THEN
                INSERT INTO tf_f_user_newscore(user_id,partition_id,year_id,acyc_id,id_type,score_type_code,score,rsrv_num1)
                VALUES(iv_user_id,MOD(iv_user_id,10000),to_char(sysdate, 'YYYY'),iv_acyc_id,'0','02',iv_score_plus,'0');
                END IF;
                EXCEPTION
                WHEN OTHERS THEN
                NULL;
                :RESULTINFO:='修改年度积分出错：'||substr(sqlerrm,1,150);
                END;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
        RETURN;
        :RESULTINFO:='积分修改出错：'||substr(sqlerrm,1,150);
    END;
END;