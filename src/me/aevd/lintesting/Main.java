package me.aevd.lintesting;

import me.aevd.lintesting.transfer.AccountsSynchronized;
import me.aevd.lintesting.util.Caller;

public class Main {
    public static void main(String[] args) {
        Caller caller = new AccountsCaller(AccountsSynchronized.class);
        Tester tester = new Tester(caller);
        System.out.println(tester.check());
    }
}

