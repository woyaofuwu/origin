--IS_CACHE=N
SELECT SIGN((SELECT D.PARA_CODE2
               FROM TD_S_COMMPARA D
              WHERE D.SUBSYS_CODE = 'CSM'
                AND D.PARAM_ATTR = '8888'
                AND D.PARAM_CODE = '1'
                AND D.EPARCHY_CODE = '0898'
                AND SYSDATE BETWEEN D.START_DATE AND D.END_DATE
                AND D.PARA_CODE1 = :DISCNT_CODE) - (SELECT D.PARA_CODE2
               FROM TD_S_COMMPARA D
              WHERE D.SUBSYS_CODE = 'CSM'
                AND D.PARAM_ATTR = '8888'
                AND D.PARAM_CODE = '1'
                AND D.EPARCHY_CODE = '0898'
                AND SYSDATE BETWEEN D.START_DATE AND D.END_DATE
                AND D.PARA_CODE3 = :OPER_CODE)) 是否升舱
              FROM DUAL