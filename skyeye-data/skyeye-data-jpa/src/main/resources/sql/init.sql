drop database if exists `monitor-center`;
create database `monitor-center`;
use `monitor-center`;

--
-- table structure for table `app_info`
--

drop table if exists `app_info`;
create table `app_info` (
  `app` varchar(255) not null,
  `host` varchar(255) not null,
  `type` int(11) not null,
  `deploy` varchar(255) not null,
  `status` varchar(255) not null,
  primary key (`app`,`host`,`type`)
) engine=innodb default charset=utf8;

--
-- Table structure for table `name_info`
--

DROP TABLE IF EXISTS `name_info`;
CREATE TABLE `name_info` (
  `name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `app` varchar(255) NOT NULL,
  `tid` int(11) NOT NULL,
  PRIMARY KEY (`name`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `service_info`
--

DROP TABLE IF EXISTS `service_info`;
CREATE TABLE `service_info` (
  `iface` varchar(255) NOT NULL,
  `method` varchar(255) NOT NULL,
  `sid` varchar(255) NOT NULL,
  PRIMARY KEY (`iface`, `method`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `monitor_template`
--

DROP TABLE IF EXISTS `monitor_template`;
CREATE TABLE `monitor_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `window` int(11) NOT NULL,
  `threshold` double NOT NULL,
  `cost` varchar(255) NOT NULL,
  `preset` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Data for table `monitor_template`
--
INSERT INTO `monitor_template` VALUES (1, 'api报警预置模板', 1, 0.1, '3500', 1);
INSERT INTO `monitor_template` VALUES (2, '第三方报警预置模板', 1, 0.1, '3000', 1);
INSERT INTO `monitor_template` VALUES (3, 'middleware报警预置模板', 1, 0.1, '1000', 1);
