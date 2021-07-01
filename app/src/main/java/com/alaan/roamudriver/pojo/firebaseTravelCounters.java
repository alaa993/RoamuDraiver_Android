package com.alaan.roamudriver.pojo;

public class firebaseTravelCounters {
    //    public Long PENDING;
    public Long ACCEPTED;
    public Long COMPLETED;
    //    public Long CANCELLED;
    public Long OFFLINE;
    public Long PAID;

    public firebaseTravelCounters() {
        this.ACCEPTED = Long.valueOf(0);
        this.COMPLETED = Long.valueOf(0);
        this.OFFLINE = Long.valueOf(0);
        this.PAID = Long.valueOf(0);
    }

    public firebaseTravelCounters(Long ACCEPTED, Long COMPLETED, Long OFFLINE, Long PAID) {
//        this.PENDING = PENDING;
        this.ACCEPTED = ACCEPTED;
        this.COMPLETED = COMPLETED;
//        this.CANCELLED = CANCELLED;
        this.OFFLINE = OFFLINE;
        this.PAID = PAID;
    }
}
