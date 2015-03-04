package me.aevd.lintesting;

import me.aevd.lintesting.counter.CounterSynchronized;
import me.aevd.lintesting.transfer.Accounts;
import me.aevd.lintesting.transfer.AccountsSynchronized;

public class Main {
    public static void main(String[] args) {
        AccountsCaller caller = new AccountsCaller(AccountsSynchronized.class);
        AccountsTester tester = new AccountsTester(caller);
        tester.test();
    }
}

