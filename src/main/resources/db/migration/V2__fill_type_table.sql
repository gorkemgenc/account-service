/*
 * Server: MySQl
 * Version: V1_1
 * Description: Fill process_type table with Deposit, Witdraw, Purchase
*/
INSERT INTO process_type (description)
SELECT * FROM (SELECT 'DEPOSIT') AS tmp WHERE NOT EXISTS (
    SELECT description FROM process_type WHERE description='DEPOSIT')
LIMIT 1;

INSERT INTO process_type (description)
SELECT * FROM (SELECT 'WITHDRAW') AS tmp WHERE NOT EXISTS (
    SELECT description FROM process_type WHERE description='WITHDRAW')
LIMIT 1;

INSERT INTO process_type (description)
SELECT * FROM (SELECT 'PURCHASE') AS tmp WHERE NOT EXISTS (
    SELECT description FROM process_type WHERE description='PURCHASE')
LIMIT 1;

