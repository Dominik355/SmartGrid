package com.dominikbilik.smartgrid.datainput.saga.objects;

import java.time.LocalDateTime;
import java.util.List;

public class SagaInstance {

    private Long id;
    private String sagaType;
    private LocalDateTime creationTime;
    private LocalDateTime finishTime;
    private LocalDateTime errorTime;
    private String endState;
    private Boolean completed;



}
