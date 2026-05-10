-- 1. 创建数据库、选用数据库
DROP DATABASE IF EXISTS fund_management;
CREATE DATABASE fund_management DEFAULT CHARACTER SET utf8mb4;
USE fund_management;

-- 2. 建表
-- 用户表（5个字段：用户账号id、用户名、密码、邮箱、月度总预算）
CREATE TABLE user (
  user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户账号id',
  username VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(64) NOT NULL COMMENT '密码',
  email VARCHAR(64) COMMENT '邮箱',
  month_budget DECIMAL(10,2) DEFAULT 0 COMMENT '月度总预算'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 资金流水表（流水id、用户id、金额、日期、交易类型：餐饮/水电/交通/生活用品/工资/其他）
CREATE TABLE transaction (
  trans_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '流水收支id',
  user_id INT NOT NULL COMMENT '用户ID',
  amount DECIMAL(10,2) NOT NULL COMMENT '金额',
  trans_date DATE NOT NULL COMMENT '日期',
  trans_type VARCHAR(20) NOT NULL COMMENT '交易类型：餐饮、水电、交通、生活用品、工资、其他',
  FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金流水记录表';

-- 管理员表（4个字段：管理员id、管理员名、密码、邮箱）
CREATE TABLE admin (
  admin_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员账号id',
  admin_name VARCHAR(32) NOT NULL UNIQUE COMMENT '管理员名',
  password VARCHAR(64) NOT NULL COMMENT '管理员密码',
  email VARCHAR(64) COMMENT '邮箱'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员信息表';

-- ======================
-- 3. 基础增删查改语句模板（无测试数据）
-- ======================

-- ---------- 用户表 CRUD ----------
-- 增
INSERT INTO user(username,password,email,month_budget) 
VALUES (?,?,?,?);

-- 查
SELECT * FROM user;
SELECT * FROM user WHERE user_id = ?;
SELECT * FROM user WHERE username = ?;

-- 改（只改ID和密码，符合你要求）
UPDATE user SET user_id=?, password=? WHERE user_id=?;

-- 删
DELETE FROM user WHERE user_id = ?;

-- ---------- 流水表 CRUD ----------
INSERT INTO transaction(user_id,amount,trans_date,trans_type)
VALUES (?,?,?,?);

SELECT * FROM transaction;
SELECT * FROM transaction WHERE user_id = ?;
SELECT * FROM transaction WHERE trans_type = ?;
SELECT * FROM transaction WHERE user_id = ? AND trans_type = ?;

UPDATE transaction SET amount=?,trans_date=?,trans_type=? WHERE trans_id=?;

DELETE FROM transaction WHERE trans_id = ?;

-- ---------- 管理员表 CRUD ----------
INSERT INTO admin(admin_name,password,email) VALUES (?,?,?);

SELECT * FROM admin;
SELECT * FROM admin WHERE admin_id = ?;
SELECT * FROM admin WHERE admin_name = ?;

UPDATE admin SET password=? WHERE admin_id=?;

DELETE FROM admin WHERE admin_id = ?;