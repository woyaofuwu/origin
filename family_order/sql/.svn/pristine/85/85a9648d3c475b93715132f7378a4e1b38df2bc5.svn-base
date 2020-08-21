DECLARE
iv_acycstarttime            DATE;
iv_acycendtime              DATE;
iv_servid                   NUMBER(16) := :USER_ID;
iv_partitionid              NUMBER(12,2);
iv_resultcode               NUMBER;
iv_resulterrinfo            VARCHAR2(200);
iv_count1                   NUMBER := 0;
iv_count2                   NUMBER := 0;
iv_count3                   NUMBER := 0;
BEGIN 
:CODE:= 0;
:TAG := 0;
:INFO:= 'TRADE OK!';
iv_partitionid := MOD( iv_servid, 10000);
BEGIN
     SELECT MIN(acyc_start_time), min(acyc_end_time)
        INTO iv_acycstarttime, iv_acycendtime
        FROM td_a_acycpara
    WHERE use_tag = '0';
EXCEPTION
    WHEN OTHERS THEN
        :CODE := -1;
        :INFO := 'p_cms_scorecreate_yn error_002:'||SQLERRM;
        RETURN;
END;
BEGIN
    SELECT COUNT(*) INTO iv_count3
    FROM tf_f_user_other
    WHERE partition_id=iv_partitionid
        AND user_id=iv_servid
        AND    RSRV_VALUE_CODE = '2005'
        AND start_date < iv_AcycEndTime
        AND end_date >=iv_AcycEndTime;
EXCEPTION
    WHEN OTHERS THEN
        :CODE:=-1;
        :INFO:='p_asp_scorecreate_yn error_013.5:'||SQLERRM;
        RETURN;
END;
IF iv_count3 > 0 THEN
    BEGIN
        SELECT COUNT(*) INTO iv_count3
        FROM tf_f_user_other
        WHERE partition_id=iv_partitionid
            AND user_id=iv_servid
            AND    RSRV_VALUE_CODE = 'ScRg'
            AND start_date < iv_AcycEndTime
            AND end_date >=iv_AcycEndTime;
    EXCEPTION
        WHEN OTHERS THEN
            :CODE:=-1;
            :INFO:='p_asp_scorecreate_yn error_013.5:'||SQLERRM;
            RETURN;
    END;
    IF iv_count3 > 0 THEN
        :TAG:=2;
    END IF;
ELSE
    BEGIN
        SELECT sum(decode(a.discnt_code,86000000,1,99000000,1,0)),
                   sum(decode(a.discnt_code,86000000,0,99000000,0,1)),
                   sum(decode(b.rsrv_str4,'COMPUTESCORE',decode(a.discnt_code,86000000,0,99000000,0,1),0))
        INTO iv_count1,iv_count2,iv_count3
        FROM TF_F_USER_DISCNT a,td_b_discnt b
        WHERE a.partition_id=iv_partitionid
            AND a.user_id=iv_servid
            AND a.DISCNT_CODE=b.DISCNT_CODE
          AND a.start_date < iv_acycendtime
          AND (a.end_date >= iv_acycendtime-0.00001 OR a.end_date IS NULL);
    EXCEPTION
        WHEN OTHERS THEN
            iv_count1:=0;
    END;
    IF iv_count1 = 1 THEN
        IF iv_count2 = 0 THEN
            :TAG := 1 ;
        ELSE
            IF iv_count3 > 0 THEN
                :TAG:= 1;
            END IF;
        END IF;
    ELSE
        IF iv_count3 > 0 THEN
            :TAG:=1;
        END IF;
    END IF;
END IF;
END;