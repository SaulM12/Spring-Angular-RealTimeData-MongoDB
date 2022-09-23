import { ToastModule } from 'primeng/toast';
import { Component, OnInit } from '@angular/core';
import { Notification, NotificationSeverities } from '../model/notification';
import { NotificationService } from '../service/notification.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  notificationsMap = new Map<String, Notification[]>();
  private eventSource!: EventSource;
  NotificationSeverities = NotificationSeverities
  display: boolean = false;
  newNotification: Notification = {
    message: '',
    severity: 'INFO'
  }
  edit: boolean = false

  constructor(private notificationService: NotificationService, private messageService:MessageService) { }

  ngOnInit(): void {
    this.getAll()
    this.startEventListener()
  }
  ngOnDestroy(): void {
    this.stopEventListener()
  }
  handleDialog() {
    this.edit = false
    this.newNotification = {
      message: '',
      severity: 'INFO'
    }
    this.display = true
  }
  getAll() {
    for (const severity of this.getSeverities()) {
      this.notificationsMap.set(severity, [])
    }
    this.notificationService.findAll().subscribe(notificationList => {
      notificationList.forEach(notification => {
        this.notificationsMap.get(notification.severity)?.push(notification)
      })

    })
  }

  getSeverities(): Array<string> {
    return Object.keys(NotificationSeverities)
  }
  getSeveritiesText(key: string) {
    return NotificationSeverities[key as keyof typeof NotificationSeverities];
  }
  editDialog(notificaion: Notification) {
    this.newNotification = notificaion
    this.display = true
  }
  deleteNotification(id: string) {
    this.notificationService.delete(id).subscribe()
  }
  createNotification(notification: Notification) {
    this.notificationService.addNotification(notification).subscribe(() => {
      this.display = false
    })
  }
  updateNotification(notification: Notification, severity: string) {
    notification.severity = severity
    this.notificationService.updateNotification(notification).subscribe()
  }
  private onNotificationSaved(event: any) {
    this.messageService.add({severity:'info', summary: 'Aviso', detail: 'Nuevos cambios'})
    const notification = event.notification
    this.removeNotification(notification.id)
    this.notificationsMap.get(notification.severity)?.push(notification)
  }
  private removeNotification(id: string) {
    for (const notifications of this.notificationsMap.values()) {
      const index = notifications.map(n => n.id).indexOf(id)
      if (index >= 0) {
        notifications.splice(index, 1)
      }
    }
  }
  private onNotificationDeleted(event: any) {
    this.removeNotification(event.id)
  }
  private startEventListener() {
    this.eventSource = this.notificationService.listenToEvents(
      (e) => this.onNotificationSaved(e),
      (e) => this.onNotificationDeleted(e)
    )
  }
  private stopEventListener() {
    this.eventSource.close()
  }
}
