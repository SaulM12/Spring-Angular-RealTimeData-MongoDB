import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs';
import { Notification } from '../model/notification';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  baseURL: string = 'http://localhost:8080/notification'

  constructor(private httpClient: HttpClient) { }

  findAll(): Observable<Notification[]> {
    return this.httpClient.get<Notification[]>(this.baseURL);
  }
  addNotification(notification: Notification): Observable<Notification> {
    return this.httpClient.post<Notification>(this.baseURL, notification);
  }
  updateNotification(notification: Notification): Observable<Notification> {
    return this.httpClient.put<Notification>(this.baseURL, notification);
  }
  delete(id: string): Observable<any> {
    return this.httpClient.delete<void>(this.baseURL + '/delete/' + id);
  }
  listenToEvents(onSaved: (event: any) => void, onDeleted: (event: any) => void): EventSource {
    const eventSource = new EventSource(this.baseURL + "/events")
    eventSource.addEventListener('NotificationSaved', (event: MessageEvent) => {
      onSaved(JSON.parse(event.data))
    })
    eventSource.addEventListener('NotificationDeleted', (event: MessageEvent) => {
      onDeleted(JSON.parse(event.data))
    })
    eventSource.onerror = (error)=>{
      console.log(error)
    }
    return eventSource;
  }
}
