package com.github.eventplanningsystem.report;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.github.eventplanningsystem.event.Event;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event; // Мероприятие, для которого генерируется отчет

    private int attendeesCount; // Количество участников
    private int declinedCount; // Количество отказов

    private LocalDateTime generatedAt; // Дата и время генерации отчета
}
