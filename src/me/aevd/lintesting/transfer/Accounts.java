package me.aevd.lintesting.transfer;

public interface Accounts {
    public Integer getAmount(int id);
    public void setAmount(int id, int value);
    public void transfer(int id1, int id2, int value);
}
