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
  PRIMARY KEY (`name`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- trace 主机信息
drop table if exists trace_host_info;
create table trace_host_info
(
    mac varchar(128) primary key not null,
    host_name varchar(32) not null,
    host_id int(11) not null auto_increment,
    remark varchar(256) default '' not null
);
create unique index trace_host_info_host_id_uindex on trace_host_info (host_id);
create unique index trace_host_info_mac_uindex on trace_host_info (mac);

-- trace 项目信息
drop table if exists trace_project_info;
create table trace_project_info
(
    project_name varchar(128) primary key not null,
    project_id int(11) not null auto_increment,
    remark varchar(256) default '' not null
);
create unique index trace_project_info_project_id_uindex on trace_project_info (project_id);
create unique index trace_project_info_project_name_uindex on trace_project_info (project_name);

-- trace 项目实例信息
drop table if exists trace_project_instance_info;
create table trace_project_instance_info
(
    host_name varchar(128) default '' not null,
    host_mac varchar(128) default '' not null,
    project_name varchar(64) default '' not null,
    instance_path varchar(256) default '' not null,
    host_id int(11) default '-1' not null,
    project_id int(11) default '-1' not null,
    instance_id int(11) default '-1' not null,
    remark varchar(256) default '' not null,
    constraint `primary` primary key (host_mac, project_name, instance_path)
);