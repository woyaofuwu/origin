SELECT to_char(add_months(SYSDATE, 0), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +1), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +2), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +3), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +4), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +5), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +6), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +7), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +8), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +9), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +10), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(add_months(SYSDATE, +11), 'yyyymm') bcyc_id  FROM dual UNION ALL
SELECT to_char(to_date('205012','yyyyMM'),'yyyyMM') bcyc_id  FROM dual