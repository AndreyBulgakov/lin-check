package me.aevd.lintesting.util;

import java.util.Random;

public class ActorGenerator {
    private int methodId;
    private String name;
    private Interval[] rangeArgs;

    public ActorGenerator(int methodId, String name, Interval... rangeArgs) {
        this.methodId = methodId;
        this.name = name;
        this.rangeArgs = rangeArgs;
    }

    private static int intFromInterval(Random r, Interval iv) {
        return r.nextInt(iv.to - iv.from) + iv.from;
    }

    public Actor generate(int indActor) {
        Random rand = new Random();

        Integer[] args = new Integer[rangeArgs.length];
        for (int i = 0; i < rangeArgs.length; i++) {
            args[i] = intFromInterval(rand, rangeArgs[i]);
        }

        Actor act = new Actor(indActor, methodId, args);
        act.methodName = name;
        return act;
    }
}
