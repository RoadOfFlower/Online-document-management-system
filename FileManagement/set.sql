DROP DATABASE IF EXISTS filemanagement;

CREATE DATABASE filemanagement;

USE filemanagement;

CREATE TABLE `user`
(
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255),
    `tel` VARCHAR(20)
);

CREATE TABLE `file` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `user_id` INT NOT NULL,
                        `filename` VARCHAR(255) NOT NULL,
                        `filepath` VARCHAR(1000) NOT NULL,
                        `filetype` VARCHAR(255)
);

CREATE TABLE `log` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `user_id` INT NOT NULL,
                        `content` VARCHAR(1255) NOT NULL,
                        `time` VARCHAR(255) NOT NULL
);