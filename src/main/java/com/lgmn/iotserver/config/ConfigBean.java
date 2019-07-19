package com.lgmn.iotserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "config")
@PropertySource("classpath:config.properties")
@Component
public class ConfigBean {

  // 端口号
  public Integer serverPort;
  // boss线程
  public Integer bossCount;
  // worker线程
  public Integer workerCount;
  // 是否active
  public  boolean keepalive;
  public  Integer backlog;
  // 心跳周期，实际为发帧间隔，单位为 秒
  public  Integer heartbeat;
  // 定时器延时启动时间，单位为 秒
  public  Integer delay;
  // 采集周期 单位为 分钟，与数据库中周期单位对应
  public  Integer readPeriod;

  public  Integer getServerPort() {
    return serverPort;
  }

  public  void setServerPort(Integer serverPort) {
    this.serverPort = serverPort;
  }

  public  Integer getBossCount() {
    return bossCount;
  }

  public  void setBossCount(Integer bossCount) {
    this.bossCount = bossCount;
  }

  public  Integer getWorkerCount() {
    return workerCount;
  }

  public  void setWorkerCount(Integer workerCount) {
    this.workerCount = workerCount;
  }

  public  boolean isKeepalive() {
    return keepalive;
  }

  public  void setKeepalive(boolean keepalive) {
    this.keepalive = keepalive;
  }

  public  Integer getBacklog() {
    return backlog;
  }

  public  void setBacklog(Integer backlog) {
    this.backlog = backlog;
  }

  public  Integer getHeartbeat() {
    return heartbeat;
  }

  public  void setHeartbeat(Integer heartbeat) {
    this.heartbeat = heartbeat;
  }

  public  Integer getDelay() {
    return delay;
  }

  public  void setDelay(Integer delay) {
    this.delay = delay;
  }

  public  Integer getReadPeriod() {
    return readPeriod;
  }

  public  void setReadPeriod(Integer readPeriod) {
    this.readPeriod = readPeriod;
  }

}
