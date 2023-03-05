package ru.shcherbatykh.Backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_history")
@Getter
@Setter
@NoArgsConstructor
public class EventHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private EventType eventType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;
    @Column(name = "time")
    @CreationTimestamp
    private LocalDateTime time;

    public EventHistory(EventType eventType, Request request) {
        this.eventType = eventType;
        this.request = request;
    }
}
