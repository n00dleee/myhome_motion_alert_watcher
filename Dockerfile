FROM java:8
WORKDIR /
ADD motion_alert_watcher.main.jar motion_alert_watcher.main.jar
EXPOSE 8002
CMD java - jar motion_alert_watcher.main.jar
