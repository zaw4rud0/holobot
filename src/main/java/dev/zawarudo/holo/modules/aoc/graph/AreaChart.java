package dev.zawarudo.holo.modules.aoc.graph;

import dev.zawarudo.holo.modules.aoc.data.AdventDay;

import java.awt.image.BufferedImage;
import java.util.List;

public class AreaChart extends AdventOfCodeGraph {

    public AreaChart(int year, int leaderboardId, String sessionKey) {
        super(year, leaderboardId, sessionKey);
    }

    @Override
    protected BufferedImage generateChart(List<AdventDay> days) {
        throw new UnsupportedOperationException("This chart is not implemented yet!");
    }
}