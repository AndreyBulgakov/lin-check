package com.devexperts.dxlab.lincheck.libtest;

/**
 * Created by alexander on 09.02.17.
 */
public class Account {

    int balance;

    public Account()
    {
        balance = 10;
    }

    public void withdraw(int n)
    {
        int r = read();
        synchronized (this){
            balance = r - n;
        }
    }

    public int read()
    {
        return balance;
    }

    public void deposit(int n)
    {
        synchronized (this){
            balance = balance + n;
        }
    }
}
