package com.souldev.NotificationsApp.controller;

import com.souldev.NotificationsApp.event.Event;
import com.souldev.NotificationsApp.model.Notification;
import com.souldev.NotificationsApp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;


@RestController
@RequestMapping("/notification")
@CrossOrigin("http://localhost:4200")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping
    public Flux<Notification> getAll(){
        return notificationService.getAll();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Notification> create(@RequestBody Notification notification){
        return notificationService.save(notification);
    }
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Notification> update(@RequestBody Notification notification){
        return notificationService.updateSeverity(notification, notification.getSeverity());
    }
    @DeleteMapping("/delete/{id}")
    public Mono<Void> delete(@PathVariable final String id){
        return notificationService.deleteById(id);
    }
    @GetMapping("/events")
    public Flux<ServerSentEvent<Event>> getEventStream(){
        return notificationService.listenToEvents()
                .map(event -> ServerSentEvent.<Event>builder()
                        .retry(Duration.ofSeconds(5))
                        .event(event.getClass().getSimpleName())
                        .data(event).build());
    }
}
