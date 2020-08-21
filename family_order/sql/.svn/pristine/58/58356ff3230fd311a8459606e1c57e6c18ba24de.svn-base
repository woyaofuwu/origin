SELECT COUNT(1) recordcount
FROM dual
WHERE (
          SELECT NVL(AVG(NVL(FEE/100,0)),0)
          FROM tf_o_credit_userbill
          WHERE acyc_id BETWEEN
                              (
                                  SELECT DECODE(SIGN(TO_NUMBER(:MONTHS)),1,acyc_id-TO_NUMBER(:MONTHS),0)
                              	  FROM td_a_acycpara
                              	  WHERE acyc_start_time <= SYSDATE AND SYSDATE < acyc_end_time
                              )
          AND
              (
                  SELECT acyc_id-1
              	  FROM td_a_acycpara
              	  WHERE acyc_start_time <= SYSDATE AND SYSDATE < acyc_end_time
              )
          AND user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000))<:NUM