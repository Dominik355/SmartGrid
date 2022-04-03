package com.dominikbilik.smartgrid.datainput.saga.objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class SagaInstance {

    @Id
    private Long id;
    private String sagaType;
    private LocalDateTime creationTime;
    private LocalDateTime finishTime;
    private LocalDateTime errorTime;
    private String endState;
    private Boolean completed;

}
