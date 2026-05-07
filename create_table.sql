-- 1. 创建数据库、选用数据库
DROP DATABASE IF EXISTS fund_management;
CREATE DATABASE fund_management DEFAULT CHARACTER SET utf8mb4;
USE fund_management;

-- 2. 建表
-- 用户表
CREATE TABLE user (
  user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(64) NOT NULL COMMENT '密码',
  email VARCHAR(64) COMMENT '邮箱',
  income_total DECIMAL(10,2) DEFAULT 0 COMMENT '累计总收入',
  budget_food DECIMAL(10,2) DEFAULT 0 COMMENT '餐饮类预算',
  budget_non_food DECIMAL(10,2) DEFAULT 0 COMMENT '非餐饮类预算'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 资金流水表
CREATE TABLE transaction (
  trans_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '流水ID',
  user_id INT NOT NULL COMMENT '用户ID',
  trans_type ENUM('收入','支出') NOT NULL COMMENT '交易类型',
  amount DECIMAL(10,2) NOT NULL COMMENT '金额',
  category VARCHAR(32) NOT NULL COMMENT '分类',
  trans_date DATE NOT NULL COMMENT '交易日期',
  remark VARCHAR(255) COMMENT '备注',
  FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金流水记录表';

-- 管理员表
CREATE TABLE admin (
  admin_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
  admin_name VARCHAR(32) NOT NULL UNIQUE COMMENT '管理员账号',
  password VARCHAR(64) NOT NULL COMMENT '密码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员信息表';

-- ======================
-- 3. 基础增删查改语句模板（无实际测试数据，仅留语句结构）
-- ======================

-- ---------- 用户表 CRUD ----------
-- 增
INSERT INTO user(username,password,email,income_total,budget_food,budget_non_food) 
VALUES (?,?,?,?,?,?);

-- 查
SELECT * FROM user;
SELECT * FROM user WHERE user_id = ?;
SELECT * FROM user WHERE username = ?;

-- 改
UPDATE user SET password=?,email=?,income_total=?,budget_food=?,budget_non_food=? WHERE user_id=?;

-- 删
DELETE FROM user WHERE user_id = ?;

-- ---------- 流水表 CRUD ----------
INSERT INTO transaction(user_id,trans_type,amount,category,trans_date,remark)
VALUES (?,?,?,?,?,?);

SELECT * FROM transaction;
SELECT * FROM transaction WHERE user_id = ?;

UPDATE transaction SET trans_type=?,amount=?,category=?,trans_date=?,remark=? WHERE trans_id=?;

DELETE FROM transaction WHERE trans_id = ?;

-- ---------- 管理员表 CRUD ----------
INSERT INTO admin(admin_name,password) VALUES (?,?);

SELECT * FROM admin;
SELECT * FROM admin WHERE admin_name = ?;

UPDATE admin SET password=? WHERE admin_id=?;

DELETE FROM admin WHERE admin_id = ?;