package com.souldev.NotificationsApp.service;

import com.mongodb.client.model.changestream.OperationType;
import com.souldev.NotificationsApp.event.Event;
import com.souldev.NotificationsApp.event.NotificationDeleted;
import com.souldev.NotificationsApp.event.NotificationSaved;
import com.souldev.NotificationsApp.model.Notification;
import com.souldev.NotificationsApp.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.bson.BsonObjectId;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<Notification> getAll(){
        return notificationRepository.findAll();
    }
    public Mono<Notification> save(Notification notification){
        return notificationRepository.save(notification);
    }
    public Mono<Notification> findById(String id){
        return notificationRepository.findById(id);
    }
    public Mono<Notification> updateSeverity(Notification notification, String severity){
        return findById(notification.getId()).flatMap(notification1 -> {
            notification1.setSeverity(severity);
            return notificationRepository.save(notification1);
        });
    }
    public Mono<Void> deleteById(String id){
        return findById(id).flatMap(notificationRepository::delete);
    }

    public Notification toResource(Notification notification){
        return notification;
    }

    public Flux<Event> listenToEvents(){
        final ChangeStreamOptions changeStreamOptions = ChangeStreamOptions.builder()
                .returnFullDocumentOnUpdate()
                .filter(Aggregation.newAggregation(
                        Aggregation.match(
                                Criteria.where("operationType")
                                        .in(OperationType.INSERT.getValue(),
                                                OperationType.REPLACE.getValue(),
                                                OperationType.UPDATE.getValue(),
                                                OperationType.DELETE.getValue()
                                                )
                        )
                ))
                .build();
        return reactiveMongoTemplate.changeStream("notifications", changeStreamOptions, Notification.class)
                .map(this::toEvent);
    }

    public Event toEvent(final ChangeStreamEvent<Notification> changeStreamEvent){
        final Event event;
        switch (changeStreamEvent.getOperationType()){
            case DELETE:
                event = new NotificationDeleted().setId(((BsonObjectId) changeStreamEvent.getRaw()
                        .getDocumentKey().get("_id")).getValue().toString());
                break;
            case INSERT:
            case UPDATE:
            case REPLACE:
                event = new NotificationSaved().setNotification(toResource(changeStreamEvent.getBody()));
                break;
            default:
                throw new UnsupportedOperationException(
                        String.valueOf(changeStreamEvent.getOperationType())
                );
        }
        return event;
    }
}
