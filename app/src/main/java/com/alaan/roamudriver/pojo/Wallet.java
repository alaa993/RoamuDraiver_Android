package com.alaan.roamudriver.pojo;

import java.io.Serializable;

public class Wallet implements Serializable {
    public String amount;
    public String typeTName;
    public String statusName;
    public String name_send;
    public String name_recive;
    public String id_transaction;
    public String transaction_type_id;
    public String transaction_date;

    public String id;
    public String balance;



    public Wallet()
    {
    }

    public Wallet(String id, String balance)
    {
        this.id = id;
        this.balance = balance;
    }

    public Wallet(String amount, String typeTName, String statusName, String id_transaction, String transaction_type_id)
    {
        this.amount = amount;
        this.typeTName = typeTName;
        this.statusName = statusName;
        this.id_transaction = id_transaction;
        this.transaction_type_id = transaction_type_id;
    }

    public Wallet(String amount, String typeTName, String statusName, String name_send, String name_recive, String id_transaction, String transaction_date)
    {
        this.amount = amount;
        this.typeTName = typeTName;
        this.statusName = statusName;
        this.name_send = name_send;
        this.name_recive = name_recive;
        this.id_transaction = id_transaction;
        this.transaction_date = transaction_date;
    }
}